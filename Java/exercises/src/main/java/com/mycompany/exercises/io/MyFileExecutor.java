/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class MyFileExecutor {

  private MyFileExecutor() {}

  public static void main(final String[] args) {
    Path file = Paths.get("/", "home", "bredkins", "Desktop", "test.txt");
    new FileGenerator().generateFile(file);
    MyFile.processStringsFromFile(file);
  }

}
