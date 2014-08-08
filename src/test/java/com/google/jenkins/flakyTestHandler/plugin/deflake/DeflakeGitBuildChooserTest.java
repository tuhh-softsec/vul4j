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

import com.google.jenkins.flakyTestHandler.plugin.deflake.DeflakeActionIntegrationTest.FailingTestResultAction;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Cause.UserCause;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.git.AbstractGitTestCase;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;

/**
 * Test {@link DeflakeGitBuildChooser} will choose previous failing build
 *
 * @author Qingzhou Luo
 */
public class DeflakeGitBuildChooserTest extends AbstractGitTestCase {

  final String commitFile1 = "commitFile1";
  final String commitFile2 = "commitFile2";

  @Test
  public void testDeflakeCheckoutFailingRevision() throws Exception {
    FreeStyleProject project = setupProject(Arrays.asList(
        new BranchSpec("master")
    ));

    DeflakeCause deflakeCause = initRepoWithDeflakeBuild(project);

    // Checkout without deflake cause will see the newly committed file
    build(project, Result.SUCCESS, commitFile2);

    // Checkout with deflake cause will only see the first file
    AbstractBuild deflakeBuild = project.scheduleBuild2(0, deflakeCause,
        new FailingTestResultAction()).get();
    assertTrue("could not see the old committed file",
        deflakeBuild.getWorkspace().child(commitFile1).exists());
    assertFalse("should not see the newly committed file",
        deflakeBuild.getWorkspace().child(commitFile2).exists());

  }

  private DeflakeCause initRepoWithDeflakeBuild(FreeStyleProject project)
      throws InterruptedException, java.util.concurrent.ExecutionException {
    commit(commitFile1, johnDoe, "Commit number 1");

    // First failing build
    AbstractBuild build = project.scheduleBuild2(0, new UserCause(), new FailingTestResultAction())
        .get();

    // Set up deflake cause of previous failing build
    DeflakeCause deflakeCause = new DeflakeCause(build);

    // Make another commit after failing build
    commit(commitFile2, janeDoe, "Commit number 2");
    return deflakeCause;
  }

  private FreeStyleProject setupProject(List<BranchSpec> specs) throws Exception {
    FreeStyleProject project = setupProject(specs, false, null, null, null, null, false, null);
    assertNotNull("could not init project", project);

    // Use DeflakeGitBuildChooser
    DeflakeGitBuildChooser chooser = new DeflakeGitBuildChooser();
    chooser.gitSCM = (GitSCM) project.getScm();
    chooser.gitSCM.setBuildChooser(chooser);

    return project;
  }
}