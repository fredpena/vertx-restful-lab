/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.publishSubscriber;

import com.github.fredpena.utils.Coffee;
import com.github.fredpena.utils.RandomCoffee;
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
        //Coffee coffee = new Coffee(routingContext.getBodyAsJson());
        RandomCoffee randomCoffee = new RandomCoffee();
        Coffee coffee = randomCoffee.getCoffee();
        LOG.info("Processing request... {}\n", coffee);

        EventBus eb = vertx.eventBus();

        JsonObject message = new JsonObject();
        String correlationId = UUID.randomUUID().toString();
        message.put("id", correlationId);
        message.put("customer", coffee.getCustomer());
        message.put("type", coffee.getType());
        message.put("size", coffee.getSize());

        eb.send("process.coffee.order", message);

        LOG.info("Order {} was Sent! For the Customer: {}, Coffee: {}, Size: {}\n", correlationId, coffee.getCustomer(), coffee.getType(), coffee.getSize());

        routingContext.response()
                .setStatusCode(200)
                .setChunked(true)
                .putHeader("Content-Type", "application/json")
                .end(coffee.toJson()
                        .put("message", String.format("Order %s was Sent! For the Customer: %s, Coffee: %s, Size: %s", correlationId, coffee.getCustomer(), coffee.getType(), coffee.getSize()))
                        .put("id", correlationId)
                        .put("order", "ok")
                        .encode());

        //message
    }

}
