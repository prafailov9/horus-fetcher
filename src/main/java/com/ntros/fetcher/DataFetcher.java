package com.ntros.fetcher;

import com.ntros.rest.ApiClient;
import com.ntros.rest.OpenSkyApiClient;
import com.ntros.runtime.ControlToken;
import com.ntros.runtime.RuntimeContext;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFetcher implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);
  private final ScheduledExecutorService accessScheduler =
      Executors.newSingleThreadScheduledExecutor();

  private final int TOKEN_RETRY_DELAY_SECONDS = 30;

  private final int pollRateMs;
  private final ApiSettings apiSettings;
  private final ControlToken token;
  private final ApiClient apiClient;
  private AccessResponse currentAccessResponse;

  public DataFetcher(RuntimeContext runtimeContext) {
    if (runtimeContext.apiSettings() == null) {
      throw new IllegalArgumentException("Empty Settings");
    }

    this.apiSettings = runtimeContext.apiSettings();
    pollRateMs = apiSettings.pollRate();
    token = runtimeContext.token();
    apiClient = new OpenSkyApiClient(apiSettings);
  }

  /**
   * Currently only fetching states
   */
  @Override
  public void run() {
    log.info("Polling every {} seconds", apiSettings.pollRate() / 1000);
    // 1. try get access
    accessScheduler.schedule(this::refreshAccessToken, 0, TimeUnit.SECONDS);
    try {
      while (token.isValid()) {
        // wait delay
        if (!pollDelay()) {
          break;
        }

        // call api with fetch settings
        var states = apiClient.getAllStates();

        if (states == null) {
          log.debug("No states returned from OpenSky");
          continue;
        }
        Set<String> uniqueCountries = new HashSet<>();
        states
            .states()
            .forEach(
                x -> {
                  log.info("StateVector: {}", x);
                  uniqueCountries.add(x.originCountry());
                });
        log.info("Total States Count: {}", states.states().size());
        log.info("Unique States Count: {}", uniqueCountries.size());
      }
    } finally {
      cleanup();
    }
  }

  /** Every refresh call reschedules the next one, calculating the delay */
  private void refreshAccessToken() {
    currentAccessResponse = apiClient.requestApiAccess();
    // schedule a bit early, before token expires so access is never restricted
    int nextDelay = TOKEN_RETRY_DELAY_SECONDS;
    if (currentAccessResponse != null && currentAccessResponse.accessToken() != null) {
      nextDelay = Math.max(currentAccessResponse.expiresInSeconds() - TOKEN_RETRY_DELAY_SECONDS, 1);
    }

    // reschedule again
    accessScheduler.schedule(this::refreshAccessToken, nextDelay, TimeUnit.SECONDS);
  }

  private boolean pollDelay() {
    try {
      Thread.sleep(pollRateMs);
      return true;
    } catch (InterruptedException e) {
      return false;
    }
  }

  private void cleanup() {
    accessScheduler.shutdownNow();
  }
}
