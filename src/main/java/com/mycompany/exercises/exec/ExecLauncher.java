/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.nio.file.Paths;

public final class ExecLauncher {

  private ExecLauncher() {}

  public static void main(final String[] args) {
    ExternalProcessExecutor executor = new ExternalProcessExecutor();
    ScriptParameters parameters = new ScriptParameters.Builder(
            Paths.get("/home/user/scripts/scriptAlfa"), 1)
            .scriptJobTimeout(60000)
            .executeInBackground(false)
            .commandLineArguments(args)
            .build();
    executor.executeScript(parameters);
    System.out.println("Script execute output: ");
    executor.getExecuteOutput().stream().forEach((line) -> {
        System.out.println(line);
      });
  }
}
