package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * {@code category} of a {@link StateVector}, using OpenSky's codes. adsb.lol sends the same ADS-B
 * emitter categories as strings; see {@link #fromEmitterCategory}.
 */
public enum AircraftCategory {
  NO_INFO(0),
  NO_ADSB_CATEGORY_INFO(1),
  LIGHT(2), // < 15500 lbs
  SMALL(3), // 15500 to 75000 lbs
  LARGE(4), // 75000 to 300000 lbs
  HIGH_VORTEX_LARGE(5), // e.g. B-757
  HEAVY(6), // > 300000 lbs
  HIGH_PERFORMANCE(7), // > 5g acceleration and 400 kts
  ROTORCRAFT(8),
  GLIDER(9),
  LIGHTER_THAN_AIR(10),
  PARACHUTIST(11),
  ULTRALIGHT(12), // hang-glider, paraglider
  RESERVED(13),
  UAV(14),
  SPACE_VEHICLE(15),
  SURFACE_EMERGENCY_VEHICLE(16),
  SURFACE_SERVICE_VEHICLE(17),
  POINT_OBSTACLE(18), // includes tethered balloons
  CLUSTER_OBSTACLE(19),
  LINE_OBSTACLE(20);

  private final int code;

  AircraftCategory(int code) {
    this.code = code;
  }

  @JsonValue
  public int code() {
    return code;
  }

  /** Returns null for a code OpenSky adds later, instead of failing the whole response. */
  @JsonCreator
  public static AircraftCategory fromCode(int code) {
    for (AircraftCategory category : values()) {
      if (category.code == code) {
        return category;
      }
    }
    return null;
  }

  /**
   * Maps an ADS-B emitter category as adsb.lol sends it ({@code "A0"} to {@code "C5"}) to the
   * OpenSky value. Returns null for anything else.
   */
  public static AircraftCategory fromEmitterCategory(String category) {
    if (category == null || category.length() != 2) {
      return null;
    }
    // A1-A7 are codes 2-8, B1-B7 are 9-15, C1-C5 are 16-20
    int base =
        switch (category.charAt(0)) {
          case 'A' -> 1;
          case 'B' -> 8;
          case 'C' -> 15;
          default -> -1;
        };
    int number = category.charAt(1) - '0';
    if (base < 0 || number < 0 || number > 7) {
      return null;
    }
    return number == 0 ? NO_ADSB_CATEGORY_INFO : fromCode(base + number);
  }
}
