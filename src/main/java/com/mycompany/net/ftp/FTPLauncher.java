/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.net.ftp;

import java.nio.file.Paths;

public final class FTPLauncher {

  private FTPLauncher() {}

  public static void main(final String[] args) {
    FTPConnectionProperties ftpProperties =
        new FTPConnectionProperties.Builder("ftp.gnu.org").directory(
            Paths.get("/third-party")).build();
    MyFTPClient ftpClient = new MyFTPClient();

    ftpClient.obtainListOfFileInformationAnonymous(ftpProperties).stream()
            .forEach(file -> System.out.println(String.valueOf(file)));
  }
}
