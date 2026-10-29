package com.ntros.runtime;

import com.ntros.LifeCycle;
import com.ntros.fetcher.DataFetcher;

public class RuntimeController implements LifeCycle {

  private final Thread fetcherThread;

  public RuntimeController(RuntimeContext runtimeContext) {
    if (runtimeContext == null) {
      throw new IllegalArgumentException("Invalid Context");
    }
    fetcherThread = new Thread(new DataFetcher(runtimeContext), "fetcher-1");
  }

  @Override
  public void start() {
    fetcherThread.start();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    fetcherThread.join();
  }
}
