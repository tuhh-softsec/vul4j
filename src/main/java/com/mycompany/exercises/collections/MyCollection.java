/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.collections;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.stream.Collectors.*;

/**
 * Can/should be refactored to a utils class. Each method should take a collection of Strings as a
 * parameter? If so, a collection might be empty then an Optional should be returned everywhere.
 */
public class MyCollection {

  private final List<String> collection;

  public MyCollection(final List<String> collection) {
    this.collection = collection;
  }

  public void printAll() {
    collection.forEach(System.out::println);
  }

  public void iterateThroughCollection(final Consumer<String> consumer) {
    collection.forEach(consumer);
  }

  // TODO
  public static void processEachElement(final Collection collection, final Consumer consumer) {
    collection.forEach(consumer);
  }

  public void printCaps() {
    collection.stream()
      .map(String::toUpperCase)
      .forEach(element -> System.out.print(element + " "));
    System.out.println();
  }

  public List<String> convertToCaps() {
    return collection.stream()
      .map(String::toUpperCase)
      .collect(toList());
  }

  public void printNumberOfCharactersInElement() {
    collection.stream()
      .map(String::length)
      .forEach(count -> System.out.print(count + " "));
    System.out.println();
  }

  public List<Integer> getNumberOfCharactersInElement() {
    return collection.stream()
      .map(String::length)
      .collect(toList());
  }

  public List<String> transformCollection(final Function<String, String> function) {
    return collection.stream().map(function).collect(toList());
  }

  public List<String> pickElementsThatStartWithN() {
    final List<String> startsWithN = 
      collection.stream()
        .filter(element -> element.startsWith("N"))
        .collect(toList());
    System.out.print(String.format("Found %d elements: ", startsWithN.size()));
    System.out.println(startsWithN);
    return startsWithN;
  }

  public List<String> findElements(final Predicate<String> predicate) {
    final List<String> elements = this.collection.stream().filter(predicate).collect(toList());
    System.out.print(String.format("Found %d elements: ", elements.size()));
    System.out.println(elements);
    return elements;
  }

  public long countElements(final Predicate<String> predicate) {
    final long count = collection.stream().filter(predicate).count();
    System.out.println(String.format("Number of elements: %d", count));
    return count;
  }

  public String pickFirstElementWhichStartsWith(final String startingLetter) {
    final Optional<String> foundElement = 
            collection.stream()
            .filter(element -> element.startsWith(startingLetter))
            .findFirst();
    System.out.println(String.format("An element starting with %s: %s", startingLetter,
      foundElement.orElse("No element found")));
    foundElement.ifPresent(element -> System.out.println("Hello " + element));
    return foundElement.orElse("No element found");
  }

  public int getTotalNumberOfCharactersInAllElements() {
    return collection.stream()
            .mapToInt(element -> element.length())
            .sum();
  }

  public int findLongestLength() {
    return collection.stream()
            .mapToInt(element -> element.length())
            .max()
            .getAsInt();
  }

  public int findShortestLength() {
    return collection.stream()
            .mapToInt(element -> element.length())
            .min()
            .getAsInt();
  }

  public double findAverageLength() {
    return collection.stream()
            .mapToInt(element -> element.length())
            .average()
            .getAsDouble();
  }

  /**
   * If there is more than one element with the same longest length, return the first one we find.
   * Demonstrates a lightweight application of the strategy pattern.
   */
  public Optional<String> getLongestElement() {
    final Optional<String> aLongElement =
      collection.stream()
      .reduce((element1, element2) -> element1.length() >= element2.length() ? element1 : element2);
    aLongElement.ifPresent(element -> System.out.println(String.format("A longest element: %s", element)));
    return aLongElement;
  }

  /**
   * If any element is longer than the given base (default value), it would get picked up, otherwise
   * the method would return the base value.
   */
  public String getLongestElementWithDefaultValue(final String defaultValue) {
    return collection.stream()
      .reduce(defaultValue, (element1, element2) ->
        element1.length() >= element2.length() ? element1 : element2);
  }

  public String joinElements(final String delimiter) {
    return String.join(delimiter, collection);
  }

  /**
   * Return elements in uppercase and comma separated.
   */
  public String collectTransformedElementsIntoStringConcatenatedWithCommas() {
    return collection.stream()
      .map(String::toUpperCase)
      .collect(joining(", "));
  }
}
