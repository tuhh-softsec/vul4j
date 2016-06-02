/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.collections.comparator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person {

  private final String name;
  private final int age;

  public int ageDifference(final Person other) {
    return age - other.age;
  }

}
