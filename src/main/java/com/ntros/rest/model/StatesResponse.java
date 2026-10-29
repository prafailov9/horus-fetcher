package com.ntros.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Response of {@code GET /states/all} and {@code GET /states/own}. {@code time} is Unix seconds.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StatesResponse(long time, List<StateVector> states) {

  public StatesResponse {
    // OpenSky sends "states": null when nothing matches the query
    states = states == null ? List.of() : states;
  }
}
