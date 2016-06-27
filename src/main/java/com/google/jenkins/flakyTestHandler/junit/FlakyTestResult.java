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

import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStats;
import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStatsWithRevision;

import org.apache.tools.ant.DirectoryScanner;
import org.dom4j.DocumentException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hudson.AbortException;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.tasks.junit.SuiteResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.tasks.test.MetaTabulatedResult;
import hudson.tasks.test.TestObject;

/**
 * Root of all the test results for one build, including flaky runs information.
 * Majority of code copied from hudson.tasks.junit.TestResult
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/
 * TestResult.java
 *
 * @author Qingzhou Luo
 */
public final class FlakyTestResult extends MetaTabulatedResult {

  /**
   * List of all {@link FlakySuiteResult}s in this test.
   * This is the core data structure to be persisted in the disk.
   */
  private final List<FlakySuiteResult> suites = new ArrayList<FlakySuiteResult>();

  /**
   * {@link #suites} keyed by their names for faster lookup.
   */
  private transient Map<String,FlakySuiteResult> suitesByName;

  /**
   * Results tabulated by package.
   */
  private transient Map<String,FlakyPackageResult> byPackages;

  // set during the freeze phase
  private transient AbstractTestResultAction parentAction;

  // set during the freeze phase
  private transient AbstractBuild owner;

  private transient TestObject parent;

  // instance of the original TestResult object to delegate method calls
  private TestResult testResultInstance;

  /**
   * Number of all tests.
   */
  private transient int totalTests;

  /**
   * Number of skipped tests.
   */
  private transient int skippedTests;

  private float duration;

  /**
   * List of failed/error tests.
   */
  private transient List<FlakyCaseResult> failedTests;

  /**
   * List of flaky tests.
   */
  private transient List<FlakyCaseResult> flakyTests;

  /**
   * List of all passing tests without a flake.
   */
  private transient List<FlakyCaseResult> passedTests;

  private final boolean keepLongStdio;

  /**
   * Construct {@link #FlakyTestResult} from {@link #TestResult}
   *
   * @param testResult
   */
  public FlakyTestResult(TestResult testResult) {
    for (SuiteResult suiteResult : testResult.getSuites()) {
      try {
        suites.addAll(FlakySuiteResult.parse(new File(suiteResult.getFile()), true));
        testResultInstance = testResult;
      } catch (DocumentException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }
    keepLongStdio = true;
  }

  public FlakyTestResult() {
    this.keepLongStdio = false;
  }

  public TestObject getParent() {
    return parent;
  }

  @Override
  public void setParent(TestObject parent) {
    this.parent = parent;
  }

  /**
   * Collect reports from the given {@link DirectoryScanner}, while
   * filtering out all files that were created before the given time.
   */
  public void parse(long buildTime, DirectoryScanner results) throws IOException {
    String[] includedFiles = results.getIncludedFiles();
    File baseDir = results.getBasedir();
    parse(buildTime,baseDir,includedFiles);
  }

  /**
   * Collect reports from the given report files, while
   * filtering out all files that were created before the given time.
   *
   * @since 1.426
   */
  public void parse(long buildTime, File baseDir, String[] reportFiles) throws IOException {

    boolean parsed=false;

    for (String value : reportFiles) {
      File reportFile = new File(baseDir, value);
      // only count files that were actually updated during this build
      if ( (buildTime-3000/*error margin*/ <= reportFile.lastModified())) {
        parsePossiblyEmpty(reportFile);
        parsed = true;
      }
    }

    if(!parsed) {
      long localTime = System.currentTimeMillis();
      if(localTime < buildTime-1000) /*margin*/
        // build time is in the the future. clock on this slave must be running behind
        throw new AbortException(
            "Clock on this slave is out of sync with the master, and therefore \n" +
                "I can't figure out what test results are new and what are old.\n" +
                "Please keep the slave clock in sync with the master.");

      File f = new File(baseDir,reportFiles[0]);
      throw new AbortException(
          String.format(
              "Test reports were found but none of them are new. Did tests run? %n"+
                  "For example, %s is %s old%n", f,
              Util.getTimeSpanString(buildTime-f.lastModified())));
    }
  }

  /**
   * Collect reports from the given report files
   *
   * @since 1.500
   */
  public void parse(long buildTime, Iterable<File> reportFiles) throws IOException {
    boolean parsed=false;

    for (File reportFile : reportFiles) {
      // only count files that were actually updated during this build
      if ( (buildTime-3000/*error margin*/ <= reportFile.lastModified())) {
        parsePossiblyEmpty(reportFile);
        parsed = true;
      }
    }

    if(!parsed) {
      long localTime = System.currentTimeMillis();
      if(localTime < buildTime-1000) /*margin*/
        // build time is in the the future. clock on this slave must be running behind
        throw new AbortException(
            "Clock on this slave is out of sync with the master, and therefore \n" +
                "I can't figure out what test results are new and what are old.\n" +
                "Please keep the slave clock in sync with the master.");

      File f = reportFiles.iterator().next();
      throw new AbortException(
          String.format(
              "Test reports were found but none of them are new. Did tests run? %n"+
                  "For example, %s is %s old%n", f,
              Util.getTimeSpanString(buildTime-f.lastModified())));
    }

  }

  private void parsePossiblyEmpty(File reportFile) throws IOException {
    if(reportFile.length()==0) {
      // this is a typical problem when JVM quits abnormally, like OutOfMemoryError during a test.
      FlakySuiteResult sr = new FlakySuiteResult(reportFile.getName(), "", "");
      sr.addCase(new FlakyCaseResult(sr,"<init>","Test report file "+reportFile.getAbsolutePath()+" was length 0"));
      add(sr);
    } else {
      parse(reportFile);
    }
  }

  private void add(FlakySuiteResult sr) {
    for (FlakySuiteResult s : suites) {
      // JENKINS-12457: If a testsuite is distributed over multiple files, merge it into a single SuiteResult:
      if(s.getName().equals(sr.getName())  && nullSafeEq(s.getId(),sr.getId())) {

        // However, a common problem is that people parse TEST-*.xml as well as TESTS-TestSuite.xml.
        // In that case consider the result file as a duplicate and discard it.
        // see http://jenkins.361315.n4.nabble.com/Problem-with-duplicate-build-execution-td371616.html for discussion.
        if(strictEq(s.getTimestamp(),sr.getTimestamp())) {
          return;
        }

        for (FlakyCaseResult cr: sr.getCases()) {
          s.addCase(cr);
          cr.replaceParent(s);
        }
        duration += sr.getDuration();
        return;
      }
    }
    suites.add(sr);
    duration += sr.getDuration();
  }

  private boolean strictEq(Object lhs, Object rhs) {
    return lhs != null && rhs != null && lhs.equals(rhs);
  }

  private boolean nullSafeEq(Object lhs, Object rhs) {
    if (lhs == null) {
      return rhs == null;
    }
    return lhs.equals(rhs);
  }

  /**
   * Parses an additional report file.
   */
  public void parse(File reportFile) throws IOException {
    try {
      for (FlakySuiteResult suiteResult : FlakySuiteResult.parse(reportFile, keepLongStdio))
        add(suiteResult);
    } catch (InterruptedException e) {
      throw new IOException("Failed to read "+reportFile,e);
    } catch (RuntimeException e) {
      throw new IOException("Failed to read "+reportFile,e);
    } catch (DocumentException e) {
      if (!reportFile.getPath().endsWith(".xml")) {
        throw new IOException("Failed to read "+reportFile+"\n"+
            "Is this really a JUnit report file? Your configuration must be matching too many files",e);
      } else {
        FlakySuiteResult sr = new FlakySuiteResult(reportFile.getName(), "", "");
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String error = "Failed to read test report file "+reportFile.getAbsolutePath()+"\n"+writer.toString();
        sr.addCase(new FlakyCaseResult(sr,"<init>",error));
        add(sr);
      }
    }
  }

  public String getDisplayName() {
    return testResultInstance.getDisplayName();
  }

  @Override
  public AbstractBuild<?,?> getOwner() {
    if (parentAction != null) {
      return parentAction.owner;
    } else {
      return owner;
    }
  }

  @Override
  public hudson.tasks.test.TestResult findCorrespondingResult(String id) {
    return testResultInstance.findCorrespondingResult(id);
  }

  @Override
  public String getTitle() {
    return testResultInstance.getTitle();
  }

  @Override
  public String getChildTitle() {
    return testResultInstance.getChildTitle();
  }

  @Exported(visibility=999)
  @Override
  public float getDuration() {
    return duration;
  }

  @Exported(visibility=999)
  @Override
  public int getPassCount() {
    if(passedTests==null)
      return 0;
    else
      return passedTests.size();
  }

  @Exported(visibility=999)
  @Override
  public int getFailCount() {
    if(failedTests==null)
      return 0;
    else
      return failedTests.size();
  }

  @Override
  public int getTotalCount() {
    return totalTests;
  }

  @Exported(visibility=999)
  @Override
  public int getSkipCount() {
    return skippedTests;
  }

  /**
   * Returns <tt>true</tt> if this doesn't have any any test results.
   * @since 1.511
   */
  @Exported(visibility=999)
  public boolean isEmpty() {
    return getTotalCount() == 0;
  }

  @Override
  public List<FlakyCaseResult> getFailedTests() {
    return failedTests;
  }

  public List<FlakyCaseResult> getFlakyTests() {
    return flakyTests;
  }

  public List<FlakyCaseResult> getAllTests() {
    List<FlakyCaseResult> allTests = new ArrayList<FlakyCaseResult>();
    allTests.addAll(failedTests);
    allTests.addAll(flakyTests);
    allTests.addAll(passedTests);
    return allTests;
  }

  /**
   * Gets the "children" of this test result that passed
   *
   * @return the children of this test result, if any, or an empty collection
   */
  @Override
  public Collection<? extends hudson.tasks.test.TestResult> getPassedTests() {
    return passedTests;
  }

  @Override
  public String getStdout() {
    return testResultInstance.getStdout();
  }

  @Override
  public String getStderr() {
    return testResultInstance.getStderr();
  }

  /**
   * If there was an error or a failure, this is the stack trace, or otherwise null.
   */
  @Override
  public String getErrorStackTrace() {
    return testResultInstance.getErrorStackTrace();
  }

  /**
   * If there was an error or a failure, this is the text from the message.
   */
  @Override
  public String getErrorDetails() {
    return testResultInstance.getErrorDetails();
  }

  /**
   * @return true if the test was not skipped and did not fail, false otherwise.
   */
  @Override
  public boolean isPassed() {
    return (getFailCount() == 0);
  }

  @Override
  public Collection<FlakyPackageResult> getChildren() {
    return byPackages.values();
  }

  /**
   * Whether this test result has children.
   */
  @Override
  public boolean hasChildren() {
    return !suites.isEmpty();
  }

  @Exported(inline=true,visibility=9)
  public Collection<FlakySuiteResult> getSuites() {
    return suites;
  }


  @Override
  public String getName() {
    return "junit";
  }

  @Override
  public Object getDynamic(String token, StaplerRequest req, StaplerResponse rsp) {
    if (token.equals(getId())) {
      return this;
    }

    FlakyPackageResult result = byPackage(token);
    if (result != null) {
      return result;
    } else {
      return super.getDynamic(token, req, rsp);
    }
  }

  public FlakyPackageResult byPackage(String packageName) {
    return byPackages.get(packageName);
  }

  public FlakySuiteResult getSuite(String name) {
    return suitesByName.get(name);
  }

  @Override
  public void setParentAction(AbstractTestResultAction action) {
    this.parentAction = action;
    tally(); // I want to be sure to inform our children when we get an action.
  }

  @Override
  public AbstractTestResultAction getParentAction() {
    return this.parentAction;
  }

  /**
   * Recount my children.
   */
  @Override
  public void tally() {
    suitesByName = new HashMap<String, FlakySuiteResult>();
    failedTests = new ArrayList<FlakyCaseResult>();
    flakyTests = new ArrayList<FlakyCaseResult>();
    passedTests = new ArrayList<FlakyCaseResult>();
    byPackages = new TreeMap<String, FlakyPackageResult>();

    totalTests = 0;
    skippedTests = 0;

    // Ask all of our children to tally themselves
    for (FlakySuiteResult s : suites) {
      s.setParent(this); // kluge to prevent double-counting the results
      suitesByName.put(s.getName(), s);
      List<FlakyCaseResult> cases = s.getCases();

      for (FlakyCaseResult cr : cases) {
        cr.setParentAction(this.parentAction);
        cr.setParentSuiteResult(s);
        cr.tally();
        String pkg = cr.getPackageName(), spkg = safe(pkg);
        FlakyPackageResult pr = byPackage(spkg);
        if (pr == null) {
          byPackages.put(spkg, pr = new FlakyPackageResult(this, pkg));
        }
        pr.add(cr);
      }
    }

    for (FlakyPackageResult pr : byPackages.values()) {
      pr.tally();
      skippedTests += pr.getSkipCount();
      failedTests.addAll(pr.getFailedTests());
      flakyTests.addAll(pr.getFlakyTests());
      passedTests.addAll((Collection<? extends FlakyCaseResult>) pr.getPassedTests());
      totalTests += pr.getTotalCount();
    }
  }

  /**
   * Builds up the transient part of the data structure
   * from results {@link #parse(File) parsed} so far.
   *
   * <p>
   * After the data is frozen, more files can be parsed
   * and then freeze can be called again.
   */
  public void freeze(AbstractTestResultAction parent, AbstractBuild build) {
    this.parentAction = parent;
    this.owner = build;
    if(suitesByName==null) {
      // freeze for the first time
      suitesByName = new HashMap<String,FlakySuiteResult>();
      totalTests = 0;
      failedTests = new ArrayList<FlakyCaseResult>();
      flakyTests = new ArrayList<FlakyCaseResult>();
      passedTests = new ArrayList<FlakyCaseResult>();
      byPackages = new TreeMap<String,FlakyPackageResult>();
    }

    for (FlakySuiteResult s : suites) {
      if (!s.freeze(this))      // this is disturbing: has-a-parent is conflated with has-been-counted
      {
        continue;
      }

      suitesByName.put(s.getName(), s);

      totalTests += s.getCases().size();
      for (FlakyCaseResult cr : s.getCases()) {
        if (cr.isSkipped()) {
          skippedTests++;
        } else if (!cr.isPassed()) {
          failedTests.add(cr);
        } else if (cr.isFlaked()) {
          flakyTests.add(cr);
        } else {
          // if a test passed without a flake
          passedTests.add(cr);
        }

        String pkg = cr.getPackageName(), spkg = safe(pkg);
        FlakyPackageResult pr = byPackage(spkg);
        if (pr == null) {
          byPackages.put(spkg, pr = new FlakyPackageResult(this, pkg));
        }
        pr.add(cr);
      }
    }

    for (FlakyPackageResult pr : byPackages.values())
      pr.freeze();
  }

  /**
   *
   * Get the map between test name and a {@link SingleTestFlakyStatsWithRevision},
   * which counts all its passing runs and failing runs. No flake will be included, because a flake
   * will be decomposed into a passing run with several failing runs.
   *
   * @return the map between test name and a {@link SingleTestFlakyStatsWithRevision},
   */
  public Map<String, SingleTestFlakyStatsWithRevision> getTestFlakyStatsMap() {

    Map<String, SingleTestFlakyStatsWithRevision> testFlakyStatsWithRevisionMap =
        new HashMap<String, SingleTestFlakyStatsWithRevision>();

    for (FlakyCaseResult passedTest : passedTests) {
      testFlakyStatsWithRevisionMap.put(passedTest.getFullDisplayName(),
          new SingleTestFlakyStatsWithRevision(new SingleTestFlakyStats(1, 0, 0), owner));
    }

    for (FlakyCaseResult failedTest : failedTests) {
      int flakyRetry = failedTest.getFlakyRuns() == null ? 0 : failedTest.getFlakyRuns().size();
      testFlakyStatsWithRevisionMap.put(failedTest.getFullDisplayName(),
          new SingleTestFlakyStatsWithRevision(new SingleTestFlakyStats(0, 1 + flakyRetry, 0),
              owner));
    }

    for (FlakyCaseResult flakyTest : flakyTests) {
      int flakyRetry = flakyTest.getFlakyRuns() == null ? 0 : flakyTest.getFlakyRuns().size();
      testFlakyStatsWithRevisionMap.put(flakyTest.getFullDisplayName(),
          new SingleTestFlakyStatsWithRevision(new SingleTestFlakyStats(1, flakyRetry, 0),
              owner));
    }

    return testFlakyStatsWithRevisionMap;
  }

  private static final long serialVersionUID = 1L;

}
