package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/** Response of {@code GET /tracks}. Times are Unix seconds. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FlightTrack(
    String icao24,
    long startTime,
    long endTime,
    // the docs spell it "calllsign", so accept both
    @JsonAlias("calllsign") String callsign,
    List<Waypoint> path) {}
