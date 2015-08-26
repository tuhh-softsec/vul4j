/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.math;

import java.util.List;
import java.util.function.Predicate;

public final class SampleUtils {

  private SampleUtils() {}

  public static Predicate<Integer> isGreaterThan(final int pivot) {
    return number -> number > pivot;
  }

  public static int totalValues(final List<Integer> numbers, final Predicate<Integer> predicate) {
    return numbers.stream()
      .filter(predicate)
      .reduce(0, Math::addExact);
  }
}
