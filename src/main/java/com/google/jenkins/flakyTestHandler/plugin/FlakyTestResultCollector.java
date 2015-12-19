package com.google.jenkins.flakyTestHandler.plugin;

import com.google.jenkins.flakyTestHandler.junit.FlakyTestResult;
import hudson.tasks.junit.TestResult;
import jenkins.security.MasterToSlaveCallable;

public class FlakyTestResultCollector extends MasterToSlaveCallable<FlakyTestResult, RuntimeException> {
    private final TestResult testResult;

    public FlakyTestResultCollector(TestResult testResult) {
        this.testResult = testResult;
    }

    public FlakyTestResult call() throws RuntimeException {
        return new FlakyTestResult(testResult);
    }

}
