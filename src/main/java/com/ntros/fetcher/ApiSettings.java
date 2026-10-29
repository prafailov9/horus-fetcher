package com.ntros.fetcher;

public record ApiSettings(int pollRate, String clientId, String clientSecrets) {

  @Override
  public String toString() {
    return "pollRate: " + pollRate + ", clientId: " + "xxx";
  }
}
