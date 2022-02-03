package com.rares.poller.repository;

import com.rares.poller.ExternalCaller;
import com.rares.poller.model.Service;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

import static com.rares.poller.model.Service.*;
import static java.time.LocalDateTime.now;

public class ServiceRepository {
  private final static Logger LOGGER = Logger.getLogger(ServiceRepository.class.getName());
  private HashMap<String, Service> memoryLoadedServices = new HashMap<>();
  private MySQLClientProvider mySQLClientProvider;
  private LocalDateTime lastMemoryLoad;
  private static final int MINUTES_BEFORE_NEXT_LOAD = 5;
  private ExternalCaller externalCaller;

  public ServiceRepository(Vertx vertx){
      LOGGER.info("Initialiazing  the ServiceRepository");
      mySQLClientProvider = new MySQLClientProvider(vertx);


      //to be sure that we load at creation in memory without extra ifs
      lastMemoryLoad = now().minusMinutes(MINUTES_BEFORE_NEXT_LOAD*2);
      getServices();

      externalCaller = new ExternalCaller(vertx);
  }

  public List<Service> getServices() {
    if( now().isAfter(lastMemoryLoad.plusMinutes(MINUTES_BEFORE_NEXT_LOAD))) {
      LOGGER.info("Updating the memory loaded services");

      Future<RowSet<Row>> rowData = mySQLClientProvider.executeQuery("SELECT * FROM service", null);
      rowData.onComplete(ar -> {
        if(ar.succeeded()) {
          ar.result().forEach(this::addToMemoryLoadedMap);
          lastMemoryLoad = now();
        }
      });

    }

    return new ArrayList<>(memoryLoadedServices.values());
  }

  public ResponsePredicate delete(String id) {
    LOGGER.info("Deleting a service");

    if( mySQLClientProvider.executeQuery("DELETE FROM service WHERE name = ?",Tuple.of(id)).failed()) {
      return ResponsePredicate.SC_NOT_FOUND;
    }

    memoryLoadedServices.remove(id);
    return ResponsePredicate.SC_NO_CONTENT;
  }

  public ResponsePredicate add(Service service) {
    LOGGER.info("Adding a service");
    if( mySQLClientProvider.executeQuery("INSERT INTO service values(?, ?, ?, ?)",Tuple.of(service.getName(), service.getURL(), service.getStatus(),service.getLastUpdate())).failed()) {
      return ResponsePredicate.SC_SERVER_ERRORS;
    }
    memoryLoadedServices.put(service.getName(), service);
    return ResponsePredicate.SC_CREATED;
  }

  public ResponsePredicate updateStatus(Service service) {
    LOGGER.info("Trying to update the status");
    Service memoryService = memoryLoadedServices.get(service.getName());
    if(memoryService.getStatus() == service.getStatus()) {
      LOGGER.info("Status is the same");
      return ResponsePredicate.SC_SUCCESS;
    }
    //update the rest
    if( mySQLClientProvider.executeQuery("REPLACE INTO service values(?, ?, ?, ?)",Tuple.of(service.getName(), service.getURL(), service.getStatus(),service.getLastUpdate())).failed()) {
      return ResponsePredicate.SC_SERVER_ERRORS;
    }
    return ResponsePredicate.SC_CREATED;
  }

  public void updateStatuses(Vertx vertx) {
    LOGGER.info("Trying to update all statuses");
    memoryLoadedServices.forEach((name, service) -> {
      LOGGER.info("Getting status for " + name);
      Future<Integer> service_response = externalCaller.get_url_status(vertx, service.getURL());
      service_response.onComplete(ar-> {
          if (ar.result().intValue() == 200) {
            if (service.getStatus() == 1) {
              LOGGER.info("Status remains OK");
            } else {
              LOGGER.info("Status changed to OK");
              memoryLoadedServices.get(name).setStatus(1);
            }
          } else {
            if (service.getStatus() == 0) {
              LOGGER.info("Status remains FAIL");
            } else {
              LOGGER.info("Status changed to FAIL");
              memoryLoadedServices.get(name).setStatus(0);
            }
          }

        //for this I would add a schedule to be updted in db wayyy slower than it is updated here
        //we reduce the number of update writings but we still update date
        memoryLoadedServices.get(name).setLastUpdateToBeNow();
      });
    });
    //WAIT! Rares, are we updating the status in the db? Answer: No, we set the lastUpdate, which tell if our service works
    //properly. If the service called doesn't work then we need to check it's logs.
    //so we might drop the status column from db, it is good only for stats, but if we have a real time db for stats then...
  }

  private void addToMemoryLoadedMap(Row row) {
    final Service tempService =
      new Service(row.getString(nameColumn),
                  row.getString(urlColumn),
                  row.getInteger(statusColumn),
                  row.getString(lastUpdateColumn));
    memoryLoadedServices.put(tempService.getName(), tempService);
  }

}
