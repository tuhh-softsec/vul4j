/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.collections.comparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.*;

public final class MyComparator {
    
  private static Comparator<Person> byName = Comparator.comparing(Person::getName);
  private static Comparator<Person> byAge = Comparator.comparing(Person::getAge);
          
  private MyComparator() {}
    
  /**
   * Sort in ascending order by age.
   */
  public static List<Person> getAscendingAge(final List<Person> people) {
    return people.stream()
      .sorted(byAge)
      .collect(toList());
  }

  /**
   * Sort in descending order by age.
   */
  public static List<Person> getDescendingAge(final List<Person> people) {
    return people.stream()
      .sorted(byAge.reversed())
      .collect(toList());
  }
  
  /**
   * Sort in ascending order by name.
   */
  public static List<Person> getAscendingName(final List<Person> people) {
    return people.stream()
      .sorted(byName)
      .collect(toList());
  }

  /**
   * Sort in descending order by name.
   */
  public static List<Person> getDescendingName(final List<Person> people) {
    return people.stream()
      .sorted(byName.reversed())
      .collect(toList());
  }
  
  /**
   * Sort in ascending order by age and name.
   */
  public static List<Person> getAscendingAgeAndName(final List<Person> people) {
    return people.stream()
      .sorted(byAge.thenComparing(byName))
      .collect(toList());
  }

  /**
   * Sort in descending order by age and name.
   */
  public static List<Person> getDescendingAgeAndName(final List<Person> people) {
    return people.stream()
      .sorted(byAge.thenComparing(byName).reversed())
      .collect(toList());
  }

  public static Optional<Person> getYoungest(final List<Person> people) {
    return people.stream()
      .min(Person::ageDifference);
  }

  public static Optional<Person> getEldest(final List<Person> people) {
    return people.stream()
      .max(Person::ageDifference);
  }

  public static List<Person> getOlderThan20(final List<Person> people) {
    return people.stream()
      .filter(person -> person.getAge() > 20)
      .collect(toList());
  }

  /**
   * Group people by their age.
   */
  public static Map<Integer, List<Person>> getPeopleByAge(final List<Person> people) {
    return people.stream()
      .collect(groupingBy(Person::getAge));
  }

  /**
   * Get people's names ordered by age.
   */
  public static Map<Integer, List<String>> getNameOfPeopleByAge(final List<Person> people) {
    return people.stream()
      .collect(groupingBy(Person::getAge, mapping(Person::getName, toList())));
  }
  
  /**
   * Group people's names by their first character and then get the oldest person in each group.
   */
  public static Map<Character, Optional<Person>> getOldestPersonOfEachLetter(final List<Person> people) {
    return people.stream()
      .collect(groupingBy(person -> person.getName().charAt(0), reducing(maxBy(byAge))));
  }

  public static void printPeople(final String message, final List<Person> people) {
    System.out.println(message);
    people.forEach(System.out::println);
  }
  
  public static void printPeople(final String message, final Map people) {
    System.out.println(message + people);
  }

}
