package com.ntros.rest;

import com.ntros.rest.model.FlightTrack;
import com.ntros.rest.model.StatesResponse;
import com.ntros.fetcher.AccessResponse;

public interface ApiClient {

  AccessResponse requestApiAccess();

  StatesResponse getAllStates();

  /** Live track of the aircraft's ongoing flight, or null if it is not flying or the call fails. */
  FlightTrack getFlightTrackForCurrentTime(String icao24);

  /**
   * Track of the flight that was ongoing at {@code time} (Unix seconds), or null if there is none
   * or the call fails. OpenSky keeps tracks for 30 days.
   */
  FlightTrack getFlightTrack(String icao24, long time);
}
