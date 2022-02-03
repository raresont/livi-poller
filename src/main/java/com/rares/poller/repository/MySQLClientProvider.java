package com.rares.poller.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

import java.util.logging.Logger;

import static io.vertx.core.Promise.promise;

public class MySQLClientProvider {
  private final static Logger LOGGER = Logger.getLogger(MySQLClientProvider.class.getName());
  //load this from config in the future or inject them with a tool like Vault
  private MySQLConnectOptions connectOptions = new MySQLConnectOptions()
    .setPort(3309)
    .setHost("localhost")
    .setDatabase("dev")
    .setUser("dev")
    .setPassword("secret");
  private PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
  private MySQLPool client;

  private MySQLClientProvider(){}

  MySQLClientProvider(Vertx vertx) {
    LOGGER.info("Setting the MySQL client");
    client = MySQLPool.pool(vertx, connectOptions, poolOptions);
    prepareServiceTable();
  }

  private void prepareServiceTable(){
    LOGGER.info("Being sure that we have a Service table");
    //this is simplified, replacing with a library that also check if the structure is the same would be helpful :)
    final String serviceTableCreation = "CREATE TABLE IF NOT EXISTS service (name VARCHAR(64) NOT NULL PRIMARY KEY, URL VARCHAR(255) NOT NULL,status tinyint DEFAULT 0 , lastUpdate VARCHAR(64))";
    executeQuery(serviceTableCreation, null);
  }

  public Future<RowSet<Row>> executeQuery(String query, Tuple tuple) {
    Promise<RowSet<Row>> promiseData = promise();

    this.client.getConnection().compose(conn -> {
      LOGGER.info("Got a connection from the pool");

      PreparedQuery<RowSet<Row>> preparedQuery = conn.preparedQuery(query);
      Future<RowSet<Row>> rowSetFuture;

      if(tuple == null) {
        rowSetFuture = preparedQuery.execute();
      } else {
        rowSetFuture = preparedQuery.execute(tuple);
      }

      return rowSetFuture
        .onComplete(ar -> {
          conn.close();
        }).onFailure(ar -> {
          conn.close();
        });
    }).onComplete(ar -> {
      if (ar.succeeded()) {
        LOGGER.info("Query executed with success.");
        promiseData.complete(ar.result());
      } else {
        promiseData.fail(ar.cause());
        LOGGER.info("Something went wrong " + ar.cause().getMessage());
      }
    });
    return promiseData.future();
  }
  public void closeConnection(){
    client.close();
  }
}
