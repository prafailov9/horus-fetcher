package com.ntros.rest.model.adsblol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response of the adsb.lol {@code /v2/*} endpoints ({@code /v2/point}, {@code /v2/mil}, {@code
 * /v2/hex}, ...). {@code now} is Unix milliseconds; the aircraft's {@code seen} ages are relative
 * to it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AdsbLolResponse(
    @JsonProperty("ac") List<AdsbLolAircraft> aircraft, String msg, long now, Integer total) {

  public AdsbLolResponse {
    aircraft = aircraft == null ? List.of() : aircraft;
  }
}
