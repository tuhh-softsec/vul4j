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
package com.google.jenkins.flakyTestHandler.junit;

import junit.framework.TestCase;

import hudson.tasks.test.TestResult;

/**
 * Test class copied from hudson.tasks.junit.ClassResultTest
 *
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/test/java/hudson/tasks/junit/
 * ClassResultTest.java
 */
public class FlakyClassResultTest extends TestCase {

  public void testFindCorrespondingResult() {
    FlakyClassResult flakyClassResult = new FlakyClassResult(null, "com.example.ExampleTest");

    FlakyCaseResult flakyCaseResult = new FlakyCaseResult(null, "testCase", null);

    flakyClassResult.add(flakyCaseResult);

    TestResult result = flakyClassResult
        .findCorrespondingResult("extraprefix.com.example.ExampleTest.testCase");
    assertEquals(flakyCaseResult, result);
  }

  public void testFindCorrespondingResultWhereFlakyClassResultNameIsNotSubstring() {
    FlakyClassResult FlakyClassResult = new FlakyClassResult(null, "aaaa");

    FlakyCaseResult FlakyCaseResult = new FlakyCaseResult(null, "tc_bbbb", null);

    FlakyClassResult.add(FlakyCaseResult);

    TestResult result = FlakyClassResult.findCorrespondingResult("tc_bbbb");
    assertEquals(FlakyCaseResult, result);
  }

  public void testFindCorrespondingResultWhereFlakyClassResultNameIsLastInFlakyCaseResultName() {
    FlakyClassResult FlakyClassResult = new FlakyClassResult(null, "aaaa");

    FlakyCaseResult FlakyCaseResult = new FlakyCaseResult(null, "tc_aaaa", null);

    FlakyClassResult.add(FlakyCaseResult);

    TestResult result = FlakyClassResult.findCorrespondingResult("tc_aaaa");
    assertEquals(FlakyCaseResult, result);
  }

}
