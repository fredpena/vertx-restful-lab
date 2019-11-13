/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.example03;

import com.github.fredpena.utils.RandomCoffee;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class Client extends AbstractVerticle {

    Logger LOG = LoggerFactory.getLogger(Client.class);
    private static int counter = 0;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = config();

        LOG.info("Deploying Client.java");

        int port = config.getInteger("http.port", 8085);
        String url = config.getString("http.url", "localhost");

        LOG.info("Starting Client on url {}", url);
        LOG.info("Starting Client on port {}", port);

        WebClient client = WebClient.create(vertx);

        String[] types = new String[]{"americano", "latte", "cappucino", "mocha"};
        String[] sizes = new String[]{"small", "large", "medium"};
        String[] customers = new String[]{"aine", "bernard", "charline", "denise", "esteban", "françois", "geraldine", "herbert"};

        vertx.setPeriodic((5 * 1000), handler -> {
            for (int i = 0; i < 3; i++) {

                RandomCoffee randomCoffee = new RandomCoffee(types, sizes, customers);
                JsonObject object = randomCoffee.getCoffee().toJson();

                client.post(port, url, "/coffee")
                        .sendJson(object, ar -> {
                            if (ar.succeeded()) {
                                HttpResponse<Buffer> response = ar.result();

                                JsonObject body = response.bodyAsJsonObject();

                                if (response.statusCode() == 200) {
                                    LOG.info("Order {} Sent! For Customer: {}, Coffee: {}, Size: {}, instance:{}\n", body.getString("id"), body.getString("customer"), body.getString("type"), body.getString("size"), this);
                                } else {
                                    LOG.error("Error {}", body.getString("message"));
                                }
                            } else {
                                LOG.error("Error {}", ar.cause().getMessage());
                            }

                        });
                if (++counter == 6) {
                    vertx.cancelTimer(handler);
                }
            }
        });

        EventBus eb = vertx.eventBus();

        eb.<JsonObject>consumer("processed.coffee.order", message -> {
            JsonObject body = message.body();

            LOG.info("Order {} completed in {} milliseconds, Calling {}\n\n", body.getString("id"), body.getLong("time"), body.getString("customer")); //message.reply(body);

        });
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
        Vertx vertx = Vertx.vertx();

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", "conf/config.json"));

        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(json -> {
            if (json.succeeded()) {

                JsonObject config = json.result();
                LoggerFactory.getLogger(Client.class).info("Starting the client with config: {}", json);

                vertx.deployVerticle(Client.class.getCanonicalName(), new DeploymentOptions().setConfig(config));

            } else {
                LoggerFactory.getLogger(Client.class).error("Error retrieving configuration.");
            }
        });

    }

}
