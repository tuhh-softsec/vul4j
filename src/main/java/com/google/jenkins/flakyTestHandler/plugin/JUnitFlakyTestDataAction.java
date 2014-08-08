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

import com.google.jenkins.flakyTestHandler.junit.FlakyCaseResult.FlakyRunInformation;

import org.jvnet.localizer.Localizable;

import java.util.List;

import hudson.model.HealthReport;
import hudson.tasks.junit.TestAction;

/**
 * Action to display rerun information (message, stackTrace, stdout/stderr)
 *
 * @author Qingzhou Luo
 */
public class JUnitFlakyTestDataAction extends TestAction {

  /**
   * List of all the reruns
   */
  private List<FlakyRunInformation> flakyRuns;

  /**
   * Whether this test failed
   */
  private boolean isFailed;

  public JUnitFlakyTestDataAction(List<FlakyRunInformation> flakyRuns, boolean isFailed) {
    this.flakyRuns = flakyRuns;
    this.isFailed = isFailed;
  }

  /**
   * Returns text with annotations.
   */
  @Override
  public String annotate(String text) {
    return text;
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

  public List<FlakyRunInformation> getFlakyRuns() { return flakyRuns; }

  public boolean getContainsFlakyRuns() {
    return flakyRuns.size() > 0;
  }

  public boolean getIsFailed() {
    return isFailed;
  }

  public boolean getIsPassed() {
    return !isFailed && flakyRuns.size() == 0;
  }

  public boolean getIsFlaked() {
    return !isFailed && flakyRuns.size() > 0;
  }

  public static String getBigImagePath(int score) {
    HealthReport healthReport = new HealthReport(score, (Localizable)null);
    return healthReport.getIconUrl("32x32");
  }

  public static String getSmallImagePath(int score) {
    HealthReport healthReport = new HealthReport(score, (Localizable)null);
    return healthReport.getIconUrl("16x16");
  }

}
