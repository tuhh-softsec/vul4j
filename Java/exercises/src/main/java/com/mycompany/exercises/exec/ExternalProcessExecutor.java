/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import com.mycompany.exercises.io.FileAccessibilityUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

public final class ExternalProcessExecutor {

  private List<String> executeOutput;

  public ExternalProcessExecutor() {

  }

  public boolean executeScript(final ScriptParameters parameters) {
    boolean scriptExecuted = false;

    if (!FileAccessibilityUtils.ensureFileIsAccessible(parameters.getScriptFileLocation())) {
      return false;
    }

    try {
      System.out.println("Preparing script job: " + parameters.toString());
      ScriptResultHandler scriptResult = execute(parameters);
      scriptResult.waitFor();
      scriptExecuted = true;
      System.out.println("The script job has finished.");
    } catch (IOException | InterruptedException ex) {
      logException(ex, parameters);
    }

    return scriptExecuted;
  }

  private ScriptResultHandler execute(final ScriptParameters parameters) throws ExecuteException,
      IOException {
    setExecuteOutput(Collections.emptyList());

    ExecuteWatchdog watchdog = new ExecuteWatchdog(parameters.getScriptJobTimeout());
    ScriptResultHandler resultHandler = null;
    CollectingLogOutputStream outputStream = new CollectingLogOutputStream();

    CommandLine commandLine =
        buildCommandLine(parameters.getScriptFileLocation(), parameters.getCommandLineArguments());

    Executor executor = getAndSetExecutor(parameters, outputStream, watchdog);

    if (parameters.getExecuteInBackground()) {
      System.out.println("Executing non-blocking script job...");
      resultHandler = new ScriptResultHandler(watchdog);
      executor.execute(commandLine, resultHandler);
    } else {
      System.out.println("Executing blocking script job...");
      int exitValue = executor.execute(commandLine);
      resultHandler = new ScriptResultHandler(exitValue);
    }

    setExecuteOutput(outputStream.getLines());

    return resultHandler;
  }

  private CommandLine buildCommandLine(final Path path, final String[] commandLineArguments) {
    CommandLine cmd = new CommandLine(path.toString());
    for (String argument : commandLineArguments) {
      cmd.addArgument(argument);
    }
    return cmd;
  }

  private Executor getAndSetExecutor(final ScriptParameters parameters,
      final CollectingLogOutputStream outputStream, final ExecuteWatchdog watchdog) {
    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    Executor executor = new DefaultExecutor();
    executor.setExitValue(parameters.getSuccessfulExitValue());
    executor.setStreamHandler(streamHandler);

    if (parameters.getWorkingDirectory() != null) {
      executor.setWorkingDirectory(parameters.getWorkingDirectory().toFile());
    }

    if (parameters.getScriptJobTimeout() > 0) {
      executor.setWatchdog(watchdog);
    }
    return executor;
  }

  private void setExecuteOutput(final List<String> lines) {
    this.executeOutput = lines;
  }

  public List<String> getExecuteOutput() {
    return this.executeOutput;
  }

  private void logException(final Exception ex, final ScriptParameters parameters) {
    System.err.println("Executing of the following script failed:" + parameters.toString());
    System.err.println("Message: " + ex.getMessage());
  }

  private static class ScriptResultHandler extends DefaultExecuteResultHandler {

    private ExecuteWatchdog watchdog;

    ScriptResultHandler(final ExecuteWatchdog watchdog) {
      this.watchdog = watchdog;
    }

    ScriptResultHandler(final int exitValue) {
      super.onProcessComplete(exitValue);
    }

    @Override
    public void onProcessComplete(final int exitValue) {
      super.onProcessComplete(exitValue);
      System.out.println("The sript was successfully executed.");
    }

    @Override
    public void onProcessFailed(final ExecuteException e) {
      super.onProcessFailed(e);
      if (watchdog != null && watchdog.killedProcess()) {
        System.err.println("The script process timed out.");
      } else {
        System.err.println("Failed to execute the script: " + e.getMessage() + ".");
      }
    }

  }

  private static class CollectingLogOutputStream extends LogOutputStream {

    private final List<String> lines = new ArrayList<>();

    @Override
    protected void processLine(final String line, int level) {
      lines.add(line);
    }

    public List<String> getLines() {
      return lines;
    }

  }

}
