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

import com.google.jenkins.flakyTestHandler.junit.FlakyTestResult;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.AbstractTestResultAction;

/**
 * Publisher for publishing rerun information
 *
 * @author Qingzhou Luo
 */
public class JUnitFlakyTestDataPublisher
    extends TestDataPublisher {

  @DataBoundConstructor
  public JUnitFlakyTestDataPublisher() {
  }

  @Override
  public TestResultAction.Data getTestData(AbstractBuild<?, ?> abstractBuild, Launcher launcher,
      BuildListener buildListener, TestResult testResult)
      throws IOException, InterruptedException {
    FlakyTestResult flakyTestResult = new FlakyTestResult(testResult);
    // TODO consider the possibility that there is >1 such action
    flakyTestResult.freeze(abstractBuild.getAction(AbstractTestResultAction.class), abstractBuild);
    return new JUnitFlakyTestData(flakyTestResult);
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }


  @Extension
  public static class DescriptorImpl extends Descriptor<TestDataPublisher> {

    @Override
    public String getDisplayName() {
      return "Publish JUnit flaky tests reports";
    }
  }
}

