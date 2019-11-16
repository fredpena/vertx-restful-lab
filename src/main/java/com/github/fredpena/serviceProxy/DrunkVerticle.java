/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class DrunkVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startFuture) {
        BarmanService barmanService = BarmanService.createProxy(vertx, "beers.services"); 

        barmanService.giveMeARandomBeer("homer", b1 -> { 
            if (b1.failed()) { 
                System.err.println("Cannot get my first beer!");
                startFuture.fail(b1.cause());
                return;
            }
            System.out.println("My first beer is a " + b1.result() + " and it costs " + b1.result().getPrice() + "RD$"); 
            vertx.setTimer(1500, l -> barmanService.giveMeARandomBeer("homer", b2 -> { 
                if (b2.failed()) {
                    System.out.println("Cannot get my second beer!");
                    startFuture.fail(b2.cause());
                    return;
                }
                System.out.println("My second beer is a " + b2.result() + " and it costs " + b2.result().getPrice() + "RD$"); 
                barmanService.getMyBill("homer", billAr -> {
                    System.out.println("My bill with the bar is " + billAr.result()); 
                    barmanService.payMyBill("homer"); 
                    startFuture.complete();
                });
            })
            );
        });
    }

}
