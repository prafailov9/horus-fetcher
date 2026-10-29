package com.ntros;

public interface LifeCycle {

  void start();
  void awaitTermination() throws InterruptedException;

}
