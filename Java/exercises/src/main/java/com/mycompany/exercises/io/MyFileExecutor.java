/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class MyFileExecutor {

  private MyFileExecutor() {}

  public static void main(final String[] args) {
    Path file = Paths.get("/", "home", "bredkins", "Desktop", "test.txt");
    new FileGenerator().generateFile(file);
    MyFile.processStringsFromFile(file);

    try {
      FileWriterEAM.use("eam.txt", writerEAM -> writerEAM.writeStuff("sweet"));

      FileWriterEAM.use("eam2.txt", writerEAM -> {
          writerEAM.writeStuff("how");
          writerEAM.writeStuff("sweet");
        });
    } catch (IOException ex) {
      System.err.println("ERROR! Failed to write files.");
    }
  }
}
