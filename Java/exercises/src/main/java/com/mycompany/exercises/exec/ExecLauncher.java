/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.exec;

import java.io.File;

public final class ExecLauncher {

    private ExecLauncher() {

    }

    public static void main(final String[] args) {
        ExternalProcessExecutor executor = new ExternalProcessExecutor();
        executor.executeScript(new ScriptParameters.Builder(
                new File("/home/user/scripts/scriptAlfa"))
                .scriptJobTimeout(60000).executeInBackground(false).commandLineArguments(args).build());
        System.out.println("Script execute output: ");
        executor.getExecuteOutput().stream().forEach((line) -> {
            System.out.println(line);
        });
    }
}
