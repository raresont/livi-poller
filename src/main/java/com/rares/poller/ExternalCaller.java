package com.rares.poller;

import com.rares.poller.repository.MySQLClientProvider;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.logging.Logger;

import static io.vertx.core.Promise.promise;

public class ExternalCaller {
  private final static Logger LOGGER = Logger.getLogger(ExternalCaller.class.getName());

  private WebClient webClient;

  private ExternalCaller(){}
  public ExternalCaller(Vertx vertx){
    webClient = WebClient.create(vertx);
  }

  //tried to make static, but it is not efficient
  public Future<Integer> get_url_status(Vertx vertx, String url){
    Promise<Integer> responded = promise();
    webClient
      .getAbs( url)
      .ssl(true)
      .putHeader("Accept", "*/*")
      .expect(ResponsePredicate.SC_OK)
      .send(ar -> {
        if (ar.succeeded()) {
          responded.complete(ar.result().statusCode());
        }else {
          LOGGER.info("Calling URL did not worked " + ar.cause().getMessage());
        }
      });

    return responded.future();
  }
}
