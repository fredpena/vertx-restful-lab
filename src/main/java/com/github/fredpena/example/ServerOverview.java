/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * @author Fred Pena fantpena@gmail.com
 * <p>
 * Uno de los componentes core de Vert.x Web es el Route. Este componente se
 * encarga de tener registradas las rutas a las que nuestra aplicación va a
 * responder, para cuando reciba una petición sobre una de ellas llamar a su
 * manejado asociado.
 * <p>
 */
public class ServerOverview extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerOverview.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /**
         * En este ejemplo podemos ver como en el router se ha registrado la
         * ruta /hello y en su manejador (handler), que recibe un objeto del tipo
         * RoutingContext, le añadimos una cabecera y respondemos al cliente con
         * el mensaje Hello Vert.x!. Importante la llamada a end para que la
         * respuesta se envíe al cliente.
         *
         * Endpoint:
         *
         * http://localhost:8080/hello
         */
        router.route("/hello")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:/hello - HTTP GET request");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Hello Vert.x!");
                });

        server.requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        LOG.info("vertx-restful-lab: Deploy ServerOverview Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerOverview());
    }

}
