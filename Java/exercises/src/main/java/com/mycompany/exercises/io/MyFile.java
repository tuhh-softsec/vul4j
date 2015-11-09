/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
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

  public static List<Path> listFilesInCurrentDirectory() {
    return listFilesInDirectory(Paths.get("."));
  }

  public static List<Path> listFilesInDirectory(final Path directory) {
    try {
      return Files.list(directory).collect(toList());
    } catch (IOException ex) {
      System.err.println("Failed to list the names of all the files in " + directory + ": "
          + ex.getMessage());
    }
    return Collections.emptyList();
  }

  public static List<Path> listSubdirectoriesInCurrentDirectory() {
    return listSubdirectoriesInDirectory(Paths.get("."));
  }

  public static List<Path> listSubdirectoriesInDirectory(final Path directory) {
    try {
      return Files.list(directory)
        .filter(Files::isDirectory)
        .collect(toList());
    } catch (IOException ex) {
      System.err.println("Failed to list the names of all the subdirectories in " + directory + ": "
        + ex.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * List all files which names end with the specified suffix in a directory.
   */
  public static List<Path> listSelectFilesInDirectory(final Path directory,
      final String suffix) {
    List<Path> files = new ArrayList<>();
    DirectoryStream.Filter<Path> filter = path -> path.toString().endsWith(suffix);
    try {
      Files.newDirectoryStream(directory, filter).forEach(path -> files.add(path));
    } catch (IOException ex) {
      System.err.println("Failed to list the names of *" + suffix + " files in " + directory + ": "
        + ex.getMessage());
    }
    return files;
  }

  public static List<File> listHiddenFilesInDirectory(final Path directory) {
    return Stream.of(new File(directory.toString())
      .listFiles(File::isHidden))
      .collect(toList());
  }

  /**
   * List the immediate (one level deep) subdirectories in a given directory.
   */
  public static List<File> listImmediateSubdirectoriesInDirectory(final Path directory) {
    return Stream.of(new File(directory.toString())
      .listFiles())
      .flatMap(file -> file.listFiles() == null ? Stream.of(file) : Stream.of(file.listFiles()))
      .collect(toList());
  }

  // TODO
  // list all files -> use it in FTPServerMock

  // TODO - investigate Optional
  public static void watchForFileChangesInDirectory(final Path directory) {
    WatchService watchService = registerWatchServiceToObserveAnyChangeToDirectory(directory);
    pollWatchServiceForAnyChangeToFilesInDirectory(watchService);
  }


  private static WatchService registerWatchServiceToObserveAnyChangeToDirectory(final Path directory) {
    try {
      WatchService watchService = directory.getFileSystem().newWatchService();
      directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
      System.out.println("Report any file changed within next 1 minute...");
      return watchService;
    } catch (IOException ex) {
      System.err.println("ERROR! Failed to registers the directory " + directory
          + " with a watch service: " + ex.getMessage());
    }
    return null;
  }

  private static void pollWatchServiceForAnyChangeToFilesInDirectory(final WatchService watchService) {
    try {
      WatchKey watchKey = watchService.poll(1, TimeUnit.MINUTES);
      if (watchKey != null) {
        watchKey.pollEvents().stream()
          .forEach(event -> System.out.println(event.context()));
      }
    } catch (InterruptedException ex) {
      System.err.println("ERROR! Watch service was interrupted while waiting: " + ex.getMessage());
    }
  }

  public static List<String> getWords(final Path path) throws IOException {
    Stream<String> lines = Files.lines(path);
    Stream<String> words = lines.flatMap(line -> Stream.of(line.split(" +")));
    return words.filter(MyFile::isWord).collect(toList());
  }

  /**
   * Checks if string has only word characters: [a-zA-Z_0-9]
   */
  private static boolean isWord(final String s) {
    return Pattern.matches("\\w+", s);
  }

}
