/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.demo2;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class App {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

        final Logger LOG = LoggerFactory.getLogger(App.class);

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
                LOG.info("Starting the app with config: {}\n", json);

                vertx.deployVerticle(CoffeeRestApiVerticle.class.getCanonicalName(), new DeploymentOptions()
                        .setConfig(config));

                vertx.deployVerticle(WorkerVerticle.class.getCanonicalName(), new DeploymentOptions()
                        .setConfig(config)
                        .setWorkerPoolName("coffee-making-pool")
                        .setWorkerPoolSize(5)
                        .setInstances(5)
                        .setWorker(true));

            } else {
                LOG.error("Error retrieving configuration.");
            }
        });

    }

}
