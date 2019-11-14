/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * @author Fred Pena fantpena@gmail.com
 * <p>
 * <p>
 * EXAMPLE: 5
 */
public class CookieHandler extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(CookieHandler.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.post("/comment")
                .handler(routingContext -> {
                    Cookie someCookie = routingContext.getCookie("visits");

                    long visits = 0;
                    if (someCookie != null) {
                        String cookieValue = someCookie.getValue();
                        try {
                            visits = Long.parseLong(cookieValue);
                        } catch (NumberFormatException e) {
                            visits = 0l;
                        }
                    }
                    // incrementar el seguimiento
                    visits++;

                    // Agregue una cookie: esto se volverá a escribir en la respuesta automáticamente
                    routingContext.addCookie(Cookie.cookie("visits", "" + visits));

                    routingContext.next();
                });

        router.get("/comment")
                .handler(routingContext -> {
                    Cookie someCookie = routingContext.getCookie("visits");

                    routingContext.response().end("visits: " + someCookie.getValue());
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
        vertx.deployVerticle(new CookieHandler());
    }
}
