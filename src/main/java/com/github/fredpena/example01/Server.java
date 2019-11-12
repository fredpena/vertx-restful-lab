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
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 * 
 * Uno de los componentes core de Vert.x Web es el Route. Este componente se encarga de 
 * tener registradas las rutas a las que nuestra aplicación va a responder, 
 * para cuando reciba una petición sobre una de ellas llamar a su manejado asociado.
 * 
 * En este ejemplo podemos ver como en el router se ha registrado la ruta /path y en su manejador (handler), 
 * que recibe un objeto del tipo RoutingContext, le añadimos una cabecera y respondemos al cliente con el mensaje Hello World!. 
 * Importante la llamada a end para que la respuesta se envíe al cliente. Finalmente en el server indicamos que el 
 * manejo de las peticiones se llevará a cabo mediante el route y lo ponemos a escuchar en el puerto 8080.
 */


public class Server extends AbstractVerticle {

    static final Logger LOG = LoggerFactory.getLogger(Server.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOG.info("vertx-restful-lab: Desplegando Java Verticle");

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/hello")
                .handler(routingContext -> {
                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Hello World!");
                });

        server.requestHandler(router::accept)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
    }

}
