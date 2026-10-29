package com.ntros.model;

import com.ntros.rest.model.AircraftCategory;
import java.time.Instant;

/**
 * One aircraft as the app sees it, independent of where the data came from. Sources are merged by
 * {@code icao24}. Units are metric: altitudes meters, speeds m/s, vertical rate m/s (positive is
 * climbing), angles degrees clockwise from north. A null field means unknown, not zero.
 *
 * @param icao24 transponder address, lower-case hex
 * @param callsign trimmed callsign
 * @param registration tail number, e.g. D-AIZZ
 * @param typeCode ICAO aircraft type designator, e.g. A320
 * @param originCountry country of the transponder address block
 * @param military null when the source doesn't say
 * @param positionTime when {@code latitude}/{@code longitude} were last reported
 * @param lastSeen when any message was last received
 * @param source source of the position
 */
public record Aircraft(
    String icao24,
    String callsign,
    String registration,
    String typeCode,
    String originCountry,
    AircraftCategory category,
    Boolean military,
    Double latitude,
    Double longitude,
    Double baroAltitude,
    Double geoAltitude,
    boolean onGround,
    Double groundSpeed,
    Double track,
    Double verticalRate,
    String squawk,
    Instant positionTime,
    Instant lastSeen,
    DataSource source) {

  public boolean hasPosition() {
    return latitude != null && longitude != null;
  }
}
