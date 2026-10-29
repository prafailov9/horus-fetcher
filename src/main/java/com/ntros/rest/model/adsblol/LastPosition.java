package com.ntros.rest.model.adsblol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Last known position of an adsb.lol aircraft whose current position is missing or stale. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LastPosition(
    @JsonProperty("lat") Double latitude,
    @JsonProperty("lon") Double longitude,
    @JsonProperty("seen_pos") Double secondsSincePosition) {}
