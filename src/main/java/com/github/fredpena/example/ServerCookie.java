/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example;

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
 *
 * Manejo de cookies
 *
 * Vert.x proporciona una forma sencilla para manejar las cookies. Al igual que
 * se hace para para los datos en el cuerpo de la petición, para poder acceder a
 * las cookies tenemos que activar un handler. Este handler se llama
 * CookieHandler y lo activaremos de la siguiente forma:
 *
 * EXAMPLE: 6
 */
public class ServerCookie extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerCookie.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /**
         * podemos obtener una cookie por su nombre con getCookie u obtener
         * todas ellas con cookieMap. El RoutingContext nos proporciona dos
         * métodos, uno para añadir una cookie nueva addCookie y otro para
         * eliminarlas removeCookie
         */
        router.get("/comment")
                .handler(routingContext -> {
                    Cookie someCookie = routingContext.getCookie("visits");

                    // routingContext.removeCookie("visits");
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

                    routingContext.response().end("visits: " + visits);

                    // Agregue una cookie: esto se volverá a escribir en la respuesta automáticamente
                    routingContext.addCookie(Cookie.cookie("visits", "" + visits));

                    routingContext.next();
                });

        router.get("/comment")
                .handler(routingContext -> {
                    Cookie someCookie = routingContext.getCookie("visits");

                    if (someCookie == null) {
                        routingContext.response().setStatusCode(404);
                        routingContext.response().end("Not Found");
                    } else {
                        routingContext.response().end("visits: " + someCookie.getValue());
                    }

                });

        server.requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        LOG.info("vertx-restful-lab: Deploy ServerCookie Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerCookie());
    }
}
