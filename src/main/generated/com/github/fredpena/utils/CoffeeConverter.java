package com.github.fredpena.utils;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.github.fredpena.utils.Coffee}.
 * NOTE: This class has been automatically generated from the {@link com.github.fredpena.utils.Coffee} original class using Vert.x codegen.
 */
public class CoffeeConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Coffee obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "customer":
          if (member.getValue() instanceof String) {
            obj.setCustomer((String)member.getValue());
          }
          break;
        case "size":
          if (member.getValue() instanceof String) {
            obj.setSize((String)member.getValue());
          }
          break;
        case "type":
          if (member.getValue() instanceof String) {
            obj.setType((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Coffee obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Coffee obj, java.util.Map<String, Object> json) {
    if (obj.getCustomer() != null) {
      json.put("customer", obj.getCustomer());
    }
    if (obj.getSize() != null) {
      json.put("size", obj.getSize());
    }
    if (obj.getType() != null) {
      json.put("type", obj.getType());
    }
  }
}
