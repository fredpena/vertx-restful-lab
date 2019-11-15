/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class BarmanVerticle extends AbstractVerticle {

    @Override
    public void start() {
        BarmanService service = new BarmanServiceImpl(WebClient.create(vertx)); 

        new ServiceBinder(vertx) 
                .setAddress("beers.services") 
                .register(BarmanService.class, service); 
    }

}
