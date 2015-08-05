/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileGenerator {

  private static final int NUMBER_OF_LINES = 700_000;
  private static final List<String> EXAMPLE_LINES = new ArrayList<String>() {
    {
      add("Blackened Recordings ");
      add("Divergent Series: Insurgent");
      add("Google Something");
      add("Pixels Movie Money");
      add("X Ambassadors");
      add("Power Path Pro Advanced");
      add("Something CYRFZQ");
      add("");
    }
  };

  public void generateFile(final Path file) {
    try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
      for (int i = 0; i <= NUMBER_OF_LINES; i++) {
        String s = getRandomString();
        writer.write(s, 0, s.length());
        writer.newLine();
      }
    } catch (IOException ex) {
      System.err.format("IOException: %s%n", ex);
    }
  }

  private String getRandomString() {
    Random rand = new Random();
    int value = rand.nextInt(EXAMPLE_LINES.size());
    return EXAMPLE_LINES.get(value);
  }

}
