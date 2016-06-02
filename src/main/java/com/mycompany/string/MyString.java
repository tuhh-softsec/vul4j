/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.string;

public final class MyString {

  private MyString() {}

  public static void printChars(final String str) {
    str.chars()
      .mapToObj(ch -> (char) ch)
      .forEach(System.out::println);
  }

  public static void printDigits(final String str) {
    str.chars()
      .filter(Character::isDigit)
      .forEach(MyString::printChar);
  }

  private static void printChar(final int ch) {
    System.out.println((char) ch);
  }

}
