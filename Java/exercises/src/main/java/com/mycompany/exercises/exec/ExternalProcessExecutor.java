/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private static final int SUCCESSFUL_EXIT_VALUE = 1;
    private List<String> executeOutput;

    public ExternalProcessExecutor() {

    }

    public boolean executeScript(final ScriptParameters parameters) {
        boolean scriptExecuted = false;

        try {
            System.out.println("Preparing script job...");
            ScriptResultHandler scriptResult = execute(parameters);
            scriptResult.waitFor();
            System.out.println("The script job has finished.");
            scriptExecuted = true;
        } catch (IOException | InterruptedException ex) {
            logException(ex, parameters.getScriptFileLocation());
        }

        return scriptExecuted;
    }

    private ScriptResultHandler execute(final ScriptParameters parameters)
            throws ExecuteException, IOException {
        ExecuteWatchdog watchdog = null;
        ScriptResultHandler resultHandler = null;

        CommandLine commandLine = buildCommandLine(
                parameters.getScriptFileLocation(), parameters.getCommandLineArguments());

        CollectingLogOutputStream outputStream = new CollectingLogOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        Executor executor = new DefaultExecutor();
        executor.setExitValue(SUCCESSFUL_EXIT_VALUE);
        executor.setStreamHandler(streamHandler);

        if (parameters.getScriptJobTimeout() > 0) {
            watchdog = new ExecuteWatchdog(parameters.getScriptJobTimeout());
            executor.setWatchdog(watchdog);
        }

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

    private CommandLine buildCommandLine(final File file, final String[] commandLineArguments) {
        CommandLine cmd = new CommandLine(file);
        for (String argument : commandLineArguments) {
            cmd.addArgument(argument);
        }
        return cmd;
    }

    private void setExecuteOutput(final List<String> lines) {
        this.executeOutput = lines;
    }

    public List<String> getExecuteOutput() {
        return this.executeOutput;
    }

    private void logException(final Exception ex, final File file) {
        System.err.println("Executing of the following script failed: " + file.getAbsolutePath());
        System.err.println("Message: " + ex.getMessage());
    }

    private static class ScriptResultHandler extends DefaultExecuteResultHandler {

        private ExecuteWatchdog watchdog;

        public ScriptResultHandler(final ExecuteWatchdog watchdog) {
            this.watchdog = watchdog;
        }

        public ScriptResultHandler(final int exitValue) {
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
