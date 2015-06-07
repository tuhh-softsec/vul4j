/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ScriptParameters {

  private final Path scriptFileLocation;
  private final int successfulExitValue;
  private final String[] commandLineArguments;
  private final Path workingDirectory;
  private final long scriptJobTimeout;
  private final boolean executeInBackground;

  public static class Builder {

    // Required parameters
    private final Path scriptFileLocation;
    private final int successfulExitValue;

    // Optional parameters - initialized to default values
    private String[] commandLineArguments;
    private Path workingDirectory;
    private long scriptJobTimeout = 60000;
    private boolean executeInBackground = false; // SUPPRESS CHECKSTYLE ExplicitInitialization

    public Builder(final Path scriptFileLocation, int successfulExitValue) {
      this.scriptFileLocation = scriptFileLocation;
      this.successfulExitValue = successfulExitValue;
    }

    public Builder commandLineArguments(final String... val) {
      commandLineArguments = val;
      return this;
    }

    public Builder workingDirectory(final String val) {
      workingDirectory = Paths.get(val);
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
    successfulExitValue = builder.successfulExitValue;
    commandLineArguments = builder.commandLineArguments;
    workingDirectory = builder.workingDirectory;
    scriptJobTimeout = builder.scriptJobTimeout;
    executeInBackground = builder.executeInBackground;
  }

  public Path getScriptFileLocation() {
    return scriptFileLocation;
  }

  public int getSuccessfulExitValue() {
    return successfulExitValue;
  }

  public String[] getCommandLineArguments() {
    return commandLineArguments;
  }

  public Path getWorkingDirectory() {
    return workingDirectory;
  }

  public long getScriptJobTimeout() {
    return scriptJobTimeout;
  }

  public boolean getExecuteInBackground() {
    return executeInBackground;
  }

}
