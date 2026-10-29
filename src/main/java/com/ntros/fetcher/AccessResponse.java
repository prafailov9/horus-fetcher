package com.ntros.fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") int expiresInSeconds,
    @JsonProperty("refresh_expires_in") int refreshExpiresIn,
    @JsonProperty("not-before-policy") long notBeforePolicy,
    String scope) {}
