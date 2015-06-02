/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.io.File;

public final class ScriptParameters {

    private final File scriptFileLocation;
    private final String[] commandLineArguments;
    private final long scriptJobTimeout;
    private final boolean executeInBackground;

    public static class Builder {

        // Required parameters
        private final File scriptFileLocation;

        // Optional parameters - initialized to default values
        private String[] commandLineArguments;
        private long scriptJobTimeout = 60000;
        private boolean executeInBackground = false;  // SUPPRESS CHECKSTYLE ExplicitInitialization

        public Builder(final File scriptFileLocation) {
            this.scriptFileLocation = scriptFileLocation;
        }

        public Builder commandLineArguments(final String... val) {
            commandLineArguments = val;
            return this;
        }

        public Builder scriptJobTimeout(long val) {
            scriptJobTimeout = val;
            return this;
        }

        public Builder executeInBackground(boolean val) {
            executeInBackground = val;
            return this;
        }

        public ScriptParameters build() {
            return new ScriptParameters(this);
        }
    }

    private ScriptParameters(final Builder builder) {
        scriptFileLocation = builder.scriptFileLocation;
        commandLineArguments = builder.commandLineArguments;
        scriptJobTimeout = builder.scriptJobTimeout;
        executeInBackground = builder.executeInBackground;
    }

    public File getScriptFileLocation() {
        return scriptFileLocation;
    }

    public long getScriptJobTimeout() {
        return scriptJobTimeout;
    }

    public boolean getExecuteInBackground() {
        return executeInBackground;
    }

    public String[] getCommandLineArguments() {
        return commandLineArguments;
    }

}
