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

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import org.jvnet.hudson.test.Bug;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import hudson.XmlFile;
import hudson.util.HeapSpaceStringConverter;
import hudson.util.XStream2;

/**
 * Tests the JUnit result XML file parsing in {@link hudson.tasks.junit.TestResult}.
 *
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/test/java/hudson/tasks/junit/
 * TestResultTest.java
 *
 */
public class FlakyTestResultTest extends TestCase {

  private File getDataFile(String name) throws URISyntaxException {
    return new File(FlakyTestResultTest.class.getResource(name).toURI());
  }

  /**
   * Verifies that all suites of an Eclipse Plug-in Test Suite are collected. These suites don't
   * differ by name (and timestamp), the y differ by 'id'.
   */
  public void testIpsTests() throws Exception {
    FlakyTestResult testResult = new FlakyTestResult();
    testResult.parse(getDataFile("eclipse-plugin-test-report.xml"));

    Collection<FlakySuiteResult> suites = testResult.getSuites();
    assertEquals("Wrong number of test suites", 16, suites.size());
    int testCaseCount = 0;
    for (FlakySuiteResult suite : suites) {
      testCaseCount += suite.getCases().size();
    }
    assertEquals("Wrong number of test cases", 3366, testCaseCount);
  }

  /**
   * This test verifies compatibility of JUnit test results persisted to XML prior to the test code
   * refactoring.
   */
  public void testXmlCompatibility() throws Exception {
    XmlFile xmlFile = new XmlFile(XSTREAM, getDataFile("junitResult.xml"));
    FlakyTestResult result = (FlakyTestResult) xmlFile.read();

    // Regenerate the transient data
    result.tally();
    assertEquals(9, result.getTotalCount());
    assertEquals(1, result.getSkipCount());
    assertEquals(1, result.getFailCount());

    // XStream seems to produce some weird rounding errors...
    assertEquals(0.576, result.getDuration(), 0.0001);

    Collection<FlakySuiteResult> suites = result.getSuites();
    assertEquals(6, suites.size());

    List<FlakyCaseResult> failedTests = result.getFailedTests();
    assertEquals(1, failedTests.size());

    FlakySuiteResult failedSuite = result.getSuite("broken");
    assertNotNull(failedSuite);
    FlakyCaseResult failedCase = failedSuite.getCase("becomeUglier");
    assertNotNull(failedCase);
    assertFalse(failedCase.isSkipped());
    assertFalse(failedCase.isPassed());
  }

  /**
   * When test methods are parametrized, they can occur multiple times in the testresults XMLs. Test
   * that these are counted correctly.
   */
  @Bug(13214)
  public void testDuplicateTestMethods() throws IOException, URISyntaxException {
    FlakyTestResult testResult = new FlakyTestResult();
    testResult.parse(getDataFile("JENKINS-13214/27449.xml"));
    testResult.parse(getDataFile("JENKINS-13214/27540.xml"));
    testResult.parse(getDataFile("JENKINS-13214/29734.xml"));
    testResult.tally();

    assertEquals("Wrong number of test suites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 3, testResult.getTotalCount());
  }

  @Bug(12457)
  public void testTestSuiteDistributedOverMultipleFilesIsCountedAsOne()
      throws IOException, URISyntaxException {
    FlakyTestResult testResult = new FlakyTestResult();
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_a1.xml"));
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_a2.xml"));
    testResult.tally();

    assertEquals("Wrong number of testsuites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 2, testResult.getTotalCount());

    // check duration: 157.980 (TestSuite_a1.xml) and 15.000 (TestSuite_a2.xml) = 172.98
    assertEquals("Wrong duration for test result", 172.98, testResult.getDuration(), 0.1);
  }

  /**
   * A common problem is that people parse TEST-*.xml as well as TESTS-TestSuite.xml. See
   * http://jenkins.361315.n4.nabble.com/Problem-with-duplicate-build-execution-td371616.html for
   * discussion.
   */
  public void testDuplicatedTestSuiteIsNotCounted() throws IOException, URISyntaxException {
    FlakyTestResult testResult = new FlakyTestResult();
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_b.xml"));
    testResult.parse(getDataFile("JENKINS-12457/TestSuite_b_duplicate.xml"));
    testResult.tally();

    assertEquals("Wrong number of testsuites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 1, testResult.getTotalCount());
    assertEquals("Wrong duration for test result", 1.0, testResult.getDuration(), 0.01);
  }

  /**
   * Test parsing of test reports with flaky tests information. More testing of contents of flaky
   * tests is in FlakySuiteResultTest
   */
  public void testFlakyTestReport() throws IOException, URISyntaxException {
    FlakyTestResult testResult = new FlakyTestResult();
    testResult.parse(getDataFile("flaky-reports/flaky-report-1.xml"));
    testResult.tally();

    assertEquals("Wrong number of testsuites", 1, testResult.getSuites().size());
    assertEquals("Wrong number of test cases", 5, testResult.getTotalCount());
    assertEquals("Wrong number of passing test cases", 3, testResult.getPassCount());
    assertEquals("Wrong number of failing test cases", 1, testResult.getFailCount());
    assertEquals("Wrong number of flaky test cases", 1, testResult.getFlakyTests().size());
  }

  private static final XStream XSTREAM = new XStream2();

  static {
    XSTREAM.alias("result", FlakyTestResult.class);
    XSTREAM.alias("suite", FlakySuiteResult.class);
    XSTREAM.alias("case", FlakyCaseResult.class);
    XSTREAM.registerConverter(new HeapSpaceStringConverter(), 100);
  }
}