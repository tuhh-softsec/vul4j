/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Implements the execute around method (EAM) pattern.
 */
public final class FileWriterEAM implements AutoCloseable {

  private final FileWriter writer;

  private FileWriterEAM(final String fileName) throws IOException {
    writer = new FileWriter(fileName);
  }

  @Override
  public void close() throws IOException {
    System.out.println("close called automatically...");
    writer.close();
  }

  public void writeStuff(final String message) throws IOException {
    writer.write(message);
  }

  public static void use(final String fileName, final UseInstance<FileWriterEAM, IOException> block)
      throws IOException {
    try (FileWriterEAM writerEAM = new FileWriterEAM(fileName)) {
      block.accept(writerEAM);
    }
  }

}
