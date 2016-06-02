/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.string;

import com.mycompany.string.MyString;
import org.junit.Test;

public class MyStringTest {

  public MyStringTest() {}

  @Test
  public void testPrintChars() {
    String str = "w00t";
    MyString.printChars(str);
  }

  @Test
  public void testPrintDigits() {
    String str = "w00t";
    MyString.printDigits(str);
  }

}
