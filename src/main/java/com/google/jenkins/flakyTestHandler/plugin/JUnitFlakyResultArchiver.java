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

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

/**
 * Record flaky information to get flaky stats for all the tests
 *
 * @author Qingzhou Luo
 */
public class JUnitFlakyResultArchiver extends Recorder implements
    Serializable {

  @DataBoundConstructor
  public JUnitFlakyResultArchiver() {

  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.NONE;
  }

  /**
   * Get the action to display aggregated flaky information for all the tests for the given project
   *
   * @param project the project that is running
   * @return the action to display aggregated flaky information for all the tests
   */
  @Override
  public Collection<Action> getProjectActions(AbstractProject<?, ?> project) {
    Collection<Action> actions = new ArrayList<Action>();
    if (!project.isBuilding()) {
      HistoryAggregatedFlakyTestResultAction action = new HistoryAggregatedFlakyTestResultAction(
          project);
      action.aggregate();

      TestFlakyStatsOverRevision testFlakyStatsOverRevision = new TestFlakyStatsOverRevision(project, action);
      actions.add(testFlakyStatsOverRevision);
      actions.add(action);
    }
    return actions;
  }

  /**
   * Perform collecting of flaky stats for the current run
   *
   * @param build this build
   * @param launcher launcher of this build
   * @param listener listener of this build
   * @return true so the build can continue
   */
  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    FlakyTestResultAction flakyStatCollectionAction = new FlakyTestResultAction(build, launcher, listener);
    build.addAction(flakyStatCollectionAction);
    if (flakyStatCollectionAction.getFlakyRunStats().isFlaked()) {
      build.setResult(Result.UNSTABLE);
    }

    return true;
  }

  @Extension
  public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    @Override
    public String getDisplayName() {
      return "Publish JUnit flaky stats";
    }

    @Override
    public Publisher
    newInstance(StaplerRequest req, JSONObject formData)
        throws hudson.model.Descriptor.FormException {
      return new JUnitFlakyResultArchiver();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }
  }
}
