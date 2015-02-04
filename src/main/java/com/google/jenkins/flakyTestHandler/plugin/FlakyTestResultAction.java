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

import com.google.jenkins.flakyTestHandler.junit.FlakyCaseResult;
import com.google.jenkins.flakyTestHandler.junit.FlakyTestResult;
import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStatsWithRevision;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.util.HeapSpaceStringConverter;
import hudson.util.XStream2;
import jenkins.model.RunAction2;

/**
 * Action for each test run, to record flaky stats for all the tests
 *
 * @author Qingzhou Luo
 */
public class FlakyTestResultAction implements RunAction2 {

  /**
   * FlakyRunStats object, use WeakReference to reduce memory overhead since it will be
   * stored on the disk
   */
  private transient WeakReference<FlakyRunStats> flakyRunStats;

  private static final XStream XSTREAM = new XStream2();

  /**
   * This build
   */
  private AbstractBuild<?,?> build;

  public static final Logger logger = Logger.getLogger(FlakyTestResultAction.class.getName());

  static {
    XSTREAM.registerConverter(new HeapSpaceStringConverter(),100);
  }

  /**
   * Construct a FlakyTestResultAction object with AbstractBuild and BuildListener
   *
   * @param build this build
   * @param listener listener of this build
   */
  public FlakyTestResultAction(AbstractBuild build, BuildListener listener) {
    this.build = build;
    // TODO consider the possibility that there is >1 such action
    AbstractTestResultAction action = build.getAction(AbstractTestResultAction.class);
    if (action != null) {
      Object latestResult = action.getResult();
      if (latestResult != null && latestResult instanceof TestResult) {
        FlakyTestResult flakyTestResult = new FlakyTestResult((TestResult) latestResult);

        flakyTestResult.freeze(action, build);
        FlakyRunStats stats = new FlakyRunStats(flakyTestResult.getTestFlakyStatsMap());
        setFlakyRunStats(stats, listener);
      }
    } else {
      logger.log(Level.WARNING, "No test result found, please publish junit report first");
    }
  }

  /**
   * Empty constructor for testing purpose
   */
  // Visible for testing
  FlakyTestResultAction() {

  }

  private XmlFile getDataFile() {
    return new XmlFile(XSTREAM,new File(build.getRootDir(), "junitFlakyStatsResult.xml"));
  }

  /**
   * Loads a {@link TestResult} from disk.
   */
  private FlakyRunStats load() {
    FlakyRunStats stats;
    try {
      stats = (FlakyRunStats)getDataFile().read();
    } catch (IOException e) {
      stats = new FlakyRunStats();   // return a dummy
    }
    return stats;
  }

  @Override
  public void onAttached(Run<?, ?> r) {
    this.build = (AbstractBuild<?,?>) r;
  }

  @Override
  public void onLoad(Run<?, ?> r) {
    this.build = (AbstractBuild<?,?>) r;
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return null;
  }

  public synchronized FlakyRunStats getFlakyRunStats() {
    FlakyRunStats stats;
    if (flakyRunStats == null) {
      stats = load();
      flakyRunStats = new WeakReference<FlakyRunStats>(stats);
    } else {
      stats = flakyRunStats.get();
    }

    if (stats == null) {
      stats = load();
      flakyRunStats = new WeakReference<FlakyRunStats>(stats);
    }
    return stats;
  }

  // Visible for testing
  synchronized void setFlakyRunStats(FlakyRunStats stats) {
    flakyRunStats = new WeakReference<FlakyRunStats>(stats);
  }

  /**
   * Overwrites the {@link FlakyRunStats} by a new data set.
   */
  public synchronized void setFlakyRunStats(FlakyRunStats stats, BuildListener listener) {

    // persist the data
    try {
      getDataFile().write(stats);
    } catch (IOException e) {
      e.printStackTrace(listener.fatalError("Failed to save the JUnit flaky test stats result"));
    }
    this.flakyRunStats = new WeakReference<FlakyRunStats>(stats);
  }

  /**
   * Get display names for all the test cases
   *
   * @param results Collection of {@link hudson.tasks.junit.TestResult} objects
   * @return the set of display names for all the test cases
   */
  public static Set<String> getTestIdFromTestResults(
      Collection<? extends hudson.tasks.test.TestResult> results) {
    Set<String> testIdSet = new HashSet<String>();
    for (hudson.tasks.test.TestResult testResult : results) {
      if (testResult instanceof FlakyCaseResult) {
        testIdSet.add(((FlakyCaseResult)testResult).getFullDisplayName());
      }
    }
    return testIdSet;
  }

  /**
   * Class to hold all the passing/failing/flaky tests for one run
   */
  public static class FlakyRunStats {

    /**
     * Map between test case name and its flaky stats with revision info
     */
    Map<String, SingleTestFlakyStatsWithRevision> testFlakyStatsWithRevisionMap;

    public FlakyRunStats() {
      this.testFlakyStatsWithRevisionMap = new HashMap<String, SingleTestFlakyStatsWithRevision>();
    }

    public FlakyRunStats(Map<String, SingleTestFlakyStatsWithRevision>
        testFlakyStatsWithRevisionMap) {
      this.testFlakyStatsWithRevisionMap = testFlakyStatsWithRevisionMap;
    }

    public Map<String, SingleTestFlakyStatsWithRevision> getTestFlakyStatsWithRevisionMap() {
      return testFlakyStatsWithRevisionMap;
    }

    /**
     * Is current run flaky or not. Build will be marked as unstable if there are flaky tests
     * and no failing test
     *
     * @return true if there is no failing test but there are some flaky tests
     */
    public boolean isFlaked() {
      if (testFlakyStatsWithRevisionMap == null) {
        return false;
      }

      boolean seenFlake = false;
      for (Map.Entry<String, SingleTestFlakyStatsWithRevision>
          singleTestFlakyStatsWithRevisionEntry : testFlakyStatsWithRevisionMap.entrySet()) {
        if (singleTestFlakyStatsWithRevisionEntry.getValue().getStats().isFailed()) {
          return false;
        } else if (singleTestFlakyStatsWithRevisionEntry.getValue().getStats().isFlaked()) {
          seenFlake = true;
        }
      }
      return seenFlake;
    }
  }
}
