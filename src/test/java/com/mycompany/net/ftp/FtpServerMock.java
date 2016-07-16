/**
 * Copyright (c) 2016 Company.
 * All rights reserved.
 */
package com.mycompany.net.ftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FtpServerMock {

  private final FakeFtpServer fakeFtpServer;

  public FtpServerMock(final int port, final String userName, final String password,
          final Path homeDirectory) {
    fakeFtpServer = new FakeFtpServer();
    fakeFtpServer.setServerControlPort(port);
    fakeFtpServer.addUserAccount(new UserAccount(userName, password, homeDirectory
            .toAbsolutePath().toString()));
  }

  public void addDirectoryAndAllFilesRecursively(final Path directory) throws IOException {
    FileSystem fileSystem = new UnixFakeFileSystem();
    fileSystem.add(new DirectoryEntry(directory.toAbsolutePath().toString()));
    addDirectory(fileSystem, directory);
    fakeFtpServer.setFileSystem(fileSystem);
  }

  private void addDirectory(final FileSystem fileSystem, final Path directory) throws IOException {
    Files.walk(directory)
            .filter(Files::isRegularFile)
            .forEach(file -> addFile(fileSystem, file));
  }

  private void addFile(final FileSystem fileSystem, final Path file) {
    try {
      fileSystem.add(new FileEntry(file.toAbsolutePath().toString(), readFileToStringIncludingNewlineCharacter(file)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String readFileToStringIncludingNewlineCharacter(final Path path) throws IOException {
    return FileUtils.readFileToString(path.toFile());
  }

  public void startServer() {
    fakeFtpServer.start();
  }

  public void stopServer() {
    fakeFtpServer.stop();
  }

}
