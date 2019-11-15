/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BarmanServiceImpl implements BarmanService {

    private final Map<String, Integer> bills;
    private final Random random = new Random();
    private final WebClient client;

    public BarmanServiceImpl(WebClient client) {
        this.client = client;
        this.bills = new HashMap<>();
    }

    @Override
    public void giveMeARandomBeer(String customerName, Handler<AsyncResult<Beer>> handler) {
        this.client
                .get(443, "www.craftbeernamegenerator.com", "/api/api.php?type=classic")
                .ssl(true)
                .send(ar -> { 
                    if (ar.failed()) {
                        handler.handle(Future.failedFuture(ar.cause())); 
                    } else {
                        JsonObject result = ar.result().bodyAsJsonObject();
                        if (result.getInteger("status") != 200) 
                        {
                            handler.handle(Future.failedFuture("Beer Generator Service replied with " + result.getInteger("status") + ": " + result.getString("status_message")));
                        } else {
                            Beer beer = new Beer( 
                                    result.getJsonObject("data").getString("name"),
                                    result.getJsonObject("data").getString("style"),
                                    200 + random.nextInt(100)
                            );
                            System.out.println("Generated a new Beer! " + beer);
                            bills.merge(customerName, beer.getPrice(), (oldVal, newVal) -> oldVal + newVal);
                            handler.handle(Future.succeededFuture(beer)); 
                        }
                    }
                });
    }

    @Override
    public void getMyBill(String customerName, Handler<AsyncResult<Integer>> handler) {
        handler.handle(Future.succeededFuture(bills.get(customerName)));
    }

    @Override
    public void payMyBill(String customerName) {
        bills.remove(customerName);
        System.out.println("Removed debt of " + customerName);
    }

}
