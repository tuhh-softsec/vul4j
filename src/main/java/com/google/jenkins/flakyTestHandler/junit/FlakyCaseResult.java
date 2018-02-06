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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.google.jenkins.flakyTestHandler.plugin.JUnitFlakyTestDataAction;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import hudson.model.AbstractBuild;
import hudson.tasks.junit.Messages;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestNameTransformer;
import hudson.tasks.test.TestResult;
import hudson.util.TextFile;

/**
 * One test result augmented with flaky information.
 * Majority of code copied from hudson.tasks.junit.CaseResult
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/
 * junit/CaseResult.java
 *
 * @author Qingzhou Luo
 */
public class FlakyCaseResult extends TestResult implements Comparable<FlakyCaseResult>,
    ActionableFlakyTestObject {
  private static final Logger LOGGER = Logger.getLogger(FlakyCaseResult.class.getName());
  private final float duration;
  /**
   * In JUnit, a test is a method of a class. This field holds the fully qualified class name
   * that the test was in.
   */
  private final String className;
  /**
   * This field retains the method name.
   */
  private final String testName;
  private transient String safeName;
  private final boolean skipped;
  private final String skippedMessage;
  private final String errorStackTrace;
  private final String errorDetails;
  private transient FlakySuiteResult parent;

  private transient FlakyClassResult classResult;

  /**
   * Some tools report stdout and stderr at testcase level (such as Maven surefire plugin), others do so at
   * the suite level (such as Ant JUnit task.)
   *
   * If these information are reported at the test case level, these fields are set,
   * otherwise null, in which case {@link FlakySuiteResult#stdout}.
   */
  private final String stdout,stderr;

  private final List<FlakyRunInformation> flakyRuns;

  private static float parseTime(Element testCase) {
    String time = testCase.attributeValue("time");
    if(time!=null) {
      time = time.replace(",","");
      try {
        return Float.parseFloat(time);
      } catch (NumberFormatException e) {
        try {
          return new DecimalFormat().parse(time).floatValue();
        } catch (ParseException x) {
          // hmm, don't know what this format is.
        }
      }
    }
    return 0.0f;
  }

  FlakyCaseResult(FlakySuiteResult parent, Element testCase, String testClassName, boolean keepLongStdio) {

    String nameAttr = testCase.attributeValue("name");
    if(testClassName==null && nameAttr.contains(".")) {
      testClassName = nameAttr.substring(0,nameAttr.lastIndexOf('.'));
      nameAttr = nameAttr.substring(nameAttr.lastIndexOf('.')+1);
    }

    className = testClassName;
    testName = nameAttr;
    errorStackTrace = getError(testCase);
    errorDetails = getErrorMessage(testCase);
    this.parent = parent;
    duration = parseTime(testCase);
    skipped = isMarkedAsSkipped(testCase);
    skippedMessage = getSkippedMessage(testCase);
    @SuppressWarnings("LeakingThisInConstructor")
    Collection<FlakyCaseResult> _this = Collections.singleton(this);
    stdout = possiblyTrimStdio(_this, keepLongStdio, testCase.elementText("system-out"));
    stderr = possiblyTrimStdio(_this, keepLongStdio, testCase.elementText("system-err"));

    // Add flaky tests information
    List flakyElements = getAllFlakyElements(testCase);
    flakyRuns = getFlakyRunInformation(flakyElements);
  }

  private static final int HALF_MAX_SIZE = 500;
  static String possiblyTrimStdio(Collection<FlakyCaseResult> results, boolean keepLongStdio, String stdio) { // HUDSON-6516
    if (stdio == null) {
      return null;
    }
    if (!isTrimming(results, keepLongStdio)) {
      return stdio;
    }
    int len = stdio.length();
    int middle = len - HALF_MAX_SIZE * 2;
    if (middle <= 0) {
      return stdio;
    }
    return stdio.subSequence(0, HALF_MAX_SIZE) + "\n...[truncated " + middle + " chars]...\n" + stdio.subSequence(len - HALF_MAX_SIZE, len);
  }

  /**
   * Flavor of {@link #possiblyTrimStdio(Collection, boolean, String)} that doesn't try to read the whole thing into memory.
   */
  static String possiblyTrimStdio(Collection<FlakyCaseResult> results, boolean keepLongStdio, File stdio) throws IOException {
    if (!isTrimming(results, keepLongStdio) && stdio.length()<1024*1024) {
      return FileUtils.readFileToString(stdio);
    }

    long len = stdio.length();
    long middle = len - HALF_MAX_SIZE * 2;
    if (middle <= 0) {
      return FileUtils.readFileToString(stdio);
    }

    TextFile tx = new TextFile(stdio);
    String head = tx.head(HALF_MAX_SIZE);
    String tail = tx.fastTail(HALF_MAX_SIZE);

    int headBytes = head.getBytes().length;
    int tailBytes = tail.getBytes().length;

    middle = len - (headBytes+tailBytes);
    if (middle<=0) {
      // if it turns out that we didn't have any middle section, just return the whole thing
      return FileUtils.readFileToString(stdio);
    }

    return head + "\n...[truncated " + middle + " bytes]...\n" + tail;
  }

  private static boolean isTrimming(Collection<FlakyCaseResult> results, boolean keepLongStdio) {
    if (keepLongStdio)      return false;
    for (FlakyCaseResult result : results) {
      // if there's a failure, do not trim and keep the whole thing
      if (result.errorStackTrace != null)
        return false;
    }
    return true;
  }

  private static List<Element> getAllFlakyElements(Element testCase) {
    List<Element> flakyElements = new ArrayList<Element>();
    for (Object object : testCase.elements()) {
      Element element = (Element) object;
      if (element.getName().equals("flakyFailure") || element.getName().equals("flakyError")
          || element.getName()
          .equals("rerunFailure") || element.getName().equals("rerunError")) {
        flakyElements.add(element);
      }
    }
    return flakyElements;
  }

  private static List<FlakyRunInformation> getFlakyRunInformation(List<Element> flakyElements) {
    List<FlakyRunInformation> flakyRunInformation = new ArrayList<FlakyRunInformation>();

    for (Element flakyElement : flakyElements) {
      // Set errorDetails
      String errorDetails = flakyElement.attributeValue("message");

      // Set errorStackTrace
      String errorStackTrace = flakyElement.getText();

      // Set system-out and system-err
      String flakyStdout = flakyElement.elementText("system-out");
      String flakyStderr = flakyElement.elementText("system-err");

      flakyRunInformation
          .add(new FlakyRunInformation(errorDetails, errorStackTrace, flakyStdout, flakyStderr));
    }
    return flakyRunInformation;
  }

  /**
   * Used to create a fake failure, when Hudson fails to load data from XML files.
   *
   * Public since 1.526.
   * @param parent suite result
   * @param testName test name
   * @param errorStackTrace stack trace
   */
  public FlakyCaseResult(FlakySuiteResult parent, String testName, String errorStackTrace) {
    this.className = parent == null ? "unnamed" : parent.getName();
    this.testName = testName;
    this.errorStackTrace = errorStackTrace;
    this.errorDetails = "";
    this.parent = parent;
    this.stdout = null;
    this.stderr = null;
    this.duration = 0.0f;
    this.skipped = false;
    this.skippedMessage = null;
    this.flakyRuns = new ArrayList<FlakyRunInformation>();
  }

  public FlakyClassResult getParent() {
    return classResult;
  }

  private static String getError(Element testCase) {
    String msg = testCase.elementText("error");
    if(msg!=null)
      return msg;
    return testCase.elementText("failure");
  }

  private static String getErrorMessage(Element testCase) {

    Element msg = testCase.element("error");
    if (msg == null) {
      msg = testCase.element("failure");
    }
    if (msg == null) {
      return null; // no error or failure elements! damn!
    }

    return msg.attributeValue("message");
  }

  /**
   * If the testCase element includes the skipped element (as output by TestNG), then
   * the test has neither passed nor failed, it was never run.
   */
  private static boolean isMarkedAsSkipped(Element testCase) {
    return testCase.element("skipped") != null;
  }

  private static String getSkippedMessage(Element testCase) {
    String message = null;
    Element skippedElement = testCase.element("skipped");

    if (skippedElement != null) {
      message = skippedElement.attributeValue("message");
    }

    return message;
  }

  public String getDisplayName() {
    return TestNameTransformer.getTransformedName(testName);
  }

  public List<FlakyRunInformation> getFlakyRuns() {
    return flakyRuns;
  }

  /**
   * Gets the name of the test, which is returned from {@code TestCase.getName()}
   *
   * <p>
   * Note that this may contain any URL-unfriendly character.
   */
  @Exported(visibility=999)
  public @Override String getName() {
    return testName;
  }

  /**
   * Gets the human readable title of this result object.
   */
  @Override
  public String getTitle() {
    return "Case Result: " + getDisplayName();
  }

  /**
   * Gets the duration of the test, in seconds
   */
  @Exported(visibility=9)
  public float getDuration() {
    return duration;
  }

  /**
   * Gets the version of {@link #getName()} that's URL-safe.
   */
  public @Override synchronized String getSafeName() {
    if (safeName != null) {
      return safeName;
    }
    StringBuilder buf = new StringBuilder(testName);
    for( int i=0; i<buf.length(); i++ ) {
      char ch = buf.charAt(i);
      if(!Character.isJavaIdentifierPart(ch))
        buf.setCharAt(i,'_');
    }
    Collection<FlakyCaseResult> siblings = (classResult ==null ? Collections.<FlakyCaseResult>emptyList(): classResult.getChildren());
    return safeName = uniquifyName(siblings, buf.toString());
  }

  /**
   * Gets the class name of a test class.
   *
   * @return class name
   */
  @Exported(visibility=9)
  public String getClassName() {
    return className;
  }

  /**
   * Gets the simple (not qualified) class name.
   *
   * @return simple class name
   */
  public String getSimpleName() {
    int idx = className.lastIndexOf('.');
    return className.substring(idx+1);
  }

  /**
   * Gets the package name of a test case
   *
   * @return package name
   */
  public String getPackageName() {
    int idx = className.lastIndexOf('.');
    if(idx<0)       return "(root)";
    else            return className.substring(0,idx);
  }

  public String getFullName() {
    return className+'.'+getName();
  }

  /**
   * @since 1.515
   * @return full display name
   */
  public String getFullDisplayName() {
    return TestNameTransformer.getTransformedName(getFullName());
  }

  @Override
  public int getFailCount() {
    if (isFailed()) return 1; else return 0;
  }

  @Override
  public int getSkipCount() {
    if (isSkipped()) return 1; else return 0;
  }

  @Override
  public int getPassCount() {
    return isPassed() ? 1 : 0;
  }

  /**
   * The stdout of this test.
   *
   * <p>
   * Depending on the tool that produced the XML report, this method works somewhat inconsistently.
   * With some tools (such as Maven surefire plugin), you get the accurate information, that is
   * the stdout from this test case. With some other tools (such as the JUnit task in Ant), this
   * method returns the stdout produced by the entire test suite.
   *
   * <p>
   * If you need to know which is the case, compare this output from {@link FlakySuiteResult#getStdout()}.
   * @since 1.294
   */
  @Exported
  public String getStdout() {
    if(stdout!=null)    return stdout;
    FlakySuiteResult sr = getSuiteResult();
    if (sr==null) return "";
    return getSuiteResult().getStdout();
  }

  /**
   * The stderr of this test.
   *
   * @see #getStdout()
   * @since 1.294
   */
  @Exported
  public String getStderr() {
    if(stderr!=null)    return stderr;
    FlakySuiteResult sr = getSuiteResult();
    if (sr==null) return "";
    return getSuiteResult().getStderr();
  }

  @Override
  public FlakyCaseResult getPreviousResult() {
    if (parent == null) return null;
    FlakySuiteResult pr = parent.getPreviousResult();
    if(pr==null)    return null;
    return pr.getCase(getName());
  }

  /**
   * Case results have no children
   * @return null
   */
  @Override
  public TestResult findCorrespondingResult(String id) {
    if (id.equals(safe(getName()))) {
      return this;
    }
    return null;
  }

  /**
   * Gets the "children" of this test result that failed
   *
   * @return the children of this test result, if any, or an empty collection
   */
  @Override
  public Collection<? extends TestResult> getFailedTests() {
    return singletonListOfThisOrEmptyList(isFailed());
  }

  /**
   * Gets the "children" of this test result that passed
   *
   * @return the children of this test result, if any, or an empty collection
   */
  @Override
  public Collection<? extends TestResult> getPassedTests() {
    return singletonListOfThisOrEmptyList(isPassed());
  }

  /**
   * Gets the "children" of this test result that were skipped
   *
   * @return the children of this test result, if any, or an empty list
   */
  @Override
  public Collection<? extends TestResult> getSkippedTests() {
    return singletonListOfThisOrEmptyList(isSkipped());
  }

  private Collection<? extends TestResult> singletonListOfThisOrEmptyList(boolean f) {
    if (f)
      return singletonList(this);
    else
      return emptyList();
  }

  /**
   * If there was an error or a failure, this is the stack trace, or otherwise null.
   */
  @Exported
  public String getErrorStackTrace() {
    return errorStackTrace;
  }

  /**
   * If there was an error or a failure, this is the text from the message.
   */
  @Exported
  public String getErrorDetails() {
    return errorDetails;
  }

  /**
   * @return true if the test was not skipped and did not fail, false otherwise.
   */
  public boolean isPassed() {
    return !skipped && errorStackTrace==null;
  }

  /**
   * Tests whether the test was skipped or not.  TestNG allows tests to be
   * skipped if their dependencies fail or they are part of a group that has
   * been configured to be skipped.
   * @return true if the test was not executed, false otherwise.
   */
  @Exported(visibility=9)
  public boolean isSkipped() {
    return skipped;
  }

  /**
   * @return true if the test was not skipped and did not pass, false otherwise.
   * @since 1.520
   */
  public boolean isFailed() {
    return !isPassed() && !isSkipped();
  }

  public boolean isFlaked() {
    return isPassed() && (flakyRuns != null && flakyRuns.size() > 0);
  }

  /**
   * Provides the reason given for the test being being skipped.
   * @return the message given for a skipped test if one has been provided, null otherwise.
   * @since 1.507
   */
  @Exported
  public String getSkippedMessage() {
    return skippedMessage;
  }

  public FlakySuiteResult getSuiteResult() {
    return parent;
  }

  @Override
  public AbstractBuild<?,?> getOwner() {
    FlakySuiteResult sr = getSuiteResult();
    if (sr==null) {
      LOGGER.warning("In getOwner(), getSuiteResult is null"); return null; }
    FlakyTestResult tr = sr.getParent();
    if (tr==null) {
      LOGGER.warning("In getOwner(), suiteResult.getParent() is null."); return null; }
    return tr.getOwner();
  }

  public void setParentSuiteResult(FlakySuiteResult parent) {
    this.parent = parent;
  }

  public void freeze(FlakySuiteResult parent) {
    this.parent = parent;
  }

  public int compareTo(FlakyCaseResult that) {
    return this.getFullName().compareTo(that.getFullName());
  }

  @Exported(name="status",visibility=9) // because stapler notices suffix 's' and remove it
  public Status getStatus() {
    if (skipped) {
      return Status.SKIPPED;
    }
    FlakyCaseResult pr = getPreviousResult();
    if(pr==null) {
      return isPassed() ? Status.PASSED : Status.FAILED;
    }

    if(pr.isPassed()) {
      return isPassed() ? Status.PASSED : Status.REGRESSION;
    } else {
      return isPassed() ? Status.FIXED : Status.FAILED;
    }
  }

  @Override
  public TestAction getTestAction() {
    return new JUnitFlakyTestDataAction(getFlakyRuns(), isFailed());
  }

  /*package*/ void setClass(FlakyClassResult classResult) {
    this.classResult = classResult;
  }

  void replaceParent(FlakySuiteResult parent) {
    this.parent = parent;
  }

  /**
   * Constants that represent the status of this test.
   */
  public enum Status {
    /**
     * This test runs OK, just like its previous run.
     */
    PASSED("result-passed",Messages._CaseResult_Status_Passed(),true),
    /**
     * This test was skipped due to configuration or the
     * failure or skipping of a method that it depends on.
     */
    SKIPPED("result-skipped",Messages._CaseResult_Status_Skipped(),false),
    /**
     * This test failed, just like its previous run.
     */
    FAILED("result-failed",Messages._CaseResult_Status_Failed(),false),
    /**
     * This test has been failing, but now it runs OK.
     */
    FIXED("result-fixed",Messages._CaseResult_Status_Fixed(),true),
    /**
     * This test has been running OK, but now it failed.
     */
    REGRESSION("result-regression", Messages._CaseResult_Status_Regression(),false);

    private final String cssClass;
    private final Localizable message;
    public final boolean isOK;

    Status(String cssClass, Localizable message, boolean OK) {
      this.cssClass = cssClass;
      this.message = message;
      isOK = OK;
    }

    public String getCssClass() {
      return cssClass;
    }

    public String getMessage() {
      return message.toString();
    }

    public boolean isRegression() {
      return this==REGRESSION;
    }
  }

  public static class FlakyRunInformation implements Serializable {

    public FlakyRunInformation(String flakyErrorDetails, String flakyErrorStackTrace,
        String flakyStdOut, String flakyStdErr) {
      this.flakyErrorDetails = flakyErrorDetails;
      this.flakyErrorStackTrace = flakyErrorStackTrace;
      this.flakyStdOut = flakyStdOut;
      this.flakyStdErr = flakyStdErr;
    }

    final String flakyErrorDetails;

    final String flakyErrorStackTrace;

    final String flakyStdOut;

    final String flakyStdErr;

    public String getFlakyErrorDetails() {
      return flakyErrorDetails;
    }

    public String getFlakyErrorStackTrace() {
      return flakyErrorStackTrace;
    }

    public String getFlakyStdOut() {
      return flakyStdOut;
    }

    public String getFlakyStdErr() {
      return flakyStdErr;
    }
  }

  private static final long serialVersionUID = 1L;
}
