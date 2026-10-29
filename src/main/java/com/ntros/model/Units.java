package com.ntros.model;

/** Conversions from the imperial units adsb.lol uses to the metric units of {@link Aircraft}. */
public final class Units {

  private static final double METERS_PER_FOOT = 0.3048;
  private static final double METERS_PER_SECOND_PER_KNOT = 1852.0 / 3600.0;

  private Units() {}

  /** Returns null for null, like every method here. */
  public static Double feetToMeters(Number feet) {
    return feet == null ? null : feet.doubleValue() * METERS_PER_FOOT;
  }

  public static Double knotsToMetersPerSecond(Number knots) {
    return knots == null ? null : knots.doubleValue() * METERS_PER_SECOND_PER_KNOT;
  }

  public static Double feetPerMinuteToMetersPerSecond(Number feetPerMinute) {
    return feetPerMinute == null ? null : feetPerMinute.doubleValue() * METERS_PER_FOOT / 60.0;
  }
}
