/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.collections;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyCollectionTest {

  private MyCollection collection;
  
  private Predicate<String> checkIfStartsWith(final String letter) {
    return element -> element.startsWith(letter);
  }
  
  private final Function<String, Predicate<String>> startsWithLetter =
          letter -> element -> element.startsWith(letter);

  @Before
  public void setUp() {
    collection = new MyCollection(Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott"));
  }

  @Test
  public void testPrintAll() {
    collection.printAll();
  }

  @Test
  public void testIterateThroughCollection() {
    collection.iterateThroughCollection(System.out::println);
  }

  @Test
  public void testPrintCaps() {
    collection.printCaps();
  }

  @Test
  public void testConvertToCaps() {
    assertEquals(Arrays.asList("BRIAN", "NATE", "NEAL", "RAJU", "SARA", "SCOTT"),
        collection.convertToCaps());
  }

  @Test
  public void testPrintNumberOfCharactersInElement() {
    collection.printNumberOfCharactersInElement();
  }

  @Test
  public void testGetNumberOfCharactersInElement() {
    assertEquals(Arrays.asList(5, 4, 4, 4, 4, 5), collection.getNumberOfCharactersInElement());
  }

  @Test
  public void testTransformCollection() {
    assertEquals(Arrays.asList("brian", "nate", "neal", "raju", "sara", "scott"),
            collection.transformCollection(String::toLowerCase));
  }

  @Test
  public void testPickElementsThatStartWithN() {
    assertEquals(Arrays.asList("Nate", "Neal"), collection.pickElementsThatStartWithN());
  }

  @Test
  public void testFindElements() {
    assertEquals(Arrays.asList("Sara", "Scott"), collection.findElements(element -> element.startsWith("S")));
  }

  @Test
  public void testCountElementsUsingPredicate() {
    assertEquals(2, collection.countElements(checkIfStartsWith("N")));
  }
  
  @Test
  public void testCountElementsUsingFunction() {
    assertEquals(1, collection.countElements(startsWithLetter.apply("R")));
  }
  
  @Test
  public void testPickFirstElementWhichStartsWith() {
    assertEquals("Nate", collection.pickFirstElementWhichStartsWith("N"));
    assertEquals("No element found", collection.pickFirstElementWhichStartsWith("Z"));
  }
  
  @Test
  public void testGetTotalNumberOfCharactersInAllElements() {
    assertEquals(26, collection.getTotalNumberOfCharactersInAllElements());
  }
  
  @Test
  public void testFindLongestLength() {
    assertEquals(5, collection.findLongestLength());
  }
  
  @Test
  public void testFindShortestLength() {
    assertEquals(4, collection.findShortestLength());
  }
  
  @Test
  public void testFindAverageLength() {
    assertEquals(4.333, collection.findAverageLength(), 3);
  }
  
  @Test
  public void testGetLongestElement() {
    assertEquals(Optional.of("Brian"), collection.getLongestElement());
  }
  
  @Test
  public void testGetLongestElementWithDefaultValue() {
    assertEquals("Steve", collection.getLongestElementWithDefaultValue("Steve"));
    assertEquals("Brian", collection.getLongestElementWithDefaultValue("Joe"));
  }
  
  @Test
  public void testJoinElements() {
    assertEquals("Brian, Nate, Neal, Raju, Sara, Scott", collection.joinElements(", "));
  }
  
  @Test
  public void testCollectTransformedElementsIntoStringConcatenatedWithCommas() {
    assertEquals("BRIAN, NATE, NEAL, RAJU, SARA, SCOTT",
      collection.collectTransformedElementsIntoStringConcatenatedWithCommas());
  }

}
