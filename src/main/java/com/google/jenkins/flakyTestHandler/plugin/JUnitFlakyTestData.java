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
package com.google.jenkins.flakyTestHandler.plugin;

import com.google.jenkins.flakyTestHandler.junit.ActionableFlakyTestObject;
import com.google.jenkins.flakyTestHandler.junit.FlakyCaseResult;
import com.google.jenkins.flakyTestHandler.junit.FlakyClassResult;
import com.google.jenkins.flakyTestHandler.junit.FlakyPackageResult;
import com.google.jenkins.flakyTestHandler.junit.FlakyTestResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestObject;
import hudson.tasks.junit.TestResultAction;

/**
 * Data object to hold rerun information for all the test cases
 *
 * @author Qingzhou Luo
 */
public class JUnitFlakyTestData extends TestResultAction.Data {

  /**
   * Map between test case id and their TestResult
   */
  Map<String, ActionableFlakyTestObject> testCaseFlakyInfoMap;

  public JUnitFlakyTestData(FlakyTestResult flakyTestResult) {
    testCaseFlakyInfoMap = new HashMap<String, ActionableFlakyTestObject>();
    putClassAndCaseResults(flakyTestResult);
  }

  /**
   * Get action to display rerun information for each test case
   *
   * @param testObject the test object to get information for
   * @return action to display rerun information for the given test
   */
  @Override
  public List<? extends TestAction> getTestAction(TestObject testObject) {

    ActionableFlakyTestObject flakyTestObject = testCaseFlakyInfoMap.get(testObject.getId());
    if (flakyTestObject != null) {
      return Collections.singletonList(flakyTestObject.getTestAction());
    }
    return Collections.emptyList();
  }

  /**
   * Extract all the classes and test cases from test result and put them into testCaseFlakyInfoMap
   *
   * @param testResult result of one build for all the tests
   *
   */
  private void putClassAndCaseResults(FlakyTestResult testResult) {

    Collection<FlakyPackageResult> packageResults = testResult.getChildren();
    for (FlakyPackageResult pkgResult : packageResults) {
      Collection<FlakyClassResult> classResults = pkgResult.getChildren();
      for (FlakyClassResult classResult : classResults) {
        testCaseFlakyInfoMap.put(classResult.getId(), classResult);
        for (FlakyCaseResult childCaseResult : classResult.getChildren()) {
          testCaseFlakyInfoMap.put(childCaseResult.getId(), childCaseResult);
        }
      }
    }
  }
}
