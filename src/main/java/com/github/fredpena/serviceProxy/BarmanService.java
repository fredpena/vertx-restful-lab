/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
@VertxGen
@ProxyGen
public interface BarmanService {

    void giveMeARandomBeer(String customerName, Handler<AsyncResult<Beer>> handler);

    void getMyBill(String customerName, Handler<AsyncResult<Integer>> handler);

    void payMyBill(String customerName);

    static BarmanService createProxy(Vertx vertx, String address) {
        return new BarmanServiceVertxEBProxy(vertx, address);
    }

}
