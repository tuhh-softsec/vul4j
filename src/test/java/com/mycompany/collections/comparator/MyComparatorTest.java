/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.collections.comparator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MyComparatorTest {

  private static final Person JOHN = new Person("John", 20);
  private static final Person SARA = new Person("Sara", 21);
  private static final Person JANE = new Person("Jane", 21);
  private static final Person GREG = new Person("Greg", 35);

  private final List<Person> people = Arrays.asList(JOHN, SARA, JANE, GREG);
  private final List<Person> ascendingAge = Arrays.asList(JOHN, SARA, JANE, GREG);
  private final List<Person> descendingAge = Arrays.asList(GREG, SARA, JANE, JOHN);
  private final List<Person> ascendingName = Arrays.asList(GREG, JANE, JOHN, SARA);
  private final List<Person> descendingName = Arrays.asList(SARA, JOHN, JANE, GREG);
  private final List<Person> ascendingAgeAndName = Arrays.asList(JOHN, JANE, SARA, GREG);
  private final List<Person> descendingAgeAndName = Arrays.asList(GREG, SARA, JANE, JOHN);
  private final List<Person> olderThan20 = Arrays.asList(SARA, JANE, GREG);
  private final Map<Integer, List<Person>> peopleByAge = new HashMap<>();
  private final Map<Integer, List<String>> nameOfPeopleByAge = new HashMap<>();
  private final Map<Character, Optional<Person>> oldestPersonOfEachLetter = new HashMap<>();

  @Before
  public void setUp() {
    peopleByAge.put(20, Arrays.asList(JOHN));
    peopleByAge.put(21, Arrays.asList(SARA, JANE));
    peopleByAge.put(35, Arrays.asList(GREG));

    nameOfPeopleByAge.put(20, Arrays.asList("John"));
    nameOfPeopleByAge.put(21, Arrays.asList("Sara", "Jane"));
    nameOfPeopleByAge.put(35, Arrays.asList("Greg"));

    oldestPersonOfEachLetter.put('S', Optional.of(SARA));
    oldestPersonOfEachLetter.put('G', Optional.of(GREG));
    oldestPersonOfEachLetter.put('J', Optional.of(JANE));
  }

  @Test
  public void testGetAscendingAge() {
    List<Person> ascendingAge = MyComparator.getAscendingAge(people);
    MyComparator.printPeople("Sorted in ascending order by age: ", ascendingAge);
    assertEquals(this.ascendingAge, ascendingAge);
  }

  @Test
  public void testGetDescendingAge() {
    List<Person> descendingAge = MyComparator.getDescendingAge(people);
    MyComparator.printPeople("Sorted in descending order by age: ", descendingAge);
    assertEquals(this.descendingAge, descendingAge);
  }

  @Test
  public void testGetAscendingName() {
    List<Person> ascendingName = MyComparator.getAscendingName(people);
    MyComparator.printPeople("Sorted in ascending order by name: ", ascendingName);
    assertEquals(this.ascendingName, ascendingName);
  }

  @Test
  public void testGetDescendingName() {
    List<Person> descendingName = MyComparator.getDescendingName(people);
    MyComparator.printPeople("Sorted in descending order by name: ", descendingName);
    assertEquals(this.descendingName, descendingName);
  }

  @Test
  public void testGetAscendingAgeAndName() {
    List<Person> ascendingAgeAndName = MyComparator.getAscendingAgeAndName(people);
    MyComparator.printPeople("Sorted in ascending order by age and name: ", ascendingAgeAndName);
    assertEquals(this.ascendingAgeAndName, ascendingAgeAndName);
  }

  @Test
  public void testGetDescendingAgeAndName() {
    List<Person> descendingAgeAndName = MyComparator.getDescendingAgeAndName(people);
    MyComparator.printPeople("Sorted in descending order by age and name: ", descendingAgeAndName);
    assertEquals(this.descendingAgeAndName, descendingAgeAndName);
  }

  @Test
  public void testGetYoungest() {
    assertEquals(Optional.of(JOHN), MyComparator.getYoungest(people));
  }

  @Test
  public void testGetEldest() {
    assertEquals(Optional.of(GREG), MyComparator.getEldest(people));
  }

  @Test
  public void testGetOlderThan20() {
    List<Person> olderThan20 = MyComparator.getOlderThan20(people);
    MyComparator.printPeople("People older than 20: ", olderThan20);
    assertEquals(this.olderThan20, olderThan20);
  }

  @Test
  public void testGroupPeopleByAge() {
    Map<Integer, List<Person>> peopleByAge = MyComparator.getPeopleByAge(people);
    MyComparator.printPeople("Grouped by age: ", peopleByAge);
    assertEquals(this.peopleByAge, peopleByAge);
  }

  @Test
  public void testGetNameOfPeopleByAge() {
    Map<Integer, List<String>> nameOfPeopleByAge = MyComparator.getNameOfPeopleByAge(people);
    MyComparator.printPeople("People grouped by age: ", nameOfPeopleByAge);
    assertEquals(this.nameOfPeopleByAge, nameOfPeopleByAge);
  }

  @Test
  public void testGetOldestPersonOfEachLetter() {
    Map<Character, Optional<Person>> oldestPersonOfEachLetter =
        MyComparator.getOldestPersonOfEachLetter(people);
    MyComparator.printPeople("Oldest person of each letter: ", oldestPersonOfEachLetter);
    assertEquals(this.oldestPersonOfEachLetter, oldestPersonOfEachLetter);
  }
}
