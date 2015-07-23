/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

import static com.mycompany.exercises.net.ftp.MyFTPClientLogger.*;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTPClient {

  public void obtainListOfFileInformationAnonymous(final FTPConnectionProperties ftpProperties) {
    obtainListOfFileInformation(ftpProperties.getHostname(), ftpProperties.getPort(),
        ftpProperties.getDirectory(), ftpProperties.getUsername(), ftpProperties.getPassword(),
        ftpProperties.isPassiveLocalDataConnectionMode());
  }

  /**
   * Unpaged (whole list) access, using a parser accessible by auto-detect.
   *
   * @param hostname The name of the remote host
   * @param port The local port to use
   * @param directory The file or directory to list
   * @param username The username to login under
   * @param password The password to use
   * @param passiveLocalDataConnectionMode Current data connection mode, set the value to true only
   *        for data transfers between the client and server.
   */
  private void obtainListOfFileInformation(final String hostname, int port, final String directory,
      final String username, final String password, boolean passiveLocalDataConnectionMode) {
    FTPClient ftp = new FTPClient();
    try {
      if (startNewFTPSessioin(ftp, hostname, port, username, password,
          passiveLocalDataConnectionMode)) {
        listFiles(ftp, directory);
      }
    } catch (IOException ex) {
      if (portIsNotDefined(port)) {
        logConnectionError(hostname, ex);
      } else {
        logConnectionError(hostname, port, ex);
      }
    } finally {
      closeConnectionIfConnected(ftp);
    }
  }

  private boolean startNewFTPSessioin(final FTPClient ftp, final String hostname, int port,
      final String username, final String password, boolean passiveLocalDataConnectionMode)
      throws IOException {
    if (portIsNotDefined(port)) {
      return connect(ftp, hostname) && login(ftp, username, password)
          && setParameters(ftp, passiveLocalDataConnectionMode);
    } else {
      return connect(ftp, hostname, port) && login(ftp, username, password)
          && setParameters(ftp, passiveLocalDataConnectionMode);
    }
  }

  private void listFiles(final FTPClient ftp, final String directory) throws IOException {
    FTPFile[] ftpFiles = ftp.listFiles(directory);
    Arrays.asList(ftpFiles)
            .stream()
            .forEach(file -> System.out.println(String.valueOf(file)));
  }

  private boolean connect(final FTPClient ftp, final String hostname) throws IOException {
    boolean isConnected = false;
    ftp.connect(hostname);

    int replyCode = ftp.getReplyCode();
    String reply = ftp.getReplyString();
    if (!FTPReply.isPositiveCompletion(replyCode)) {
      logConnectionRefused(hostname, reply);
    } else {
      logConnectionSuccessful(hostname, reply);
      isConnected = true;
    }
    return isConnected;
  }

  private boolean connect(final FTPClient ftp, final String hostname, int port) throws IOException {
    boolean isConnected = false;
    ftp.connect(hostname, port);

    int replyCode = ftp.getReplyCode();
    String reply = ftp.getReplyString();
    if (!FTPReply.isPositiveCompletion(replyCode)) {
      logConnectionRefused(hostname, port, reply);
    } else {
      logConnectionSuccessful(hostname, port, reply);
      isConnected = true;
    }
    return isConnected;
  }

  private boolean login(final FTPClient ftp, final String username, final String password)
      throws IOException {
    boolean isLoged = ftp.login(username, password);
    String reply = ftp.getReplyString();
    if (!isLoged) {
      logLoginRefused(username, reply);
    } else {
      logLoginSuccessful(username, reply);
    }
    return isLoged;
  }

  private boolean setParameters(final FTPClient ftp, boolean passiveLocalDataConnectionMode) {
    if (passiveLocalDataConnectionMode) {
      ftp.enterLocalPassiveMode();
      logEnterPassiveLocalDataConnectionModeSuccessful();
    }
    return true;
  }

  private void closeConnectionIfConnected(final FTPClient ftp) {
    if (ftp != null && ftp.isConnected()) {
      try {
        ftp.logout();
        ftp.disconnect();
      } catch (IOException ex) {
        logCloseConnectionFailed(ex);
      }
    }
  }

  private boolean portIsNotDefined(int port) {
    return port == -1;
  }

}
