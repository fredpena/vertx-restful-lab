/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.demo1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class WorkerVerticle extends AbstractVerticle {

    private final Random random = new Random();
    final Logger LOG = LoggerFactory.getLogger(WorkerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        String id = UUID.randomUUID().toString();
        LOG.info("Deploying WorkerVerticle - Instances ID: {}", id);

        EventBus eb = vertx.eventBus();

        eb.<JsonObject>consumer("process.coffee.order", message -> {
            JsonObject body = message.body();

            System.out.println("\n");
            LOG.info("New request on Instances ID: {}", id);

            LOG.info("Processing coffee order {} for Customer: {}, Coffee: {}, Size: {}", body.getString("id"), body.getString("customer"), body.getString("type"), body.getString("size"));

            long startTime = System.currentTimeMillis();
            try {
                Thread.sleep(random.nextInt(10) * 1000);
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage());
            }
            long endTime = System.currentTimeMillis();

            LOG.info("Order {} completed in {} milliseconds, Calling {}\n\n", body.getString("id"), (endTime - startTime), body.getString("customer"));
            //message.reply(body);

        });

    }

}
