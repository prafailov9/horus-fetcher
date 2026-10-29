package com.ntros.runtime;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControlToken {
  private final AtomicBoolean token = new AtomicBoolean(true);

  public boolean isValid() {
    return token.get();
  }

  public void invalidate() {
    token.set(false);
  }
}
