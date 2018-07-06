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
package com.google.jenkins.flakyTestHandler.plugin.deflake;

import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildChooser;
import hudson.plugins.git.util.BuildChooserContext;
import hudson.plugins.git.util.BuildChooserDescriptor;
import hudson.plugins.git.util.BuildData;
import hudson.plugins.git.util.DefaultBuildChooser;
import hudson.remoting.VirtualChannel;

/**
 * For a failing build, restore the source code status to the one it failed on and then re-run
 * failing tests. This will help us get precise idea whether a test is flaky or not.
 *
 * @author Qingzhou Luo
 */
public class DeflakeGitBuildChooser extends BuildChooser {

  /**
   * The default git checkout strategy
   */
  private final DefaultBuildChooser defaultBuildChooser = new DefaultBuildChooser();

  @DataBoundConstructor
  public DeflakeGitBuildChooser() {
  }

  /**
   * Get failing build revision if this is a deflake build, otherwise use the default build chooser
   */
  @Override
  public Collection<Revision> getCandidateRevisions(boolean isPollCall, String singleBranch, GitClient git,
      TaskListener listener, BuildData buildData, BuildChooserContext context)
      throws GitException, IOException, InterruptedException {

    // Not sure why it cannot be inferred and we have to put cast here
    DeflakeCause cause = (DeflakeCause) context.getBuild().getCause(DeflakeCause.class);

    if (cause != null) {
      BuildData gitBuildData = gitSCM.getBuildData(cause.getUpstreamRun(), true);
      Revision revision = gitBuildData.getLastBuiltRevision();
      if (revision != null) {
        return Collections.singletonList(revision);
      }
    }

    // If it is not a deflake run, then use the default git checkout strategy
    defaultBuildChooser.gitSCM = this.gitSCM;
    return defaultBuildChooser.getCandidateRevisions(isPollCall, singleBranch, git, listener, buildData, context);
  }

  @Extension
  public static class DescriptorImpl extends BuildChooserDescriptor {

    @Override
    public String getDisplayName() {
      return "Checkout failing revision when deflaking";
    }

  }

  /**
   * Retrieve build
   */
  private static class GetBuild implements BuildChooserContext.ContextCallable<AbstractBuild<?, ?>, AbstractBuild> {

    @Override
    public AbstractBuild invoke(AbstractBuild<?, ?> build, VirtualChannel channel) {
      return build;
    }
  }
}
