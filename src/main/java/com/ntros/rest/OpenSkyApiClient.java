package com.ntros.rest;

import com.ntros.rest.model.FlightTrack;
import com.ntros.rest.model.StatesResponse;
import com.ntros.fetcher.AccessResponse;
import com.ntros.fetcher.ApiSettings;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class OpenSkyApiClient implements ApiClient {
  private static final Logger log = LoggerFactory.getLogger(OpenSkyApiClient.class);
  private static final String ACCESS_REQUEST_URL =
      "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
  private static final String API_URL = "https://opensky-network.org/api";
  private static final String ALL_STATES_URL = API_URL + "/states/all";
  // the docs say GET /tracks, but that returns 404; their own example uses /tracks/all
  private static final String TRACKS_URL = API_URL + "/tracks/all";

  private final HttpClient httpClient;
  private final ApiSettings apiSettings;
  private final ObjectMapper mapper = new ObjectMapper();
  private volatile String bearer;

  public OpenSkyApiClient(ApiSettings apiSettings) {
    if (apiSettings == null) {
      throw new IllegalArgumentException("Invalid ApIsEttings");
    }
    this.apiSettings = apiSettings;
    httpClient = HttpClient.newHttpClient();
  }

  @Override
  public AccessResponse requestApiAccess() {
    AccessResponse accessResponse = null;
    try {
      var res = httpClient.send(accessRequest(), BodyHandlers.ofString());
      log.info("Access Response: {}", res);
      // TODO: handle 429 Too Many Requests. Create a richer response type and add the failure mode
      // so the caller can respond appropriatelly(pause the data fetch).
      accessResponse = mapper.readValue(res.body(), AccessResponse.class);
      bearer = accessResponse.accessToken();
    } catch (IOException e) {
      log.error("Error during access request", e);
    } catch (InterruptedException e) {
      log.error("Interrupted during access request", e);
    }
    return accessResponse;
  }

  @Override
  public StatesResponse getAllStates() {
    return get(URI.create(ALL_STATES_URL), StatesResponse.class);
  }

  @Override
  public FlightTrack getFlightTrackForCurrentTime(String icao24) {
    // time = 0 asks OpenSky for the live track
    return getFlightTrack(icao24, 0);
  }

  @Override
  public FlightTrack getFlightTrack(String icao24, long time) {
    if (icao24 == null || icao24.isBlank()) {
      throw new IllegalArgumentException("Missing icao24");
    }
    // OpenSky requires the transponder address in lower case
    String address = URLEncoder.encode(icao24.toLowerCase(Locale.ROOT), StandardCharsets.UTF_8);
    return get(URI.create(TRACKS_URL + "?icao24=" + address + "&time=" + time), FlightTrack.class);
  }

  /** Returns null when OpenSky has no data (404) or the request fails. */
  private <T> T get(URI uri, Class<T> type) {
    try {
      var res = httpClient.send(getRequest(uri), BodyHandlers.ofString());
      log.info("Response: {}", res);
      if (res.statusCode() == 404) {
        return null;
      }
      if (res.statusCode() != 200) {
        log.error("Request {} failed with status {}: {}", uri, res.statusCode(), res.body());
        return null;
      }
      return mapper.readValue(res.body(), type);
    } catch (IOException e) {
      log.error("Error during request {}", uri, e);
    } catch (JacksonException e) {
      log.error("Could not parse response of {}", uri, e);
    } catch (InterruptedException e) {
      log.error("Interrupted during request {}", uri, e);
      Thread.currentThread().interrupt();
    }
    return null;
  }

  private HttpRequest accessRequest() {
    String body =
        "grant_type=client_credentials"
            + "&client_id="
            + URLEncoder.encode(apiSettings.clientId(), StandardCharsets.UTF_8)
            + "&client_secret="
            + URLEncoder.encode(apiSettings.clientSecrets(), StandardCharsets.UTF_8);

    return HttpRequest.newBuilder()
        .uri(URI.create(ACCESS_REQUEST_URL))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(BodyPublishers.ofString(body))
        .build();
  }

  private HttpRequest getRequest(URI uri) {
    var builder = HttpRequest.newBuilder().uri(uri).header("Accept", "application/json").GET();
    // without a token OpenSky still answers, just with anonymous rate limits
    if (bearer != null) {
      builder.header("Authorization", "Bearer " + bearer);
    }
    return builder.build();
  }
}
