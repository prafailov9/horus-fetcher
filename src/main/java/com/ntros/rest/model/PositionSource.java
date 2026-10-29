package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** {@code position_source} of a {@link StateVector}. */
public enum PositionSource {
  ADS_B(0),
  ASTERIX(1),
  MLAT(2),
  FLARM(3);

  private final int code;

  PositionSource(int code) {
    this.code = code;
  }

  @JsonValue
  public int code() {
    return code;
  }

  /** Returns null for a code OpenSky adds later, instead of failing the whole response. */
  @JsonCreator
  public static PositionSource fromCode(int code) {
    for (PositionSource source : values()) {
      if (source.code == code) {
        return source;
      }
    }
    return null;
  }
}
