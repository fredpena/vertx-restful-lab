/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * @author Fred Pena fantpena@gmail.com
 * <p>
 * <p>
 * EXAMPLE: 6
 */
public class SessionHandlerExample extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(SessionHandlerExample.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        //router.route().handler(LoggerHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        router.route("/comment").handler(routingContext -> {

            Session session = routingContext.session();

            Integer cnt = session.get("hitcount");
            cnt = (cnt == null ? 0 : cnt) + 1;

            session.put("hitcount", cnt);

            routingContext.response().putHeader("content-type", "text/html")
                    .end("<html><body><h1>Hitcount: " + cnt + "</h1></body></html>");
        });

        server.requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        LOG.info("vertx-restful-lab: Deploy CookieHandler Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SessionHandlerExample());
    }
}
