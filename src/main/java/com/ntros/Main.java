package com.ntros;

import com.ntros.config.OpenSkyConfig;
import com.ntros.runtime.ControlToken;
import com.ntros.runtime.RuntimeContext;
import com.ntros.fetcher.ApiSettings;
import com.ntros.runtime.RuntimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Hello world! */
public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);
  private static final int DEFAULT_API_POLL_RATE_SECONDS = 52;

  static void main() {
    System.out.println("Hello World!");
    ApiSettings apiSettings = OpenSkyConfig.fromEnvironment(DEFAULT_API_POLL_RATE_SECONDS * 1000);

    RuntimeContext runtimeContext = new RuntimeContext(new ControlToken(), apiSettings);

    RuntimeController controller = new RuntimeController(runtimeContext);
    Runtime.getRuntime().addShutdownHook(new Thread(submitShutdown(controller), "shutdown-hook-1"));

    controller.start();
  }

  private static Runnable submitShutdown(LifeCycle lifeCycle) {
    return () -> {
      try {
        lifeCycle.awaitTermination();
      } catch (InterruptedException e) {
        log.error("Failed during shutdown", e);
      }
    };
  }
}
