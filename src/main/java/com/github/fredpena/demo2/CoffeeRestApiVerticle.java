/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.demo2;

import com.github.fredpena.utils.Coffee;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.UUID;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class CoffeeRestApiVerticle extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(CoffeeRestApiVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = config();

        String id = UUID.randomUUID().toString();

        int port = config.getInteger("http.port", 8085);
        LOG.info("Deploying ServerVerticle on port {} - Instances ID: {}", port, id);
        
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post("/coffee")
                .produces("application/json")
                .handler(this::processCoffeeOrder);

        server.requestHandler(router)
                .listen(port, ar -> {
                    if (ar.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });

    }

    private void processCoffeeOrder(RoutingContext routingContext) {
        Coffee coffee = new Coffee(routingContext.getBodyAsJson());
        LOG.info("Processing request... {}\n", coffee);

        EventBus eb = vertx.eventBus();

        JsonObject message = new JsonObject();
        String correlationId = UUID.randomUUID().toString();
        message.put("id", correlationId);
        message.put("customer", coffee.getCustomer());
        message.put("type", coffee.getType());
        message.put("size", coffee.getSize());

        LOG.info("Order {} Sent! For Customer: {}, Coffee: {}, Size: {}\n", correlationId, coffee.getCustomer(), coffee.getType(), coffee.getSize());
        eb.send("process.coffee.order", message, ar -> {

            if (ar.succeeded()) {
                routingContext.response()
                        .setStatusCode(200)
                        .setChunked(true)
                        .putHeader("Content-Type", "application/json")
                        .end(coffee.toJson()
                                .put("id", correlationId)
                                .put("order", "ok")
                                .encode());
            } else {
                routingContext.response()
                        .setStatusCode(400)
                        .setChunked(true)
                        .putHeader("Content-Type", "application/json")
                        .end(new JsonObject()
                                .put("message", ar.cause().getMessage())
                                .put("order", "error")
                                .encode());
            }
        });

        //  LOG.info("Order {} Sent! For Customer: {}, Coffee: {}, Size: {}\n", correlationId, coffee.getCustomer(), coffee.getType(), coffee.getSize());
        //message
    }

}
