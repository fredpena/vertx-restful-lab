/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example01;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 *
 * Hasta ahora hemos visto como registrar rutas para nuestra aplicación, pero el
 * protocolo HTTP proporciona una serie de métodos para poder hacer distintos
 * tipos de peticiones GET, POST, PUT, DELETE… Con Vert.x podemos indicar cada
 * una de nuestras rutas a que tipo de método responde
 */
public class RouterHTTP extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(RouterHTTP.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOG.info("vertx-restful-lab: Desplegando RouterHTTP Verticle");

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        /**
         * En el ejemplo siguiente únicamente se responderá a la ruta /user si
         * el método HTTP con el que se ha llamado es POST.
         */
        router.route(HttpMethod.POST, "/user")
                .produces("application/json")
                .handler(routingContext -> {
                    JsonObject object = routingContext.getBodyAsJson();

                    routingContext.response()
                            .setStatusCode(200)
                            .putHeader("Content-Type", "application/json")
                            .end(object.encode());
                });

        /**
         * Otra forma de hacer esto mismo es usando directamente los métodos de
         * router get, getWithRegex, post, postWithRegex, put, putWithRegex…
         */
        router.post("/client")
                .produces("application/json")
                .handler(routingContext -> {
                    JsonObject object = routingContext.getBodyAsJson();

                    routingContext.response()
                            .setStatusCode(200)
                            .putHeader("Content-Type", "application/json")
                            .end(object.encode());
                });

        /**
         * En el ejemplo siguiente únicamente se responderá a la ruta /user si
         * el método HTTP con el que se ha llamado es POST.
         */
        router.route("/profile")
                .method(HttpMethod.GET)
                .method(HttpMethod.POST)
                .produces("application/json")
                .handler(routingContext -> {

                    LOG.info("vertx-restful-lab: HttpMethod: {}", routingContext.request().rawMethod());

                    JsonObject object = routingContext.getBodyAsJson();

                    routingContext.response()
                            .setStatusCode(200)
                            .putHeader("Content-Type", "application/json")
                            .end(object.encode());
                });

        /**
         * Orden de las rutas 
         * 
         * Por defecto las rutas se van resolviendo por el
         * orden en el que han sido agregadas al Router. Una vez se encuentra
         * una coincidencia se ejecuta el handler asociado y no se siguen
         * buscando coincidencias a excepción que se llame al método next, en
         * cuyo caso se seguirá buscando coincidencias. Si queremos añadir datos
         * de respuesta durante todas las coincidencias, tendremos que activar
         * la respuesta por trozos (chunked):
         */
        router.get("/user/profile")
                .produces("application/json")
                .handler(routingContext -> {
                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);

                    response.write("Response1 - ");

                    routingContext.next();
                });

        router.route("/user/profile")
                .handler(routingContext -> {
                    HttpServerResponse response = routingContext.response();
                    response.write("Response2").end();
                });

        server.requestHandler(router::accept)
                .listen(8085, ar -> {
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
        vertx.deployVerticle(new RouterHTTP());
    }

}
