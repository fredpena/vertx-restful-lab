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
 *
 * EXAMPLE: 1
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

        /**
         * Cuando vamos a definir las rutas para nuestra aplicación, Vert.x nos
         * ofrece muchas posibilidades para que las personalicemos a nuestro
         * gusto. Las más comunes son las rutas exactas
         *
         * router.route("/user/profile").handler( ... );
         *
         * Con este ejemplo, cuando la ruta que se reciba sea /user/profile se
         * ejecutará el handler indicado. Hay que mencionar que las barras del
         * final de las rutas son ignoradas, lo que significa que las siguientes
         * rutas /user/profile/ y /user/profile// son equivalentes a la
         * anterior, pero /users/profile/edit no es igual.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/profile
         *
         * http://localhost:8080/user/profile/edit
         */
        router.route("/user/profile")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:/user/profile - HTTP GET request");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Esto es una ruta exactas");
                });

        router.route("/user/profile/edit")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:/user/profile - HTTP GET request");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("En este enpoint editariamos el perfil");
                });

        /**
         * Otra posibilidad son definir rutas que comienzan igual pero difieren
         * en la parte final. Si queremos que todas las rutas con un mismo
         * comienzo sean manejadas por el mismo handler esta es la solución más
         * cómoda. Para definir estas rutas se usa el símbolo * de forma que un
         * ruta como la siguente:
         *
         * router.route("/client/profile/*").handler( ... );
         *
         * Escucharía todas las peticiones que comenzarán por ella como:
         * /client/profile, /client/profile/edit, /client/profile/me/photo.jpg …
         * y todas ellas serían manejadas por el mismo handler.
         *
         * Endpoint:
         *
         * http://localhost:8080/client/profile
         *
         * http://localhost:8080/client/profile/edit
         *
         * http://localhost:8080/client/profile/me
         *
         * http://localhost:8080/client/profile/me/photo.jpg
         */
        router.route("/client/profile/*")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:/client/profile/* - HTTP GET request");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Rutas que comienzan igual pero difieren en la parte final");
                });

        /**
         * Las rutas definidas, pueden contener parámetros que nos interese
         * obtener en el handler. Para ello indicaremos estos parámetros en las
         * rutas anteponiendo al nombre del parámetro el símbolo <:>
         * Posteriormente dentro del handler podemos acceder a ellos obteniendo
         * el objeto HttpServerRequest desde el routingContext y llamando al
         * método getParam():
         *
         * Endpoint:
         *
         * http://localhost:8080/user/Maria
         *
         * http://localhost:8080/user/Juan
         */
        router.route("/supplier/:userId")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:/user/:userId - HTTP GET request");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    String userId = routingContext.request().getParam("userId");

                    response.end("Hello " + userId + ", how are you?");
                });

        /**
         * La última opción que nos deja Vert.x para definir rutas, es usar
         * expresiones regulares:
         *
         * route.route().pathRegex(".*expression").handler(...);
         *
         * // Otra opción
         *
         * route.routeWithRegex(".*regular").handler(...);
         *
         * En los dos ejemplos siguiente se ejecutaría el manejado para todas
         * las rutas que contengan expression: /users/expression, /expression,
         * /users/expression/me … Cualquier expresión regular nos valdría para
         * indicar una ruta.
         *
         * Endpoint:
         *
         * http://localhost:8080/users/expression
         *
         * http://localhost:8080/expression
         *
         * http://localhost:8080/users/profile/expression
         *
         */
        router.route().pathRegex(".*expression")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:.*expression - URI pathRegex");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Expresiones regulares: .*expression");
                });

        router.routeWithRegex(".*regular")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:.*regular - URI routeWithRegex");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    response.end("Expresiones regulares: .*regular");
                });

        /**
         * En el caso de rutas con expresiones regulares, también podemos
         * capturar los parámetros, aunque no es tan sencillo ya que hay que
         * usar los grupos de captura.
         *
         * En el ejemplo sigueinte se está capturando dos parámetros que vienen
         * separados por el carácter /. Es decir, en la ruta /user/client
         * tendremos en param0 user y en param1 client. A partir de aquí podemos
         * complicar la expresión regular tanto como queramos y utilizar
         * distintos grupos de captura para recoger los valores que nos
         * interese.
         *
         * Endpoint:
         *
         * http://localhost:8080/user/client
         */
        router.routeWithRegex(".*client")
                .pathRegex("\\/([^\\/]+)\\/([^\\/]+)")
                .handler(routingContext -> {
                    LOG.info("vertx-restful-lab:ServerOverview:  path:.*client - URI routeWithRegex");

                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "text/plain");

                    String first = routingContext.request().getParam("param0");
                    String second = routingContext.request().getParam("param1");

                    response.end("Hello, " + first + " " + second);
                });

        /**
         * Le indicamos al server que el manejo de las peticiones se llevará a
         * cabo mediante el route y lo ponemos a escuchar en el puerto 8080.
         */
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
