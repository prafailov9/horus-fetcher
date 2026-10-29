package com.ntros.rest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ntros.fetcher.AccessResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

class OpenSkyModelTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void readsStatesWithoutCategory() {
    String json =
        """
        {"time": 1761480000, "states": [
          ["3c6444", "DLH9LF  ", "Germany", 1761479998, 1761479999, 6.1546, 50.1964, 9639.3,
           false, 232.88, 98.26, 4.55, null, 9944.1, "1000", false, 0],
          ["a808c5", null, "United States", null, 1761479990, null, null, null,
           true, 0.0, null, null, [1234, 5678], null, null, false, 2]
        ]}
        """;

    StatesResponse response = mapper.readValue(json, StatesResponse.class);

    assertEquals(1761480000L, response.time());
    assertEquals(2, response.states().size());

    StateVector airborne = response.states().get(0);
    assertEquals("3c6444", airborne.icao24());
    assertEquals("DLH9LF  ", airborne.callsign());
    assertEquals("Germany", airborne.originCountry());
    assertEquals(1761479998L, airborne.timePosition());
    assertEquals(1761479999L, airborne.lastContact());
    assertEquals(6.1546, airborne.longitude());
    assertEquals(50.1964, airborne.latitude());
    assertEquals(9639.3, airborne.baroAltitude());
    assertFalse(airborne.onGround());
    assertEquals(232.88, airborne.velocity());
    assertEquals(98.26, airborne.trueTrack());
    assertEquals(4.55, airborne.verticalRate());
    assertNull(airborne.sensors());
    assertEquals(9944.1, airborne.geoAltitude());
    assertEquals("1000", airborne.squawk());
    assertFalse(airborne.spi());
    assertEquals(PositionSource.ADS_B, airborne.positionSource());
    assertNull(airborne.category());

    StateVector grounded = response.states().get(1);
    assertNull(grounded.callsign());
    assertNull(grounded.timePosition());
    assertNull(grounded.longitude());
    assertTrue(grounded.onGround());
    assertEquals(List.of(1234, 5678), grounded.sensors());
    assertEquals(PositionSource.MLAT, grounded.positionSource());
  }

  @Test
  void readsExtendedStatesWithCategory() {
    String json =
        """
        {"time": 1761480000, "states": [
          ["4ca7b3", "RYR4UP  ", "Ireland", 1761479998, 1761479999, -6.27, 53.42, 1200.0,
           false, 120.5, 280.0, -5.2, null, 1250.0, "4721", false, 0, 4]
        ]}
        """;

    StatesResponse response = mapper.readValue(json, StatesResponse.class);

    assertEquals(AircraftCategory.LARGE, response.states().get(0).category());
  }

  @Test
  void readsNullStatesAsEmptyList() {
    StatesResponse response =
        mapper.readValue("{\"time\": 1761480000, \"states\": null}", StatesResponse.class);

    assertTrue(response.states().isEmpty());
  }

  @Test
  void readsUnknownEnumCodeAsNull() {
    String json =
        """
        {"time": 1, "states": [
          ["abc9f3", null, "X", null, 1, null, null, null, false, null, null, null, null,
           null, null, false, 9, 99]
        ]}
        """;

    StateVector state = mapper.readValue(json, StatesResponse.class).states().get(0);

    assertNull(state.positionSource());
    assertNull(state.category());
  }

  @Test
  void readsFlights() {
    String json =
        """
        [
          {"icao24": "3c675a", "firstSeen": 1517220729, "estDepartureAirport": "EDDF",
           "lastSeen": 1517230737, "estArrivalAirport": "EDDT", "callsign": "DLH016  ",
           "estDepartureAirportHorizDistance": 1604, "estDepartureAirportVertDistance": 40,
           "estArrivalAirportHorizDistance": 2140, "estArrivalAirportVertDistance": 9,
           "departureAirportCandidatesCount": 1, "arrivalAirportCandidatesCount": 2},
          {"icao24": "3c675a", "firstSeen": 1517240000, "estDepartureAirport": "EDDT",
           "lastSeen": 1517250000, "estArrivalAirport": null, "callsign": null,
           "estDepartureAirportHorizDistance": 500, "estDepartureAirportVertDistance": 12,
           "estArrivalAirportHorizDistance": null, "estArrivalAirportVertDistance": null,
           "departureAirportCandidatesCount": 0, "arrivalAirportCandidatesCount": 0}
        ]
        """;

    List<Flight> flights = mapper.readValue(json, new TypeReference<List<Flight>>() {});

    assertEquals(2, flights.size());
    assertEquals(
        new Flight(
            "3c675a", 1517220729L, "EDDF", 1517230737L, "EDDT", "DLH016  ", 1604, 40, 2140, 9, 1,
            2),
        flights.get(0));
    assertNull(flights.get(1).estArrivalAirport());
    assertNull(flights.get(1).estArrivalAirportHorizDistance());
  }

  @Test
  void readsTrack() {
    String json =
        """
        {"icao24": "3c4b26", "startTime": 1761470000, "endTime": 1761479000,
         "callsign": "DLH1KC  ", "path": [
           [1761470000, 50.0379, 8.5622, 0.0, 250.0, true],
           [1761470900, 50.3, 8.1, 3048.0, 262.5, false],
           [1761471800, null, null, null, null, false]
         ]}
        """;

    FlightTrack track = mapper.readValue(json, FlightTrack.class);

    assertEquals("3c4b26", track.icao24());
    assertEquals(1761470000L, track.startTime());
    assertEquals(1761479000L, track.endTime());
    assertEquals("DLH1KC  ", track.callsign());
    assertEquals(3, track.path().size());
    assertEquals(new Waypoint(1761470900L, 50.3, 8.1, 3048.0, 262.5, false), track.path().get(1));
    assertNull(track.path().get(2).latitude());
  }

  @Test
  void readsTrackWithDocumentedCallsignSpelling() {
    String json =
        """
        {"icao24": "3c4b26", "startTime": 1, "endTime": 2, "calllsign": "DLH1KC  ", "path": []}
        """;

    assertEquals("DLH1KC  ", mapper.readValue(json, FlightTrack.class).callsign());
  }

  @Test
  void readsTrackWithFloatingPointTimes() {
    // shape of a live /tracks/all response: start and end times come back as floats
    String json =
        """
        {"icao24": "39de4d", "callsign": "TVF74QN ", "startTime": 1.7931019E9,
         "endTime": 1.793103932E9, "path": [[1793101900, 48.7253, 2.3905, 0, 254, false]]}
        """;

    FlightTrack track = mapper.readValue(json, FlightTrack.class);

    assertEquals(1793101900L, track.startTime());
    assertEquals(1793103932L, track.endTime());
    assertEquals(new Waypoint(1793101900L, 48.7253, 2.3905, 0.0, 254.0, false), track.path().get(0));
  }

  @Test
  void readsAccessToken() {
    String json =
        """
        {"access_token": "eyJhbGciOi.payload.sig", "expires_in": 1800, "refresh_expires_in": 0,
         "token_type": "Bearer", "not-before-policy": 0, "scope": "profile email"}
        """;

    AccessResponse response = mapper.readValue(json, AccessResponse.class);

    assertEquals("eyJhbGciOi.payload.sig", response.accessToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(1800, response.expiresInSeconds());
    assertEquals("profile email", response.scope());
  }
}
