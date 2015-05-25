/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.net.ftp;

// Builder Pattern
public final class FTPConnectionProperties {

    private final String hostname;
    private final int port;
    private final String directory;
    private final String username;
    private final String password;
    private final boolean passiveLocalDataConnectionMode;

    public static class Builder {

        // Required parameters
        private final String hostname;

        // Optional parameters - initialized to default values
        private int port = -1;
        private String directory = "/";
        private String username = "anonymous";
        private String password = "anonymous";
        private boolean passiveLocalDataConnectionMode;

        public Builder(final String hostname) {
            this.hostname = hostname;
        }

        public Builder port(int val) {
            port = val;
            return this;
        }

        public Builder directory(final String val) {
            directory = val;
            return this;
        }

        public Builder username(final String val) {
            username = val;
            return this;
        }

        public Builder password(final String val) {
            password = val;
            return this;
        }

        public Builder passiveLocalDataConnectionMode(final boolean val) {
            passiveLocalDataConnectionMode = val;
            return this;
        }

        public FTPConnectionProperties build() {
            return new FTPConnectionProperties(this);
        }
    }

    private FTPConnectionProperties(final Builder builder) {
        hostname = builder.hostname;
        port = builder.port;
        directory = builder.directory;
        username = builder.username;
        password = builder.password;
        passiveLocalDataConnectionMode = builder.passiveLocalDataConnectionMode;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getDirectory() {
        return directory;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public boolean isPassiveLocalDataConnectionMode() {
        return passiveLocalDataConnectionMode;
    }

}
