package com.ntros.rest.model.adsblol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ntros.model.Units;
import com.ntros.rest.model.AircraftCategory;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class AdsbLolModelTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void readsAirborneAircraft() {
    // trimmed from a live /v2/point response
    String json =
        """
        {"ac":[
          {"hex":"4d2507","type":"adsb_icao","flight":"EWG3MV  ","r":"9H-SWE","t":"A320",
           "alt_baro":19425,"alt_geom":20100,"gs":374.9,"ias":291,"tas":386,"mach":0.628,
           "track":313.70,"mag_heading":308.85,"true_heading":313.05,"baro_rate":-1984,
           "geom_rate":-2048,"squawk":"7443","emergency":"none","category":"A3",
           "nav_qnh":1012.8,"nav_altitude_mcp":11008,"lat":48.629654,"lon":10.736600,"nic":7,
           "seen_pos":0.000,"version":2,"mlat":[],"tisb":[],"messages":45751,"seen":0.0,
           "rssi":-9.1,"dst":44.653,"dir":292.4}
         ],
         "msg":"No error","now":1793196085000,"total":1,"ctime":1793196085000,"ptime":0}
        """;

    AdsbLolResponse response = mapper.readValue(json, AdsbLolResponse.class);

    assertEquals(1793196085000L, response.now());
    assertEquals("No error", response.msg());
    assertEquals(1, response.total());
    assertEquals(1, response.aircraft().size());

    AdsbLolAircraft aircraft = response.aircraft().get(0);
    assertEquals("4d2507", aircraft.hex());
    assertEquals("adsb_icao", aircraft.messageType());
    assertEquals("EWG3MV  ", aircraft.flight());
    assertEquals("9H-SWE", aircraft.registration());
    assertEquals("A320", aircraft.typeCode());
    assertNull(aircraft.dbFlags());
    assertFalse(aircraft.isMilitary());
    assertEquals(new BaroAltitude(19425, false), aircraft.baroAltitude());
    assertEquals(20100, aircraft.geomAltitude());
    assertEquals(374.9, aircraft.groundSpeed());
    assertEquals(291, aircraft.indicatedAirSpeed());
    assertEquals(386, aircraft.trueAirSpeed());
    assertEquals(0.628, aircraft.mach());
    assertEquals(313.70, aircraft.track());
    assertEquals(313.05, aircraft.trueHeading());
    assertEquals(308.85, aircraft.magneticHeading());
    assertEquals(-1984, aircraft.baroRate());
    assertEquals(-2048, aircraft.geomRate());
    assertEquals("7443", aircraft.squawk());
    assertEquals(Emergency.NONE, aircraft.emergency());
    assertEquals("A3", aircraft.category());
    assertEquals(11008, aircraft.selectedAltitude());
    assertNull(aircraft.selectedHeading());
    assertEquals(48.629654, aircraft.latitude());
    assertEquals(10.7366, aircraft.longitude());
    assertEquals(0.0, aircraft.secondsSincePosition());
    assertEquals(0.0, aircraft.secondsSinceSeen());
    assertNull(aircraft.lastPosition());
    assertEquals(45751L, aircraft.messages());
    assertEquals(-9.1, aircraft.rssi());
  }

  @Test
  void readsMilitaryAircraftOnGround() {
    String json =
        """
        {"ac":[
          {"hex":"ae49c3","type":"adsb_icao","flight":"RCH3225 ","r":"09-9207","t":"C17",
           "dbFlags":1,"alt_baro":"ground","gs":0.0,"true_heading":129.38,"squawk":"7440",
           "emergency":"none","category":"A5","lat":61.259894,"lon":-149.799779,
           "seen_pos":0.442,"messages":1485,"seen":0.4,"rssi":-2.3}
         ],"msg":"No error","now":1793196085000,"total":1}
        """;

    AdsbLolAircraft aircraft = mapper.readValue(json, AdsbLolResponse.class).aircraft().get(0);

    assertTrue(aircraft.isMilitary());
    assertFalse(aircraft.isLadd());
    assertTrue(aircraft.baroAltitude().onGround());
    assertNull(aircraft.baroAltitude().feet());
    assertNull(aircraft.geomAltitude());
  }

  @Test
  void readsStalePositionAsLastPosition() {
    String json =
        """
        {"ac":[
          {"hex":"ae1146","type":"mode_s","flight":"KATT43  ","r":"165980","t":"TEX2",
           "dbFlags":1,"alt_baro":7225,"ias":202,"squawk":"2667","emergency":"none",
           "category":"A1","rr_lat":30.4,"rr_lon":-88.1,
           "lastPosition":{"lat":30.060409,"lon":-87.937592,"nic":0,"rc":0,"seen_pos":349.925},
           "messages":1726,"seen":1.1,"rssi":-22.2}
         ],"msg":"No error","now":1793196085000,"total":1}
        """;

    AdsbLolAircraft aircraft = mapper.readValue(json, AdsbLolResponse.class).aircraft().get(0);

    assertNull(aircraft.latitude());
    assertNull(aircraft.longitude());
    assertEquals(new LastPosition(30.060409, -87.937592, 349.925), aircraft.lastPosition());
  }

  @Test
  void readsDbFlagBits() {
    String json =
        """
        {"ac":[{"hex":"a1b2c3","dbFlags":14}],"msg":"No error","now":1,"total":1}
        """;

    AdsbLolAircraft aircraft = mapper.readValue(json, AdsbLolResponse.class).aircraft().get(0);

    assertFalse(aircraft.isMilitary());
    assertTrue(aircraft.isInteresting());
    assertTrue(aircraft.isPia());
    assertTrue(aircraft.isLadd());
  }

  @Test
  void readsEmergencyAndUnknownValues() {
    String json =
        """
        {"ac":[
          {"hex":"000001","emergency":"nordo","alt_baro":null},
          {"hex":"000002","emergency":"something-new","alt_baro":"unexpected"}
         ],"msg":"No error","now":1,"total":2}
        """;

    AdsbLolResponse response = mapper.readValue(json, AdsbLolResponse.class);

    assertEquals(Emergency.NO_COMMUNICATIONS, response.aircraft().get(0).emergency());
    assertNull(response.aircraft().get(0).baroAltitude());
    assertNull(response.aircraft().get(1).emergency());
    assertNull(response.aircraft().get(1).baroAltitude());
  }

  @Test
  void readsMissingAircraftListAsEmpty() {
    AdsbLolResponse response =
        mapper.readValue("{\"msg\":\"No error\",\"now\":1,\"total\":0}", AdsbLolResponse.class);

    assertTrue(response.aircraft().isEmpty());
  }

  @Test
  void mapsEmitterCategoryToOpenSkyCategory() {
    assertEquals(AircraftCategory.NO_ADSB_CATEGORY_INFO, AircraftCategory.fromEmitterCategory("A0"));
    assertEquals(AircraftCategory.LIGHT, AircraftCategory.fromEmitterCategory("A1"));
    assertEquals(AircraftCategory.LARGE, AircraftCategory.fromEmitterCategory("A3"));
    assertEquals(AircraftCategory.HEAVY, AircraftCategory.fromEmitterCategory("A5"));
    assertEquals(AircraftCategory.ROTORCRAFT, AircraftCategory.fromEmitterCategory("A7"));
    assertEquals(AircraftCategory.GLIDER, AircraftCategory.fromEmitterCategory("B1"));
    assertEquals(AircraftCategory.UAV, AircraftCategory.fromEmitterCategory("B6"));
    assertEquals(AircraftCategory.SPACE_VEHICLE, AircraftCategory.fromEmitterCategory("B7"));
    assertEquals(
        AircraftCategory.SURFACE_EMERGENCY_VEHICLE, AircraftCategory.fromEmitterCategory("C1"));
    assertEquals(AircraftCategory.LINE_OBSTACLE, AircraftCategory.fromEmitterCategory("C5"));
    assertNull(AircraftCategory.fromEmitterCategory("C6"));
    assertNull(AircraftCategory.fromEmitterCategory("D1"));
    assertNull(AircraftCategory.fromEmitterCategory("A"));
    assertNull(AircraftCategory.fromEmitterCategory(null));
  }

  @Test
  void convertsUnits() {
    assertEquals(3048.0, Units.feetToMeters(10000), 1e-9);
    assertEquals(1852.0 / 3600.0 * 100, Units.knotsToMetersPerSecond(100.0), 1e-9);
    assertEquals(5.08, Units.feetPerMinuteToMetersPerSecond(1000), 1e-9);
    assertNull(Units.feetToMeters(null));
  }
}
