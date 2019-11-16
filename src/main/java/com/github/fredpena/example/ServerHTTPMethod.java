/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example;

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
 * @author Fred Pena fantpena@gmail.com
 * <p>
 * Hasta ahora hemos visto como registrar rutas para nuestra aplicación, pero el
 * protocolo HTTP proporciona una serie de métodos para poder hacer distintos
 * tipos de peticiones GET, POST, PUT, DELETE… Con Vert.x podemos indicar cada
 * una de nuestras rutas a que tipo de método responde
 *
 * EXAMPLE: 2
 */
public class ServerHTTPMethod extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(ServerHTTPMethod.class);
    private static final int KB = 1024;
    private static final int MB = 1024 * KB;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        /**
         * Peticiones con datos en el cuerpo
         *
         * Cuando realizamos peticiones podemos enviar parámetros tanto en la
         * url como en el cuerpo de la petición. Anteriormente vimos como poder
         * recoger esos parámetros de la url con el método getParam. En cambio
         * para poder recoger los parámetros en el cuerpo de las peticiones
         * tenemos que activar un handler. Esto lo podemos hacer añadiendo la
         * siguiente linea antes de cualquier petición que vaya a necesitar
         * obtener datos
         */
        router.route().handler(BodyHandler.create().setBodyLimit(50 * MB));

        /**
         * En el ejemplo siguiente únicamente se responderá a la ruta /user si
         * el método HTTP con el que se ha llamado es POST.
         *
         * Endpoint:
         *
         * http://localhost:8080/user
         *
         * Body: {"user": "fred" }
         */
        router.route(HttpMethod.POST, "/user")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/user - HTTP POST request");

                    JsonObject object = routingContext.getBodyAsJson();

                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        /**
         * Otra forma de hacer esto mismo es usando directamente los métodos de
         * router get, getWithRegex, post, postWithRegex, put, putWithRegex…
         *
         * Endpoint:
         *
         * http://localhost:8080/client
         *
         * Body: {"customer" : "aine"}
         */
        router.post("/client")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/client - HTTP POST request");
                    JsonObject object = routingContext.getBodyAsJson();

                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        /**
         * Además podemos combinar varias rutas para con un mismo handler
         * responder a ellas
         *
         * Endpoint:
         *
         * http://localhost:8080/profile
         */
        router.route("/profile")
                .method(HttpMethod.GET)
                .method(HttpMethod.POST)
                .produces("application/json")
                .handler(routingContext -> {
                    JsonObject object = new JsonObject().put("HTTP_Method", routingContext.request().rawMethod());

                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/profile - HTTP {} request", object.getString("HTTP_Method"));

                    routingContext.response()
                            .putHeader("content-type", "application/json")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        /**
         * Orden de las rutas
         *
         * Por defecto las rutas se van resolviendo por el orden en el que han
         * sido agregadas al Router. Una vez se encuentra una coincidencia se
         * ejecuta el handler asociado y no se siguen buscando coincidencias a
         * excepción que se llame al método next, en cuyo caso se seguirá
         * buscando coincidencias. Si queremos añadir datos de respuesta durante
         * todas las coincidencias, tendremos que activar la respuesta por
         * trozos (chunked):
         *
         * La respuesta a la llamada /user/profile con el ejemplo siguiente
         * devolverá Response1 - Response2. Primero se ejecutará el primer
         * handler y a continuación gracias a la llamada a next se ejecutará el
         * segundo handler.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/profile
         */
        router.get("/user/profile")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/user/profile - HTTP GET request #1");

                    HttpServerResponse response = routingContext.response();
                    response.setChunked(true);
                    response.write("Respondiendo desde el primer endpoint, si encuentro otro igual lo ejecutare. ");

                    routingContext.next();
                });

        router.get("/user/profile")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/user/profile - HTTP GET request #2");

                    HttpServerResponse response = routingContext.response();
                    response.write("Otro endpoint encontrado").end();
                });

        /**
         * Si quisiéramos mantener algún tipo de dato entre los distintos
         * handlers y únicamente durante el tiempo de vida de la petición
         * podríamos usar el RoutingContext para ello.
         *
         * En el primer manejador se almacenaría la cadena Hello con la clave
         * info, posteriormente en el segundo, se extraería esa cadena y se
         * concatenaría con la cadena World! para devolverla al usuario. Después
         * de esto se limpiaría el RoutingContext y la clave info no existiría
         * en la siguiente petición.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/client
         */
        router.get("/user/client")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/user/client - HTTP GET request #1");

                    routingContext.put("info", "Hello");

                    routingContext.next();
                });

        router.get("/user/client")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerHTTPMethod:  path:/user/client - HTTP GET request #1");

                    String data = routingContext.get("info");
                    routingContext.response().end(data + " World!");
                });

        /**
         * Rutas con tipo MIME
         *
         * Además de los visto hasta ahora, también podemos definir rutas
         * basadas en el tipo MIME que se envía en la petición desde el cliente.
         *
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
         */
        router.route("/user/profile3")
                .consumes("text/html")
                //.consumes("text/*")
                //.consumes("text/plain")
                .produces("text/html")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab: Router:route /user/profile3, consumes:text/html");

                    JsonObject object = new JsonObject();
                    object.put("/user/profile3", "consumes:text/html");
                    routingContext.response()
                            .putHeader("content-type", "text/html")
                            .setStatusCode(200)
                            .end(object.encode());
                });

        router.route("/user/profile3")
                .produces("application/json")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab: Router:route /user/profile3, consumes:with out text/html");

                    JsonObject object = new JsonObject();
                    object.put("/user/profile3", "consumes:with out text/html");
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
         */
        router.route("/user/profile4")
                .produces("application/json")
                .produces("application/xml")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab: Router:route /user/profile4, getAcceptableContentType");

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
                        LOG.info("vertx-restful-lab: Deploy ServerHTTPMethod Verticle in the port: {}", ar.result().actualPort());
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerHTTPMethod());
    }
}
