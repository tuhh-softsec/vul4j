/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.io;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileAccessibilityUtils {

  private FileAccessibilityUtils() {}

  public static boolean ensureFileIsAccessible(final Path path) {
    return Files.exists(path) && Files.isRegularFile(path) && ensureFileIsReadable(path)
        && ensureFileIsExecutable(path);
  }

  private static boolean ensureFileIsReadable(final Path scriptFileLocation) {
    if (Files.isReadable(scriptFileLocation)) {
      return true;
    } else {
      System.err.println("File " + scriptFileLocation + " is not readable.");
      return attemptToSetReadable(scriptFileLocation);
    }
  }

  private static boolean attemptToSetReadable(final Path scriptFileLocation) {
    scriptFileLocation.toFile().setReadable(true);
    if (Files.isReadable(scriptFileLocation)) {
      System.out.println("Set file readable.");
      return true;
    } else {
      System.err.println("Failed to set file readable.");
      return false;
    }
  }

  private static boolean ensureFileIsExecutable(final Path scriptFileLocation) {
    if (Files.isExecutable(scriptFileLocation)) {
      return true;
    } else {
      System.err.println("File " + scriptFileLocation + " is not executable.");
      return attemptToSetExecutable(scriptFileLocation);
    }
  }

  private static boolean attemptToSetExecutable(final Path scriptFileLocation) {
    scriptFileLocation.toFile().setExecutable(true);
    if (Files.isExecutable(scriptFileLocation)) {
      System.out.println("Set file executable.");
      return true;
    } else {
      System.err.println("Failed to set file executable.");
      return false;
    }
  }

}
