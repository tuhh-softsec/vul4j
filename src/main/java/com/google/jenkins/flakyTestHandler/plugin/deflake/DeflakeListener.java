/* Copyright 2014 Google Inc. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.jenkins.flakyTestHandler.plugin.deflake;

import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestResultAction;

/**
 * Listener to add deflake action when there are failing tests
 *
 * @author Qingzhou Luo
 */
@Extension
public class DeflakeListener extends RunListener<Run> {

  private static final Logger LOGGER = Logger.getLogger(DeflakeListener.class.getName());

  public DeflakeListener() {
    super(Run.class);
  }

  // Add deflake action to the build and aggregate test running stats from this build
  @Override
  public void onCompleted(Run run, TaskListener listener) {
    // TODO consider the possibility that there is >1 such action
    TestResultAction testResultAction = run.getAction(TestResultAction.class);

    HistoryAggregatedFlakyTestResultAction historyAction = run.getParent()
        .getAction(HistoryAggregatedFlakyTestResultAction.class);

    // Aggregate test running results
    if (historyAction != null) {
      historyAction.aggregateOneBuild(run);
    }

    if (testResultAction != null && testResultAction.getFailCount() > 0) {
      // Only add deflake action if there are test failures
      run.addAction(
          new DeflakeAction(getFailingTestClassMethodMap(testResultAction.getFailedTests())));
    }
  }

  // Set the name of a deflake build
  @Override
  public void onStarted(Run build, TaskListener listener) {
    List<Cause> causesList = build.getCauses();
    try {
      for (Cause cause : causesList) {
        if (cause instanceof DeflakeCause) {
          build.setDisplayName(build.getDisplayName() + ": " + cause.getShortDescription());
          return;
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to set deflake build name: " + e.getMessage());
    }
  }

  /**
   * Organize failing test cases by their class names
   *
   * @param caseResultList all the failing test cases list
   * @return the map with class name and the set of test methods in each class
   */
  static Map<String, Set<String>> getFailingTestClassMethodMap(List<CaseResult> caseResultList) {
    Map<String, Set<String>> classMethodMap = new HashMap<String, Set<String>>();
    if (caseResultList != null) {
      for (CaseResult caseResult : caseResultList) {
        if (!classMethodMap.containsKey(caseResult.getClassName())) {
          classMethodMap.put(caseResult.getClassName(), new HashSet<String>());
        }
        classMethodMap.get(caseResult.getClassName()).add(caseResult.getName());
      }
    }
    return classMethodMap;
  }
}
