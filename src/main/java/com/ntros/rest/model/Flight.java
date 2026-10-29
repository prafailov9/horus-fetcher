package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One element of the array returned by {@code GET /flights/all}, {@code /flights/aircraft}, {@code
 * /flights/arrival} and {@code /flights/departure}. Times are Unix seconds, distances meters. The
 * estimated airports and their distances are null when OpenSky could not identify the airport.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Flight(
    String icao24,
    long firstSeen,
    String estDepartureAirport,
    long lastSeen,
    String estArrivalAirport,
    String callsign,
    Integer estDepartureAirportHorizDistance,
    Integer estDepartureAirportVertDistance,
    Integer estArrivalAirportHorizDistance,
    Integer estArrivalAirportVertDistance,
    int departureAirportCandidatesCount,
    int arrivalAirportCandidatesCount) {}
