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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.jenkins.flakyTestHandler.plugin.FlakyTestResultAction.FlakyRunStats;
import com.google.jenkins.flakyTestHandler.plugin.deflake.DeflakeCause;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.servlet.ServletException;
import jenkins.triggers.SCMTriggerItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildData;
import hudson.scm.SCM;

/**
 * Action for aggregate and display information for flaky history for all the tests
 *
 * @author Qingzhou Luo
 */
public class HistoryAggregatedFlakyTestResultAction implements Action {

  /**
   * The project which is running
   */
  AbstractProject<?, ?> project;

  /**
   * Map between test name and flaky stats for a single test
   */
  Map<String, SingleTestFlakyStats> aggregatedFlakyStats;

  /**
   * Map between test name and the map between each scm revision and its running stats (# passes + #
   * fails)
   */
  Map<String, Map<String, SingleTestFlakyStats>> aggregatedTestFlakyStatsWithRevision;

  /**
   * The set of all tests being run in last non-deflake build
   */
  Set<String> allTests;

  /**
   * Whether to only show flaky tests or all tests
   */
  boolean onlyShowFlakyTests;

  public HistoryAggregatedFlakyTestResultAction(AbstractProject<?, ?> project) {
    this.project = project;
    this.aggregatedTestFlakyStatsWithRevision = new TreeMap<String, Map<String, SingleTestFlakyStats>>();
    this.aggregatedFlakyStats = new TreeMap<String, SingleTestFlakyStats>();
    this.allTests = new HashSet<String>();
    this.onlyShowFlakyTests = true;
  }

  /**
   * Aggregate all the previous builds to get flaky stats information for all the tests
   */
  void aggregate() {

    // set of all the previous builds
    Stack<AbstractBuild> builds = new Stack<AbstractBuild>();
    for (AbstractBuild<?, ?> build : project._getRuns().values()) {
      builds.push(build);
    }

    while (!builds.empty()) {
      aggregateOneBuild(builds.pop());
    }
  }

  /**
   * Aggregate flaky runs one previous build and put results into a map between test name and
   * its map between scm revisions and aggregated flaky stats for that revision
   *
   * @param build the build to be aggregated
   */
  public void aggregateOneBuild(Run<?, ?> build) {
    FlakyTestResultAction action = build.getAction(FlakyTestResultAction.class);
    if (action == null) {
      return;
    }

    FlakyRunStats runStats = action.getFlakyRunStats();

    if (runStats == null) {
      return;
    }

    Map<String, SingleTestFlakyStatsWithRevision> testFlakyStatsMap = runStats.getTestFlakyStatsWithRevisionMap();

    if (testFlakyStatsMap == null) {
      // Skip old build which doesn't have the map
      return;
    }

    if (build.getCause(DeflakeCause.class) == null) {
      // This is a non-deflake build, update allTests
      allTests = testFlakyStatsMap.keySet();
    }

    for (Map.Entry<String, SingleTestFlakyStatsWithRevision> testFlakyStat : testFlakyStatsMap.entrySet()) {
      String testName = testFlakyStat.getKey();
      String revision = testFlakyStat.getValue().getRevision();
      SingleTestFlakyStats stats = testFlakyStat.getValue().getStats();

      if (aggregatedTestFlakyStatsWithRevision.containsKey(testName)) {
        Map<String, SingleTestFlakyStats> testFlakyStatMap = aggregatedTestFlakyStatsWithRevision.get(testName);

        if (testFlakyStatMap.containsKey(revision)) {
          // Merge flaky stats with the same test and the same revision
          testFlakyStatMap.get(revision).merge(stats);
        } else {
          // First specific revision flaky stat for a given test
          testFlakyStatMap.put(revision, new SingleTestFlakyStats(stats));
        }
      } else {
        // The first test entry
        Map<String, SingleTestFlakyStats> testFlakyStatMap = new LinkedHashMap<String, SingleTestFlakyStats>();
        testFlakyStatMap.put(revision, new SingleTestFlakyStats(stats));
        aggregatedTestFlakyStatsWithRevision.put(testName, testFlakyStatMap);

      }
    }

    aggregatedFlakyStats = Maps.filterKeys(
        Maps.transformValues(aggregatedTestFlakyStatsWithRevision, REVISION_STATS_MAP_TO_AGGREGATED_STATS),
        Predicates.in(allTests));
  }

  public Map<String, Map<String, SingleTestFlakyStats>> getAggregatedTestFlakyStatsWithRevision() {
    return aggregatedTestFlakyStatsWithRevision;
  }

  public boolean getOnlyShowFlakyTests() {
    return onlyShowFlakyTests;
  }

  /**
   * Function to aggregate flaky stats over revisions
   */
  public static final Function<Map<String, SingleTestFlakyStats>, SingleTestFlakyStats> REVISION_STATS_MAP_TO_AGGREGATED_STATS = new Function<Map<String, SingleTestFlakyStats>, SingleTestFlakyStats>() {
    @Override
    public SingleTestFlakyStats apply(Map<String, SingleTestFlakyStats> revisionStatsMap) {
      SingleTestFlakyStats aggregatedStatsOverRevision = new SingleTestFlakyStats(0, 0, 0);

      for (SingleTestFlakyStats singleTestFlakyStats : revisionStatsMap.values()) {
        if (singleTestFlakyStats.isPassed()) {
          aggregatedStatsOverRevision.increasePass();
        } else if (singleTestFlakyStats.isFlaked()) {
          aggregatedStatsOverRevision.increaseFlake();
        } else if (singleTestFlakyStats.isFailed()) {
          aggregatedStatsOverRevision.increaseFail();
        }
      }

      return aggregatedStatsOverRevision;
    }
  };

  public void doShowAll(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
    onlyShowFlakyTests = !onlyShowFlakyTests;
    rsp.sendRedirect("..");
  }

  public Map<String, SingleTestFlakyStats> getAggregatedFlakyStats() {
    return aggregatedFlakyStats;
  }

  /**
   * Get filtered tests to display on the project page. Users can decide whether to show all tests
   * or just flaky tests
   *
   * @return the filtered tests
   */
  public Map<String, SingleTestFlakyStats> getFilteredAggregatedFlakyStats() {
    Predicate<Entry<String, SingleTestFlakyStats>> flakyTestsFilter;
    if (onlyShowFlakyTests) {
      flakyTestsFilter = new Predicate<Entry<String, SingleTestFlakyStats>>() {
        @Override
        public boolean apply(Entry<String, SingleTestFlakyStats> singleTestFlakyStatsEntry) {
          return singleTestFlakyStatsEntry.getValue().getFlake() > 0;
        }
      };
    } else {
      flakyTestsFilter = Predicates.alwaysTrue();
    }
    return Maps.filterEntries(aggregatedFlakyStats, flakyTestsFilter);
  }

  public String getIconFileName() {
    return null;
  }

  public String getDisplayName() {
    return null;
  }

  public String getUrlName() {
    return "historyAggregate";
  }

  /**
   * Class for flaky information for one single test
   */
  public static class SingleTestFlakyStats {

    int flake;

    int pass;

    int fail;

    public int getFlake() {
      return flake;
    }

    public int getPass() {
      return pass;
    }

    public int getFail() {
      return fail;
    }

    public void increasePass() {
      pass++;
    }

    public void increaseFail() {
      fail++;
    }

    public void increaseFlake() {
      flake++;
    }

    public SingleTestFlakyStats(int pass, int fail, int flake) {
      this.pass = pass;
      this.fail = fail;
      this.flake = flake;
    }

    public SingleTestFlakyStats(SingleTestFlakyStats stats) {
      this.pass = stats.pass;
      this.fail = stats.fail;
      this.flake = stats.flake;
    }

    public void merge(SingleTestFlakyStats otherTestStats) {
      this.pass += otherTestStats.pass;
      this.fail += otherTestStats.fail;
      this.flake += otherTestStats.flake;
    }

    public boolean isPassed() {
      return pass > 0 && fail == 0 && flake == 0;
    }

    public boolean isFailed() {
      return fail > 0 && pass == 0 && flake == 0;
    }

    public boolean isFlaked() {
      return (pass > 0 && fail > 0) || flake > 0;
    }

    public boolean isUnknown() {
      return pass == 0 && fail == 0 && flake == 0;
    }
  }

  /**
   * A class which augments {@link SingleTestFlakyStats} with a revision string.
   */
  public static class SingleTestFlakyStatsWithRevision {

    /**
     * Embedded {@link SingleTestFlakyStats} object
     */
    private SingleTestFlakyStats stats;

    /**
     * The revision with this test stats. If using GIT for scm, then it will be the git Shal string;
     * Otherwise it will be the build number.
     */
    private String revision;

    /**
     * Construct a SingleTestFlakyStatsWithRevision object with {@link SingleTestFlakyStats} and
     * build information.
     *
     * @param stats Embedded {@link SingleTestFlakyStats} object
     * @param build The {@link hudson.model.AbstractBuild} object to get SCM information from.
     */
    public SingleTestFlakyStatsWithRevision(SingleTestFlakyStats stats, Run build) {
      this.stats = stats;
      revision = Integer.toString(build.getNumber());

      Job job = build.getParent();
      SCMTriggerItem s = SCMTriggerItem.SCMTriggerItems.asSCMTriggerItem(job);
      if (s != null) {
        ArrayList<SCM> scms = new ArrayList<>(s.getSCMs());
        SCM scm = scms.size() > 0 ? scms.get(0) : null;

        if ("hudson.plugins.git.GitSCM".equalsIgnoreCase(scm.getType())) {
          GitSCM gitSCM = (GitSCM) scm;
          BuildData buildData = gitSCM.getBuildData(build);
          if (buildData != null) {
            Revision gitRevision = buildData.getLastBuiltRevision();
            if (gitRevision != null) {
              revision = gitRevision.getSha1String();
            }
          }
        }
      }
    }

    public SingleTestFlakyStatsWithRevision(SingleTestFlakyStats stats, String revision) {
      this.stats = stats;
      this.revision = revision;
    }

    public String getRevision() {
      return revision;
    }

    public SingleTestFlakyStats getStats() {
      return stats;
    }
  }
}
