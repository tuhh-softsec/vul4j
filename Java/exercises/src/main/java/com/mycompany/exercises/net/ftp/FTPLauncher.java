/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

public final class FTPLauncher {
    
    private FTPLauncher() {
    }
    
    public static void main(final String[] args) {
        FTPConnectionProperties ftpProperties = new FTPConnectionProperties.Builder("ftp.uk.debian.org").
            directory("debian/dists/Debian8.0/main/installer-amd64/20150422/images/cdrom").build();
        MyFTPClient ftpClient = new MyFTPClient();
        
        ftpClient.obtainListOfFileInformationAnonymous(ftpProperties);        
    }
}
