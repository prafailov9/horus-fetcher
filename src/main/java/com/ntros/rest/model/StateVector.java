package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * One row of {@code states}. OpenSky sends it as a JSON array, so the property order below must
 * match the documented indices 0-17. Times are Unix seconds, altitudes meters, speeds m/s, angles
 * degrees clockwise from north. {@code category} is only sent when requested with {@code
 * extended=1}.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({
  "icao24",
  "callsign",
  "originCountry",
  "timePosition",
  "lastContact",
  "longitude",
  "latitude",
  "baroAltitude",
  "onGround",
  "velocity",
  "trueTrack",
  "verticalRate",
  "sensors",
  "geoAltitude",
  "squawk",
  "spi",
  "positionSource",
  "category"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public record StateVector(
    String icao24,
    String callsign,
    String originCountry,
    Long timePosition,
    long lastContact,
    Double longitude,
    Double latitude,
    Double baroAltitude,
    boolean onGround,
    Double velocity,
    Double trueTrack,
    Double verticalRate,
    List<Integer> sensors,
    Double geoAltitude,
    String squawk,
    boolean spi,
    PositionSource positionSource,
    AircraftCategory category) {}
