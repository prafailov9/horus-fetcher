package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * One point of {@link FlightTrack#path()}. OpenSky sends it as a JSON array, so the property order
 * below must match the documented indices 0-5. {@code time} is Unix seconds, {@code baroAltitude}
 * meters, {@code trueTrack} degrees clockwise from north.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"time", "latitude", "longitude", "baroAltitude", "trueTrack", "onGround"})
@JsonIgnoreProperties(ignoreUnknown = true)
public record Waypoint(
    long time,
    Double latitude,
    Double longitude,
    Double baroAltitude,
    Double trueTrack,
    boolean onGround) {}
