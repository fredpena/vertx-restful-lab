/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.serviceProxy;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
@DataObject(generateConverter = true)
public class Beer {

    private String name;
    private String style;
    private int price;

    public Beer(String name, String style, int price) {
        this.name = name;
        this.style = style;
        this.price = price;
    }

    public Beer(JsonObject jsonObject) {
        BeerConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        BeerConverter.toJson(this, json);
        return json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Beer{" + "name=" + name + ", style=" + style + ", price=" + price + '}';
    }

}
