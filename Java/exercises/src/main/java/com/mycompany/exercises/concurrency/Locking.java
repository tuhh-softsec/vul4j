/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.concurrency;

import static com.mycompany.exercises.concurrency.Locker.runLocked;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Locking {

  private Lock lock = new ReentrantLock(); // or mock

  protected void setLock(final Lock lock) {
    this.lock = lock;
  }

  public void doOp1() {
    runLocked(lock, () -> { /*...critical code...*/ });
  }

  public void doOp2() {
    runLocked(lock, () -> { /*...critical code...*/ });
  }

  public void doOp3() {
    runLocked(lock, () -> { /*...critical code...*/ });
  }
}
