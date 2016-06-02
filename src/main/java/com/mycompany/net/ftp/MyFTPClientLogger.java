/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.net.ftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class MyFTPClientLogger {

  private MyFTPClientLogger() {}

  public static void logLoginSuccessful(final String username, final String reply) {
    System.out.println("User " + username + " logged to FTP server.");
    System.out.print("FTP server response: " + reply);
  }

  public static void logEnterPassiveLocalDataConnectionModeSuccessful() {
    System.out
        .println("Set the current data connection mode to PASSIVE_LOCAL_DATA_CONNECTION_MODE.");
  }

  public static void logConnectionSuccessful(final String hostname, int port, final String reply) {
    System.out.println("Connected to FTP server " + hostname + ":" + port + ".");
    System.out.print("FTP server response: " + reply);
  }

  public static void logConnectionSuccessful(final String hostname, final String reply) {
    System.out.println("Connected to FTP server " + hostname + ".");
    System.out.print("FTP server response: " + reply);
  }

  public static void logConnectionRefused(final String hostname, int port, final String reply) {
    System.err.println("FTP server " + hostname + ":" + port + " refused connection.");
    System.err.println("FTP server response: " + reply);
  }

  public static void logConnectionRefused(final String hostname, final String reply) {
    System.err.println("FTP server " + hostname + " refused connection.");
    System.err.println("FTP server response: " + reply);
  }

  public static void logLoginRefused(final String username, final String reply) {
    System.err.println("Login to FTP server using the provided username " + username
        + " and password unsuccessful.");
    System.err.println("FTP server response: " + reply);
  }

  public static void logConnectionError(final String hostname, int port, final IOException ex) {
    System.err.println("Error while getting FTP files from " + hostname + ":" + port + ".");
    System.err.println("------ IOException ------");
    System.err.println("Message: " + ex.getMessage());
  }

  public static void logConnectionError(final String hostname, final IOException ex) {
    System.err.println("Error while getting FTP files from " + hostname);
    System.err.println("------ IOException ------");
    System.err.println("Message: " + ex.getMessage());
  }

  public static void logCloseConnectionFailed(final IOException ex) {
    System.err.println("Failed to close FTP server connectioin.");
    System.err.println("Message: " + ex.getMessage());
  }

  public static void logFileDownloadedSuccessfully(final String remoteFile, final Path localFile) {
    System.out.println("Downloaded \"" + remoteFile + "\" from FTP server into \"" + localFile
        + "\".");
    System.out.println("Size: " + getFileSizeInKilobytes(localFile.toFile()) + "KB.");
  }

  public static void logFileDownloadFailed(final String remoteFile) {
    System.err.println("Failed to download \"" + remoteFile + "\" from FTP server.");
  }

  // TODO to be moved into a new file project
  private static long getFileSizeInKilobytes(final File file) {
    long bytes = file.length();
    return bytes / 1024;
  }


}
