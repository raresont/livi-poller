package com.rares.poller;

import com.rares.poller.repository.ServiceRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
  private final static Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());
  private ServiceRepository serviceRepository;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    serviceRepository = new ServiceRepository(vertx);

    PollerRouter router = new PollerRouter(vertx, serviceRepository);
    vertx.setPeriodic(3000, notUsedHandler -> serviceRepository.updateStatuses(vertx));
    vertx.createHttpServer().requestHandler(router.getRouter()).listen(8888, http -> {
      if (http.succeeded()) {
        //https://youtu.be/o02-ox30OR8?t=23
        LOGGER.fine("HERE WE COME!!");

        startPromise.complete();
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
