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

/**
 *
 * Test aggregation logic
 *
 * @author Qingzhou Luo
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.google.jenkins.flakyTestHandler.plugin.FlakyTestResultAction.FlakyRunStats;
import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStats;
import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStatsWithRevision;
import com.google.jenkins.flakyTestHandler.plugin.deflake.DeflakeCause;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hudson.model.AbstractBuild;
import hudson.model.CauseAction;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

public class HistoryAggregatedFlakyTestResultActionTest {

  private final static String TEST_ONE = "testOne";

  private final static String TEST_TWO = "testTwo";

  private final static String TEST_THREE = "testThree";

  private final static String TEST_FOUR = "testFour";

  private final static String REVISION_ONE = "revision_one";

  private final static String REVISION_TWO = "revision_two";

  private final static int TOTAL_RUNS = 2;

  @Rule
  public JenkinsRule jenkins = new JenkinsRule();

  @Test
  public void testAggregateFlakyRunsWithRevisions() throws Exception {

    FreeStyleProject project = jenkins.createFreeStyleProject("project");

    List<AbstractBuild> buildList = new ArrayList<AbstractBuild>();

    for (FlakyTestResultAction action : setUpFlakyTestResultAction()) {
      FreeStyleBuild build = new FreeStyleBuild(project);
      build.addAction(action);
      buildList.add(build);
    }

    HistoryAggregatedFlakyTestResultAction action = new HistoryAggregatedFlakyTestResultAction(
        null);

    for (AbstractBuild build : buildList) {
      action.aggregateOneBuild(build);
    }

    Map<String, Map<String, SingleTestFlakyStats>> statsMapOverRevision =
        action.getAggregatedTestFlakyStatsWithRevision();

    // TEST_ONE
    SingleTestFlakyStats testOneRevisionOneStats = statsMapOverRevision.get(TEST_ONE)
        .get(REVISION_ONE);
    assertEquals("wrong number passes", 2, testOneRevisionOneStats.getPass());
    assertEquals("wrong number fails", 0, testOneRevisionOneStats.getFail());
    assertEquals("wrong number flakes", 0, testOneRevisionOneStats.getFlake());

    SingleTestFlakyStats testOneRevisionTwoStats = statsMapOverRevision.get(TEST_ONE)
        .get(REVISION_TWO);
    assertEquals("wrong number passes", 1, testOneRevisionTwoStats.getPass());
    assertEquals("wrong number fails", 1, testOneRevisionTwoStats.getFail());
    assertEquals("wrong number flakes", 0, testOneRevisionTwoStats.getFlake());

    // TEST_TWO
    SingleTestFlakyStats testTwoRevisionOneStats = statsMapOverRevision.get(TEST_TWO)
        .get(REVISION_ONE);
    assertEquals("wrong number passes", 1, testTwoRevisionOneStats.getPass());
    assertEquals("wrong number fails", 3, testTwoRevisionOneStats.getFail());
    assertEquals("wrong number flakes", 0, testTwoRevisionOneStats.getFlake());

    SingleTestFlakyStats testTwoRevisionTwoStats = statsMapOverRevision.get(TEST_TWO)
        .get(REVISION_TWO);
    assertEquals("wrong number passes", 1, testTwoRevisionTwoStats.getPass());
    assertEquals("wrong number fails", 0, testTwoRevisionTwoStats.getFail());
    assertEquals("wrong number flakes", 0, testTwoRevisionTwoStats.getFlake());

    // TEST_THREE
    SingleTestFlakyStats testThreeRevisionOneStats = statsMapOverRevision.get(TEST_THREE)
        .get(REVISION_ONE);
    assertEquals("wrong number passes", 1, testThreeRevisionOneStats.getPass());
    assertEquals("wrong number fails", 2, testThreeRevisionOneStats.getFail());
    assertEquals("wrong number flakes", 0, testThreeRevisionOneStats.getFlake());

    SingleTestFlakyStats testThreeRevisionTwoStats = statsMapOverRevision.get(TEST_THREE)
        .get(REVISION_TWO);
    assertEquals("wrong number passes", 0, testThreeRevisionTwoStats.getPass());
    assertEquals("wrong number fails", 2, testThreeRevisionTwoStats.getFail());
    assertEquals("wrong number flakes", 0, testThreeRevisionTwoStats.getFlake());

    // TEST_FOUR
    SingleTestFlakyStats testFourRevisionOneStats = statsMapOverRevision.get(TEST_FOUR)
        .get(REVISION_ONE);
    assertEquals("wrong number passes", 1, testFourRevisionOneStats.getPass());
    assertEquals("wrong number fails", 3, testFourRevisionOneStats.getFail());
    assertEquals("wrong number flakes", 0, testFourRevisionOneStats.getFlake());

    SingleTestFlakyStats testFourRevisionTwoStats = statsMapOverRevision.get(TEST_FOUR)
        .get(REVISION_TWO);
    assertEquals("wrong number passes", 1, testFourRevisionTwoStats.getPass());
    assertEquals("wrong number fails", 0, testFourRevisionTwoStats.getFail());
    assertEquals("wrong number flakes", 0, testFourRevisionTwoStats.getFlake());
  }

  @Test
  public void testAggregate() throws Exception {
    FreeStyleProject project = jenkins.createFreeStyleProject("project");
    List<FlakyTestResultAction> flakyTestResultActions = setUpFlakyTestResultAction();

    List<FlakyTestResultAction> flakyTestResultActionList = new ArrayList<FlakyTestResultAction>(
        flakyTestResultActions);

    // First non-deflake build
    AbstractBuild firstBuild = (AbstractBuild) project
        .scheduleBuild2(0, flakyTestResultActionList.get(0)).get();
    while (firstBuild.isBuilding()) {
      Thread.sleep(100);
    }

    // Second deflake build
    AbstractBuild secondBuild = (AbstractBuild) project
        .scheduleBuild2(0, flakyTestResultActionList.get(1),
            new CauseAction(new DeflakeCause(firstBuild))).get();
    while (secondBuild.isBuilding()) {
      Thread.sleep(100);
    }

    // Third deflake build with HistoryAggregatedFlakyTestResultAction
    AbstractBuild thirdBuild = (AbstractBuild) project
        .scheduleBuild2(0, flakyTestResultActionList.get(2),
            new HistoryAggregatedFlakyTestResultAction(project)).get();
    while (thirdBuild.isBuilding()) {
      Thread.sleep(100);
    }

    HistoryAggregatedFlakyTestResultAction action = thirdBuild
        .getAction(HistoryAggregatedFlakyTestResultAction.class);
    action.aggregate();

    Map<String, SingleTestFlakyStats> aggregatedFlakyStatsMap = action.getAggregatedFlakyStats();

    // Make sure revisions are inserted in the order of their appearance
    Map<String, SingleTestFlakyStats> revisionMap = action.getAggregatedTestFlakyStatsWithRevision()
        .get(TEST_ONE);
    assertArrayEquals("Incorrect revision history", new String[]{REVISION_ONE, REVISION_TWO},
        revisionMap.keySet().toArray(new String[revisionMap.size()]));

    assertEquals("wrong number of entries for flaky stats", 4, aggregatedFlakyStatsMap.size());

    SingleTestFlakyStats testOneStats = aggregatedFlakyStatsMap.get(TEST_ONE);
    SingleTestFlakyStats testTwoStats = aggregatedFlakyStatsMap.get(TEST_TWO);
    SingleTestFlakyStats testThreeStats = aggregatedFlakyStatsMap.get(TEST_THREE);
    SingleTestFlakyStats testFourStats = aggregatedFlakyStatsMap.get(TEST_FOUR);

    assertEquals("wrong number passes", 1, testOneStats.getPass());
    assertEquals("wrong number fails", 0, testOneStats.getFail());
    assertEquals("wrong number flakes", 1, testOneStats.getFlake());

    assertEquals("wrong number passes", 1, testTwoStats.getPass());
    assertEquals("wrong number fails", 0, testTwoStats.getFail());
    assertEquals("wrong number flakes", 1, testTwoStats.getFlake());

    assertEquals("wrong number passes", 0, testThreeStats.getPass());
    assertEquals("wrong number fails", 1, testThreeStats.getFail());
    assertEquals("wrong number flakes", 1, testThreeStats.getFlake());

    assertEquals("wrong number passes", 1, testFourStats.getPass());
    assertEquals("wrong number fails", 0, testFourStats.getFail());
    assertEquals("wrong number flakes", 1, testFourStats.getFlake());
  }


  public static List<FlakyTestResultAction> setUpFlakyTestResultAction() {
    FlakyTestResultAction actionOne = new FlakyTestResultAction();
    FlakyTestResultAction actionTwo = new FlakyTestResultAction();
    FlakyTestResultAction actionThree = new FlakyTestResultAction();

    Map<String, SingleTestFlakyStatsWithRevision> testFlakyStatsWithRevisionMap =
        new HashMap<String, SingleTestFlakyStatsWithRevision>();
    testFlakyStatsWithRevisionMap.put(TEST_ONE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.PASSED));
    testFlakyStatsWithRevisionMap.put(TEST_TWO,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.FAILED));
    testFlakyStatsWithRevisionMap.put(TEST_THREE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.PASSED));
    testFlakyStatsWithRevisionMap.put(TEST_FOUR,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.FLAKED));

    FlakyRunStats actionOneResult = new FlakyRunStats(testFlakyStatsWithRevisionMap);

    testFlakyStatsWithRevisionMap = new HashMap<String, SingleTestFlakyStatsWithRevision>();
    testFlakyStatsWithRevisionMap.put(TEST_ONE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.PASSED));
    testFlakyStatsWithRevisionMap.put(TEST_TWO,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.FLAKED));
    testFlakyStatsWithRevisionMap.put(TEST_THREE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.FAILED));
    testFlakyStatsWithRevisionMap.put(TEST_FOUR,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_ONE, TestState.FAILED));

    FlakyRunStats actionTwoResult = new FlakyRunStats(testFlakyStatsWithRevisionMap);

    testFlakyStatsWithRevisionMap = new HashMap<String, SingleTestFlakyStatsWithRevision>();
    testFlakyStatsWithRevisionMap.put(TEST_ONE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_TWO, TestState.FLAKED));
    testFlakyStatsWithRevisionMap.put(TEST_TWO,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_TWO, TestState.PASSED));
    testFlakyStatsWithRevisionMap.put(TEST_THREE,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_TWO, TestState.FAILED));
    testFlakyStatsWithRevisionMap.put(TEST_FOUR,
        createSingleTestFlakyStatsWithRevision(TOTAL_RUNS, REVISION_TWO, TestState.PASSED));

    FlakyRunStats actionThreeResult = new FlakyRunStats(testFlakyStatsWithRevisionMap);

    actionOne.setFlakyRunStats(actionOneResult);
    actionTwo.setFlakyRunStats(actionTwoResult);
    actionThree.setFlakyRunStats(actionThreeResult);

    List<FlakyTestResultAction> actionList = new ArrayList<FlakyTestResultAction>();

    actionList.add(actionOne);
    actionList.add(actionTwo);
    actionList.add(actionThree);
    return actionList;
  }

  private static enum TestState {
    PASSED, FAILED, FLAKED;
  }

  /**
   * Create a {@link SingleTestFlakyStatsWithRevision} object for testing
   *
   * @param totalRuns number of maximal number of potential runs in this bulid. Assume if a test is
   * flaky, it will fail in all previous totalRuns-1 retries but pass in the last time.
   * @param revision the revision this build was run
   * @param result result of a single test
   * @return a {@link SingleTestFlakyStatsWithRevision} object which contains the revision
   * information and the number of passes/fails for the test
   */
  private static SingleTestFlakyStatsWithRevision createSingleTestFlakyStatsWithRevision(
      int totalRuns,
      String revision, TestState result) {

    SingleTestFlakyStats stats;
    if (result == TestState.PASSED) {
      stats = new SingleTestFlakyStats(1, 0, 0);
    } else if (result == TestState.FAILED) {
      stats = new SingleTestFlakyStats(0, totalRuns, 0);
    } else {
      stats = new SingleTestFlakyStats(1, totalRuns - 1, 0);
    }

    return new SingleTestFlakyStatsWithRevision(stats, revision);
  }

}
