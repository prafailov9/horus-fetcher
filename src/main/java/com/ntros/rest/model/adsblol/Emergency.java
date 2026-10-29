package com.ntros.rest.model.adsblol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** {@code emergency} of an adsb.lol aircraft: the emergency/priority status the aircraft sends. */
public enum Emergency {
  NONE("none"),
  GENERAL("general"), // squawk 7700
  LIFEGUARD("lifeguard"), // medical
  MINIMUM_FUEL("minfuel"),
  NO_COMMUNICATIONS("nordo"), // squawk 7600
  UNLAWFUL_INTERFERENCE("unlawful"), // squawk 7500
  DOWNED("downed"),
  RESERVED("reserved");

  private final String code;

  Emergency(String code) {
    this.code = code;
  }

  @JsonValue
  public String code() {
    return code;
  }

  /** Returns null for an unknown value instead of failing the whole response. */
  @JsonCreator
  public static Emergency fromCode(String code) {
    for (Emergency emergency : values()) {
      if (emergency.code.equals(code)) {
        return emergency;
      }
    }
    return null;
  }
}
