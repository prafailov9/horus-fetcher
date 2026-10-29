package com.ntros.config;

import com.ntros.fetcher.ApiSettings;

public final class OpenSkyConfig {

  public static ApiSettings fromEnvironment(int pollRateMs) {
    return new ApiSettings(
        pollRateMs, requireEnv("OPENSKY_CLIENT_ID"), requireEnv("OPENSKY_CLIENT_SECRET"));
  }

  private static String requireEnv(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing environment variable " + name);
    }
    return value;
  }
}