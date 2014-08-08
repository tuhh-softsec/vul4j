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

import hudson.tasks.junit.TestAction;

/**
 * Action to aggregate flaky information for a single test class
 *
 * @author Qingzhou Luo
 */
public class JUnitFlakyAggregatedTestDataAction extends TestAction {

  private int passed, failed, flaked;

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

  public JUnitFlakyAggregatedTestDataAction(int passed, int failed, int flaked) {
    this.passed = passed;
    this.failed = failed;
    this.flaked = flaked;
  }

  public int getPassed() {
    return passed;
  }

  public int getFailed() {
    return failed;
  }

  public int getFlaked() {
    return flaked;
  }

  /**
   * Get the path of the image to display for the test class
   *
   * @return successful image if no fail or flake, otherwise failure images
   */
  public String getImagePath() {
    if (failed == 0 && flaked == 0) {
      return JUnitFlakyTestDataAction.getSmallImagePath(100);
    } else if (failed == 0 && flaked > 0) {
      return JUnitFlakyTestDataAction.getSmallImagePath(80);
    } else {
      return JUnitFlakyTestDataAction.getSmallImagePath(0);
    }
  }
}
