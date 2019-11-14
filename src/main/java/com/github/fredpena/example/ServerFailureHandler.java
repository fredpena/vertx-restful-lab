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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author Fred Pena fantpena@gmail.com
 * <p>
 *
 * Manejo de errores
 *
 * Por defecto, Vert.x proporciona un manejador de errores cuando se pide una
 * ruta que no se ha definido. Este manejador devuelve un código de estado 404
 * con el mensaje Resource not found. Para personalizarlo podemos hacer un
 * manejado que se ejecute el último de todos y personalizar nosotros el error
 * devuelto
 *
 * EXAMPLE: 5
 */
public class ServerFailureHandler extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerFailureHandler.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        /**
         * Creamos un route sin ningún tipo de ruta asociada y le indicamos que
         * queremos que se situe en la última posición de todas las rutas
         * registradas con el método last. Finalmente en su manejador ponemos el
         * código de estado 404 y contestamos la petición con el mensaje Not
         * Found.
         */
        router.route()
                .last()
                .handler(routingContext -> {
                    routingContext.response().setStatusCode(404);
                    routingContext.response().end("Not Found");
                });

        /**
         * Además de esta forma de dar solución a los errores 404, también
         * podemos manejar los errores que se producen dentro de cada handler
         * indicando que la route definida tiene un failureHandler
         *
         * En el ejemplo siguiente cada vez que se lance la excepción entrará
         * por el failureHandler y devolverá al cliente un error 500 y el
         * mensaje Error in handler.
         */
        router.get("/api")
                .handler(routingContext -> {
                    throw new RuntimeException("Error");
                }).failureHandler(routingContext -> {
            routingContext.response().setStatusCode(500);
            routingContext.response().end("Error in handler");
        });

        server.requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        LOG.info("vertx-restful-lab: Deploy ServerFailureHandler Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerFailureHandler());
    }
}
