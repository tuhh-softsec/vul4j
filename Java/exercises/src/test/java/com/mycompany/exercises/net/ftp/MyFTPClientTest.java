/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.apache.commons.io.FileUtils.contentEquals;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MyFTPClientTest {

  private static final String USERNAME = "anonymous";
  private static final String PASSWORD = "anonymous";
  private static final String SERVER = "localhost";
  private static final int PORT = 9187;

  private static final Path TEST_RESOURCES_DIR = Paths.get("src", "test", "resources");
  private static final Path FTP_DIRECTORY = TEST_RESOURCES_DIR.resolve("ftp_dir");
  private static final String TMP_DIRECTORY = System.getProperty("java.io.tmpdir");

  private static FtpServerMock serverMock;
  private MyFTPClient ftpClient;
  private FTPConnectionProperties ftpProperties;

  @Before
  public void setUp() {
    serverMock = new FtpServerMock(PORT, USERNAME, PASSWORD, FTP_DIRECTORY.toFile());
    serverMock.addDirectoryAndAllFilesRecursively(FTP_DIRECTORY.toFile());
    serverMock.startServer();
    ftpClient = new MyFTPClient();
    ftpProperties =
        new FTPConnectionProperties.Builder(SERVER).directory(FTP_DIRECTORY).port(PORT).build();
  }

  @After
  public void tearDown() {
    serverMock.stopServer();
  }

  @Test
  public void testObtainListOfFileInformationAnonymous() {
    List<FTPFile> ftpFiles = ftpClient.obtainListOfFileInformationAnonymous(ftpProperties);

    String currentDate = getCurrentDate();
    assertEquals(3, ftpFiles.size());
    assertEquals("drwxrwxrwx  1 none     none                   0 " + currentDate + " main",
        ftpFiles.get(1).toString());
    assertEquals("-rwxrwxrwx  1 none     none                1072 " + currentDate + " Release",
        ftpFiles.get(0).toString());
  }

  @Test
  public void testDownloadFile() throws IOException {
    Path remoteFile = FTP_DIRECTORY.resolve("Release");
    Path localFile = Paths.get(TMP_DIRECTORY, "LocalRelease");
    boolean fileDownloaded = ftpClient.downloadFile(ftpProperties, "Release", localFile);

    assertTrue(fileDownloaded);
    assertTrue(contentEquals(remoteFile.toFile(), localFile.toFile()));

    Files.delete(localFile);
  }

  private String getCurrentDate() {
    LocalDate date = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd  yyyy");
    return date.format(formatter);
  }

}
