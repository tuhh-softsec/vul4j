/**
 * Copyright (c) 2016 Company.
 * All rights reserved.
 */
package com.mycompany.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchive {

  private final Collection<Path> files;

  /**
   * Compresses all given files.
   */
  public ZipArchive(final Collection<Path> files) {
    this.files = files;
  }

  public void compressTo(final Path destination) throws IOException {
    try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(destination)))) {
      files.forEach(file -> writeDataToZipFile(file, out));
    }
  }

  private void writeDataToZipFile(final Path file, final ZipOutputStream out) {
    try {
      ZipEntry entry = new ZipEntry(file.getFileName().toString());
      out.putNextEntry(entry);
      byte[] bytes = Files.readAllBytes(file);
      out.write(bytes);
    } catch (IOException ex) {
      System.err.println("ERROR! Failed to write " + file + " to the ZIP archive: " + ex.getMessage());
    }
  }

}
