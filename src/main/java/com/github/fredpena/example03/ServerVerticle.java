/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example03;

import com.github.fredpena.utils.Coffee;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class ServerVerticle extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = config();

        String id = UUID.randomUUID().toString();

        int port = config.getInteger("http.port", 8085);
        LOG.info("Deploying ServerVerticle on port {} - Instances ID: {}", port, id);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        allowHeaders.add("Authorization");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.PUT);
        allowMethods.add(HttpMethod.OPTIONS);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        router.post("/coffee")
                .produces("application/json")
                .handler(this::processCoffeeOrder);

        server.requestHandler(router::accept)
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

        eb.send("process.coffee.order", message);

        LOG.info("Order {} Sent! For Customer: {}, Coffee: {}, Size: {}\n", correlationId, coffee.getCustomer(), coffee.getType(), coffee.getSize());

        routingContext.response()
                .setStatusCode(200)
                .setChunked(true)
                .putHeader("Content-Type", "application/json")
                .end(coffee.toJson()
                        .put("id", correlationId)
                        .put("order", "ok")
                        .encode());

        //message
    }

}
