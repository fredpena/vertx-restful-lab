/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.fredpena.utils;

import java.util.Random;

/**
 *
 * @author Fred Pena fantpena@gmail.com
 */
public class RandomCoffee {

    private final String[] types;
    private final String[] sizes;
    private final String[] customers;

    public RandomCoffee() {
        this.types = new String[]{"americano", "latte", "cappucino", "mocha"};
        this.sizes = new String[]{"small", "large", "medium"};
        this.customers = new String[]{"aine", "bernard", "charline", "denise", "esteban", "françois", "geraldine", "herbert"};
    }

    private String getType() {
        Random random = new Random();
        int randomNumber = random.nextInt(types.length);
        return types[randomNumber];
    }

    private String getSize() {
        Random random = new Random();
        int randomNumber = random.nextInt(sizes.length);
        return sizes[randomNumber];
    }

    private String getCustomer() {
        Random random = new Random();
        int randomNumber = random.nextInt(customers.length);
        return customers[randomNumber];
    }

    public Coffee getCoffee() {
        return new Coffee(getCustomer(), getType(), getSize());
    }

}
