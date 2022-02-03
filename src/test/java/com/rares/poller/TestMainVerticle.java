package com.rares.poller;

import com.rares.poller.model.Service;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  private Service testLiviService;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    testLiviService = new Service("livi","https://kry.health/");
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  @Timeout(value = 20, unit = TimeUnit.SECONDS)
  void test_service_live(Vertx vertx, VertxTestContext testContext) throws Throwable {
    ExternalCaller externalCaller = new ExternalCaller(vertx);
    externalCaller.get_url_status(vertx, testLiviService.getURL()).onComplete(ar -> {
      assertEquals(ar.result().intValue(),200);
      testContext.completeNow();
    });
  }

}
