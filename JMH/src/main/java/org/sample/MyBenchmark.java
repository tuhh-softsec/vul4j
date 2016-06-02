/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */

/*
 * Tests taken from Java Magazine March/April 2015 "The Quantum Physics of Java".
 * Compares running two loops over the array with 67,000 integer elements.
 *
 * 1st loop: changes every element
 * 2nd loop: changes every sixteenth element
 */
package org.sample;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(5)
public class MyBenchmark {

  private static final int ARRAY_SIZE = 64 * 1024 * 1024;
  private int[] array = new int[ARRAY_SIZE];

  @Benchmark
  public void testLoop1() {
    for (int i = 0, n = array.length; i < n; i++) {
      array[i] *= 3;
    }
  }

  @Benchmark
  public void testLoop2() {
    for (int i = 0, n = array.length; i < n; i += 16) {
      array[i] *= 3;
    }
  }

}
