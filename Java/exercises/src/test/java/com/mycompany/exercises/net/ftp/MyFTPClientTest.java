/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

import java.io.File;
import java.util.List;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyFTPClientTest {

  private static final String USERNAME = "anonymous";
  private static final String PASSWORD = "anonymous";
  private static final String SERVER = "localhost";
  private static final int PORT = 9187;

  private static final String FTP_DIRECTORY = "src/test/resources/ftp_dir";

  private static FtpServerMock serverMock;
  private MyFTPClient ftpClient;
  private FTPConnectionProperties ftpProperties;

  @BeforeClass
  public static void setUpClass() {
    serverMock = new FtpServerMock(PORT, USERNAME, PASSWORD, new File(FTP_DIRECTORY));
    serverMock.addDirectoryAndAllFilesRecursively(new File(FTP_DIRECTORY));
    serverMock.startServer();
  }

  @AfterClass
  public static void tearDownClass() {
    serverMock.stopServer();
  }

  @Test
  public void testObtainListOfFileInformationAnonymous() {
    ftpClient = new MyFTPClient();
    ftpProperties =
        new FTPConnectionProperties.Builder(SERVER).directory(FTP_DIRECTORY).port(PORT).build();

    List<FTPFile> ftpFiles = ftpClient.obtainListOfFileInformationAnonymous(ftpProperties);
    assertEquals(2, ftpFiles.size());
    assertEquals("drwxrwxrwx  1 none     none                   0 Jul 24  2015 main",
        ftpFiles.get(0).toString());
    assertEquals("-rwxrwxrwx  1 none     none                1072 Jul 24  2015 Release", ftpFiles
        .get(1).toString());
  }
}
