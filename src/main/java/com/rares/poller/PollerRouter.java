package com.rares.poller;

import com.rares.poller.model.Service;
import com.rares.poller.repository.ServiceRepository;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class PollerRouter {
  //using URI parameter/header value or HATEOAS is prepared for the next version ;)
  private final static String API_PATH ="/api/";
  private final static String VERSION = "v12/";
  private final static String API_RESOURCE = "/services";
  private final static Logger LOGGER = Logger.getLogger(PollerRouter.class.getName());
  private Router router;
  private Router apiRouter;

  private ServiceRepository serviceRepository;

  private PollerRouter(){};

  PollerRouter(Vertx vertx, ServiceRepository serviceRepository) {
    this.serviceRepository = serviceRepository;
    LOGGER.info("Building the router");

    router = Router.router(vertx);
    apiRouter = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    LOGGER.info("Setting the static content");
    //to be replaced by a node server for better performance and to help the front-end :)
    router.route().handler(StaticHandler.create().setCachingEnabled(false));

    LOGGER.info("Setting the API router");
    router.mountSubRouter(API_PATH + VERSION, apiRouter);

    LOGGER.info("Setting the API GET path");
    apiRouter.get(API_RESOURCE).handler(request -> {
      prepareForShipping(request, 200, String.valueOf(new JsonArray(serviceRepository.getServices())));
    });

    LOGGER.info("Setting the API POST path");
    apiRouter.post(API_RESOURCE).handler(request -> {
      JsonObject jsonBody = request.getBodyAsJson();
      if((jsonBody == null) || !isValidURL(jsonBody.getString("url"))) {
        LOGGER.info("A post was sent with empty body");
        prepareForShipping(request, 400,"");
        return;
      }

      Service tempService = new Service(jsonBody.getString("name"), jsonBody.getString("url"));
      serviceRepository.add(tempService);

      prepareForShipping(request, 201, "");
    });

    LOGGER.info("Setting the API DELETE path");
    apiRouter.delete(API_RESOURCE+"/:id").handler(request -> {
      if(request.request().getParam("id").equals(null)){
        prepareForShipping(request, 404, "");
        return;
      }
      serviceRepository.delete(request.request().getParam("id"));
      prepareForShipping(request, 204, "");
    });
  }

  private void prepareForShipping(RoutingContext routingContext, int statusCode, String whatToShip) {
    if(whatToShip == null) {
      whatToShip = "";
    }

    routingContext.
      response().
      setStatusCode(statusCode).
      putHeader("Content-Type", "application/json").
      end(whatToShip);
  }

  public Router getRouter() {
    return router;
  }

  //this should be moved to some util class
  private static boolean isValidURL(String url) {
    try {
      new URI(url).parseServerAuthority();
      return true;
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
