package com.ntros.rest.model.adsblol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One entry of {@code ac} in an adsb.lol response (readsb format). Only the fields this app uses
 * are mapped; the rest are ignored. Units are imperial: altitudes feet, speeds knots, vertical
 * rates feet/minute, angles degrees clockwise from north. Ages ({@code seen}, {@code seen_pos})
 * are seconds before the response's {@code now}. Any field except {@code hex} can be missing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AdsbLolAircraft(
    String hex,
    // where the data came from: adsb_icao, mode_s, mlat, tisb_icao, adsr_icao, ...
    @JsonProperty("type") String messageType,
    String flight,
    @JsonProperty("r") String registration,
    // ICAO type designator, e.g. A320, C17, H60
    @JsonProperty("t") String typeCode,
    Integer dbFlags,
    @JsonProperty("alt_baro") BaroAltitude baroAltitude,
    @JsonProperty("alt_geom") Integer geomAltitude,
    @JsonProperty("gs") Double groundSpeed,
    @JsonProperty("ias") Integer indicatedAirSpeed,
    @JsonProperty("tas") Integer trueAirSpeed,
    Double mach,
    Double track,
    @JsonProperty("true_heading") Double trueHeading,
    @JsonProperty("mag_heading") Double magneticHeading,
    @JsonProperty("baro_rate") Integer baroRate,
    @JsonProperty("geom_rate") Integer geomRate,
    String squawk,
    Emergency emergency,
    // ADS-B emitter category, e.g. A3; see AircraftCategory.fromEmitterCategory
    String category,
    @JsonProperty("nav_altitude_mcp") Integer selectedAltitude,
    @JsonProperty("nav_heading") Double selectedHeading,
    @JsonProperty("lat") Double latitude,
    @JsonProperty("lon") Double longitude,
    @JsonProperty("seen_pos") Double secondsSincePosition,
    @JsonProperty("seen") Double secondsSinceSeen,
    // only sent when the current position is stale
    LastPosition lastPosition,
    Long messages,
    Double rssi) {

  private static final int MILITARY = 1;
  private static final int INTERESTING = 2;
  private static final int PIA = 4;
  private static final int LADD = 8;

  public boolean isMilitary() {
    return hasFlag(MILITARY);
  }

  public boolean isInteresting() {
    return hasFlag(INTERESTING);
  }

  /** Privacy ICAO Address: a temporary address that hides the real aircraft. */
  public boolean isPia() {
    return hasFlag(PIA);
  }

  /** On the FAA's Limiting Aircraft Data Displayed list (other trackers hide these). */
  public boolean isLadd() {
    return hasFlag(LADD);
  }

  private boolean hasFlag(int flag) {
    return dbFlags != null && (dbFlags & flag) != 0;
  }
}
