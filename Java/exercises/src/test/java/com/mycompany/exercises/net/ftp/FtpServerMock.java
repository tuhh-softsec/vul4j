/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FtpServerMock {

  private FakeFtpServer fakeFtpServer;

  public FtpServerMock(final int port, final String userName, final String password,
      final File homeDirectory) {
    fakeFtpServer = new FakeFtpServer();
    fakeFtpServer.setServerControlPort(port);
    fakeFtpServer.addUserAccount(new UserAccount(userName, password, homeDirectory
        .getAbsolutePath()));
  }

  public void addDirectoryAndAllFilesRecursively(final File directory) {
    FileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.add(new DirectoryEntry(directory.getAbsolutePath()));
    addDirectory(fileSystem, directory);
    fakeFtpServer.setFileSystem(fileSystem);
  }

  private void addDirectory(final FileSystem fileSystem, final File directory) {
    for (File file : directory.listFiles()) {
      if (file.isFile()) {
        addFile(fileSystem, file);
      } else if (file.isDirectory()) {
        addDirectory(fileSystem, file);
      }
    }
  }

  private void addFile(final FileSystem fileSystem, final File file) {
    try {
      fileSystem.add(new FileEntry(file.getAbsolutePath(), FileUtils.readFileToString(file)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void startServer() {
    fakeFtpServer.start();
  }

  public void stopServer() {
    fakeFtpServer.stop();
  }

}
