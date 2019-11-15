/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.core.Vertx;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class BeersApplication {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new BarmanVerticle(), ar -> {
            if (ar.succeeded()) {
                System.out.println("The barman is ready to serve you");
                vertx.deployVerticle(new DrunkVerticle(), ar2 -> {
                    vertx.close();
                });
            }
        });
    }
}
