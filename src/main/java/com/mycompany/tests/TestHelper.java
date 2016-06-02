/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.tests;

import static org.junit.Assert.fail;

/**
 * Looks for exceptions using lambda expressions.
 */
public final class TestHelper {

  private TestHelper() {}

  /**
   * Checks if the block throws the expected exception.
   */
  public static <X extends Throwable> Throwable assertThrows(final Class<X> exceptionClass,
      final Runnable block) {
    try {
      block.run();
    } catch (Throwable ex) {
      if (exceptionClass.isInstance(ex)) {
        return ex;
      }
    }
    fail("Failed to throw expected exception ");
    return null;
  }

}
