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
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * @author Fred Pena fantpena@gmail.com
 *
 * Rutas con tipo MIME
 *
 * Además de los visto hasta ahora, también podemos definir rutas basadas en el
 * tipo MIME que se envía en la petición desde el cliente.
 *
 * EXAMPLE: 3
 */
public class ServerTypeMIME extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerTypeMIME.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /**
         * En el ejemplo siguiente hemos usado consumes para hacer que
         * únicamente se ejecute el handler si la petición a /user/profile
         * contiene en su cabecera el tipo MIME text/html, de lo contrario no se
         * hará matching con esa ruta y se seguirá en el proceso de búsqueda de
         * rutas.
         *
         * Cuando se indica el tipo MIME, se puede utilizar el carácter * como
         * comodín para omitir una de las dos partes del tipo
         *
         * Otra característica que podemos aplicar a este tipo de ruta es que
         * podemos combinar varios tipos para así poder responder a todos ellos
         * desde un mismo handler.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/profile
         *
         */
        router.route("/user/profile")
                .consumes("text/html")
                //.consumes("text/*")
                //.consumes("text/plain")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerTypeMIME:  path:/user/profile - ROUTE request");

                    JsonObject object = new JsonObject();
                    object.put("/user/profile", "consumes:text/html");

                    routingContext.response()
                            .putHeader("content-type", "text/html")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        router.route("/user/profile")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerTypeMIME:  path:/user/profile - ROUTE request");

                    JsonObject object = new JsonObject();
                    object.put("/user/profile", "Response with out consumes");
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        /**
         * Al igual que se puede definir el tipo MIME para la petición, también
         * podemos hacer que nuestra ruta solo ejecute su handler si el cliente
         * que ha enviado la petición acepta como respuesta el tipo MIME que
         * indicamos.
         *
         * En este caso se usa el método produces para indicar el tipo MIME de
         * respuesta que el servicio va a retornar. También podemos combinar
         * varios tipos y obtener el preferido del cliente con la function
         * getAcceptableContentType.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/client
         */
        router.route("/user/client")
                .produces("application/json")
                .produces("application/xml")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerTypeMIME:  path:/user/client - ROUTE request");

                    String contentType = routingContext.getAcceptableContentType();

                    JsonObject object = new JsonObject();
                    object.put("contentType", contentType);
                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        server.requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        LOG.info("vertx-restful-lab: Deploy ServerTypeMIME Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerTypeMIME());
    }
}
