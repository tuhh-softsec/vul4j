/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.math;

import static com.mycompany.math.SampleUtils.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Functional Programming with Java 8.
 * 
 * source: https://www.youtube.com/watch?v=Ee5t_EGjv0A
 */
public class Sample {

  private static final long ONE_SECOND = 1_000;

  public static void main(final String[] args) {
    System.out.println("1 is prime: " + isPrime(1));
    System.out.println("2 is prime: " + isPrime(2));
    System.out.println("3 is prime: " + isPrime(3));
    System.out.println("4 is prime: " + isPrime(4));

    List<Integer> values = Arrays.asList(1, 2, 3, 5, 4, 6, 7, 8, 9, 10);
    System.out.println("Values: " + values);
    System.out.println("Double of the first even number greater than 3: "
        + findDoubleOfFirstEvenNumberGreaterThan3(values));
    
    System.out.println("Total values, all: " + totalValues(values, e -> true));
    System.out.println("Total values, even: " + totalValues(values, e -> e % 2 == 0));
    
    System.out.println("Total of doubled values: " + doubleValuesAndSumItUp(values));
  }

  public static boolean isPrime(final int number) {
    IntPredicate isDivisible = divisor -> number % divisor == 0;
    return number > 1 && IntStream.range(2, number).noneMatch(isDivisible);
  }

  public static int findDoubleOfFirstEvenNumberGreaterThan3(final List<Integer> values) {
    Predicate<Integer> isEven = number -> number % 2 == 0;
    Function<Integer, Integer> doubleValue = number -> number * 2;

    Integer result = values.stream()
      .filter(isEven)
      .filter(isGreaterThan(3))
      .map(doubleValue)
      .findFirst()
      .get();
    return result;
  }

  public static int doubleValuesAndSumItUp(final List<Integer> values) {
    // demonstrate power of parallel execution compared to sequential
    return values.parallelStream()
            .mapToInt(Sample::doubleValue)
            .sum();
  }

  private static int doubleValue(final int number) {
    // simulate long execution
    try {
      Thread.sleep(ONE_SECOND);
    } catch (Exception ex) {
      System.err.println("ERROR! Failed to cause the currently executing thread to sleep.");
    }
    return number * 2;
  }

}
