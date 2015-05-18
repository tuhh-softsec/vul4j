/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.threads;

/**
 * Helper class to execute other classes.
 */
public final class ThreadsLauncher {
    
    private ThreadsLauncher() { }

    public static void main(final String[] args) {
        executeInSync();
    }

    private static void executeInSync() {
        StringBuffer sb = new StringBuffer("A");
        
        new InSync(sb).start();
        new InSync(sb).start();
        new InSync(sb).start();
    }
}
