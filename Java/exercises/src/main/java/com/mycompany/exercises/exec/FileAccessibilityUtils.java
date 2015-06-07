/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileAccessibilityUtils {

  private FileAccessibilityUtils() {

  }

  public static boolean ensureFileIsAccessible(final Path path) {
    return ensureFileExists(path) && ensureFileIsRegular(path) && ensureFileIsReadable(path)
        && ensureFileIsExecutable(path);
  }

  private static boolean ensureFileExists(final Path scriptFileLocation) {
    if (Files.exists(scriptFileLocation)) {
      return true;
    } else {
      System.err.println("File " + scriptFileLocation + " does not exist.");
      return false;
    }

  }

  private static boolean ensureFileIsRegular(final Path scriptFileLocation) {
    if (Files.isRegularFile(scriptFileLocation)) {
      return true;
    } else {
      System.err.println("File " + scriptFileLocation + " is not a regular file.");
      return false;
    }

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
