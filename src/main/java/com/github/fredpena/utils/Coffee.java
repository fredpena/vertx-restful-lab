/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.utils;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
@DataObject(generateConverter = true)
public class Coffee {

    private String customer;
    private String type;
    private String size;

    public Coffee() {
    }

    public Coffee(String customer, String type, String size) {
        this.customer = customer;
        this.type = type;
        this.size = size;
    }

    public Coffee(JsonObject json) {
        CoffeeConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        CoffeeConverter.toJson(this, json);
        return json;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Coffee{" + "customer=" + customer + ", type=" + type + ", size=" + size + '}';
    }

}
