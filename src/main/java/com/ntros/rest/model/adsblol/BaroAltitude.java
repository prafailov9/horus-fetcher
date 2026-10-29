package com.ntros.rest.model.adsblol;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * {@code alt_baro} of an adsb.lol aircraft, which is either the altitude in feet or the string
 * {@code "ground"}. {@code feet} is null on the ground.
 */
public record BaroAltitude(Integer feet, boolean onGround) {

  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static BaroAltitude of(Object value) {
    if (value instanceof Number number) {
      return new BaroAltitude(number.intValue(), false);
    }
    if ("ground".equals(value)) {
      return new BaroAltitude(null, true);
    }
    return null;
  }
}
