/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.threads;

/**
 * Synchronizing a Block of Code.
 * From OCA/OCP Java SE7 Programmer 1 & 2 Study Guide, Chapter 13: Threads.
 */
public class InSync extends Thread {
    
    private static final int N = 100;
    private final StringBuffer letter;
    
    public InSync(final StringBuffer letter) {
        this.letter = letter;
    }
    
    @Override
    public void run() {
        synchronized (letter) {
            // output the StringBuffer 100 times
            for (int i = 0; i < N; i++) {
                System.out.println(letter);
            }
            
            // increment the letter in the StringBuffer
            char ch = letter.charAt(0);
            ch++;
            letter.setCharAt(0, ch);
        }
    }
    
}
