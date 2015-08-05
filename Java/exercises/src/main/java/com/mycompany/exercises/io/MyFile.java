/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class MyFile {

  private MyFile() {}

  /**
   * Process lines from a file. Uses Files.lines() method which take advantage of Stream API
   * introduced in Java 8.
   * 
   * @param file
   */
  public static void processStringsFromFile(final Path file) {
    try (Stream<String> lines = Files.lines(file)) {
      lines.map(s -> s.trim())
        .filter(s -> !s.isEmpty())
        .filter(s -> !s.startsWith("#"))
        .filter(s -> s.contains("Something"))
        .forEach(MyFile::doSomething);
    } catch (IOException ex) {
      logProcessStringsFailed(ex);        
    }
  }

  private static void doSomething(final String s) {
    System.out.println(s);
  }

  private static void logProcessStringsFailed(final IOException ex) {
    System.err.println("ERROR! Failed to process strings.");
    System.err.println("Message: " + ex.getMessage());
  }

}
