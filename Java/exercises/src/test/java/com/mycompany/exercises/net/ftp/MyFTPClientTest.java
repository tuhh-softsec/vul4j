/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  private static final String FTP_DIRECTORY = "src/test/resources/ftp_dir";
  private static final String TMP_DIRECTORY = System.getProperty("java.io.tmpdir");

  private static FtpServerMock serverMock;
  private MyFTPClient ftpClient;
  private FTPConnectionProperties ftpProperties;

  @Before
  public void setUp() {
    serverMock = new FtpServerMock(PORT, USERNAME, PASSWORD, new File(FTP_DIRECTORY));
    serverMock.addDirectoryAndAllFilesRecursively(new File(FTP_DIRECTORY));
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

    assertEquals(2, ftpFiles.size());
    assertEquals("drwxrwxrwx  1 none     none                   0 Jul 24  2015 main",
        ftpFiles.get(0).toString());
    assertEquals("-rwxrwxrwx  1 none     none                1072 Jul 24  2015 Release", ftpFiles
        .get(1).toString());
  }

  @Test
  public void testDownloadFile() throws IOException {
    Path remoteFile = Paths.get(FTP_DIRECTORY, "Release");
    Path localFile = Paths.get(TMP_DIRECTORY, "LocalRelease");
    boolean fileDownloaded = ftpClient.downloadFile(ftpProperties, "Release", localFile);

    assertTrue(fileDownloaded);
    assertTrue(contentEquals(remoteFile.toFile(), localFile.toFile()));

    Files.delete(localFile);
  }

}
