/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.concurrency;

import java.util.concurrent.locks.Lock;

/**
 * Class to manage locks.
 */
public final class Locker {

  private Locker() {}

  /**
   * Wraps critical concurrent sections.
   */
  public static void runLocked(final Lock lock, final Runnable block) {
    lock.lock();
    try {
      block.run();
    } finally {
      lock.unlock();
    }
  }

}
