/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.openAPI;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class OpenAPI3Server extends AbstractVerticle {

    final Logger LOG = LoggerFactory.getLogger(OpenAPI3Server.class);

    HttpServer server;

    final List<JsonObject> pets = new ArrayList<>(Arrays.asList(
            new JsonObject().put("id", 1).put("name", "Fufi").put("tag", "ABC"),
            new JsonObject().put("id", 2).put("name", "Garfield").put("tag", "XYZ"),
            new JsonObject().put("id", 3).put("name", "Puffa")
    ));

    @Override
    public void start(Promise<Void> startPromise) {
        OpenAPI3RouterFactory.create(this.vertx, "petstore.yaml", ar -> {
            if (ar.succeeded()) {
                OpenAPI3RouterFactory routerFactory = ar.result();

                routerFactory.addHandlerByOperationId("listPets", routingContext
                        -> routingContext
                                .response()
                                .setStatusCode(200)
                                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                .end(new JsonArray(getAllPets()).encode())
                );

                routerFactory.addHandlerByOperationId("createPets", routingContext -> {
                    RequestParameters params = routingContext.get("parsedParameters");
                    JsonObject pet = params.body().getJsonObject();
                    addPet(pet);
                    routingContext
                            .response()
                            .setStatusCode(200)
                            .end(pet.encode());
                });

                routerFactory.addHandlerByOperationId("showPetById", routingContext -> {
                    RequestParameters params = routingContext.get("parsedParameters");
                    Integer id = Integer.valueOf(params.pathParameter("petId").toString());
                    Optional<JsonObject> pet = getAllPets()
                            .stream()
                            .filter(p -> p.getInteger("id").equals(id))
                            .findFirst();
                    if (pet.isPresent()) {
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                .end(pet.get().encode());
                    } else {
                        routingContext.fail(404, new Exception("Pet not found"));
                    }
                });

                Router router = routerFactory.getRouter();
                router.errorHandler(404, routingContext -> {
                    JsonObject errorObject = new JsonObject()
                            .put("code", 404)
                            .put("message",
                                    (routingContext.failure() != null)
                                    ? routingContext.failure().getMessage()
                                    : "Not Found"
                            );
                    routingContext
                            .response()
                            .setStatusCode(404)
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(errorObject.encode());
                });
                router.errorHandler(400, routingContext -> {
                    JsonObject errorObject = new JsonObject()
                            .put("code", 400)
                            .put("message",
                                    (routingContext.failure() != null)
                                    ? routingContext.failure().getMessage()
                                    : "Validation Exception"
                            );
                    routingContext
                            .response()
                            .setStatusCode(400)
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(errorObject.encode());
                });

                server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));

                server.requestHandler(router).listen();

                startPromise.complete(); // Complete the verticle start
            } else {
                startPromise.fail(ar.cause()); // Fail the verticle start
            }
        });
    }

    private List<JsonObject> getAllPets() {
        return this.pets;
    }

    private void addPet(JsonObject pet) {
        this.pets.add(pet);
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new OpenAPI3Server());
    }

}
