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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.jenkins.flakyTestHandler.plugin.FlakyTestResultAction;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.ServletException;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BooleanParameterValue;
import hudson.model.CauseAction;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import jenkins.model.Jenkins;

/**
 * Deflake action used to configure and trigger deflake build
 *
 * @author Qingzhou Luo
 */
public class DeflakeAction implements Action {

  private static final String DEFLAKE_CONFIG_URL = "deflakeConfig";

  private static final String RERUN_FAILING_TESTS_COUNT_PARAM = "rerunFailingTestsCountParam";

  private static final String MAVEN_TEST_PARAM = "testParam";

  private static final String MAVEN_TEST = "test";

  private static final String COMMA = ",";

  private static final String SHARP = "#";

  private static final String PLUS = "+";

  private static final Function<Map.Entry<String, Set<String>>, String>
      CLASS_METHOD_MAP_TO_MAVEN_TESTS_LIST = new Function<Entry<String, Set<String>>, String>() {

    @Override
    public String apply(Entry<String, Set<String>> entry) {
      return entry.getKey() + SHARP + Joiner.on(PLUS).join(entry.getValue());
    }
  };

  private final Map<String, Set<String>> failingClassMethodMap;

  public DeflakeAction(Map<String, Set<String>> failingClassMethodMap) {
    this.failingClassMethodMap = failingClassMethodMap;
  }

  @Override
  public String getIconFileName() {
    return "clock.gif";
  }

  @Override
  public String getDisplayName() {
    return "Deflake this build";
  }

  @Override
  public String getUrlName() {
    return "deflake";
  }

  /**
   * Handles the rebuild request and redirects to deflake config page
   *
   * @param request StaplerRequest the request.
   * @param response StaplerResponse the response handler.
   * @throws java.io.IOException in case of Stapler issues
   * @throws javax.servlet.ServletException if something unfortunate happens.
   * @throws InterruptedException if something unfortunate happens.
   */
  public void doIndex(StaplerRequest request, StaplerResponse response) throws
      IOException, ServletException, InterruptedException {
    AbstractBuild currentBuild = request.findAncestorObject(AbstractBuild.class);
    if (currentBuild != null) {

      AbstractProject project = currentBuild.getProject();
      if (project == null) {
        return;
      }
      project.checkPermission(AbstractProject.BUILD);
      response.sendRedirect(DEFLAKE_CONFIG_URL);
    }
  }

  /**
   * Get parameters from submitted form and submit deflake request
   */
  public void doSubmitDeflakeRequest(StaplerRequest request, StaplerResponse response) throws
      IOException, ServletException, InterruptedException {

    AbstractBuild currentBuild = request.findAncestorObject(AbstractBuild.class);
    if (currentBuild != null) {
      AbstractProject project = currentBuild.getProject();
      if (project == null) {
        response.sendRedirect("../../");
        return;
      }
      project.checkPermission(AbstractProject.BUILD);
      List<Action> actions = constructDeflakeCause(currentBuild);

      JSONObject formData = request.getSubmittedForm();
      List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
      parameterValues.add(getStringParam(formData, RERUN_FAILING_TESTS_COUNT_PARAM));

      JSONObject paramObj = JSONObject.fromObject(formData.get(MAVEN_TEST_PARAM));
      boolean onlyRunFailingTests = paramObj.getBoolean("value");
      if (onlyRunFailingTests) {
        String testParameter = generateMavenTestParams();
        if (testParameter != null) {
          parameterValues.add(new StringParameterValue(MAVEN_TEST, testParameter));
        }
      }

      ParametersAction originalParamAction = currentBuild.getAction(ParametersAction.class);
      if (originalParamAction == null) {
        originalParamAction = new ParametersAction();
      }
      actions.add(originalParamAction.createUpdated(parameterValues));
      Jenkins.getInstance().getQueue().schedule2(currentBuild.getProject(), 0,
          actions);
    }

    response.sendRedirect("../../");
  }

  /**
   * Generate maven test parameters to run all the failed tests
   *
   * @return  a string in the format of testClass1#testMethod1+testMethod2,testClass2#testMethod3,
   * ...
   */
  String generateMavenTestParams() {
    return Joiner.on(COMMA).join(Iterables.transform(failingClassMethodMap.entrySet(),
        CLASS_METHOD_MAP_TO_MAVEN_TESTS_LIST));
  }

  /**
   * Construct a list of actions which contain deflake cause and the original failed build
   *
   * @param up upstream build
   * @return list with all original causes and a {@link hudson.model.Cause.UserIdCause} and a {@link
   * com.google.jenkins.flakyTestHandler.plugin.deflake.DeflakeCause}.
   */
  private static List<Action> constructDeflakeCause(AbstractBuild up) {
    List<Action> actions = new ArrayList<Action>();
    actions.add(new CauseAction(new DeflakeCause(up)));
    return actions;
  }

  private static ParameterValue getBooleanParam(JSONObject formData, String paramName) {
    JSONObject paramObj = JSONObject.fromObject(formData.get(paramName));
    String name = paramObj.getString("name");
    FlakyTestResultAction.logger.log(Level.FINE,
        "Param: " + name + " with value: " + paramObj.getBoolean("value"));
    return new BooleanParameterValue(name, paramObj.getBoolean("value"));
  }

  private static ParameterValue getStringParam(JSONObject formData, String paramName) {
    JSONObject paramObj = JSONObject.fromObject(formData.get(paramName));
    String name = paramObj.getString("name");
    FlakyTestResultAction.logger.log(Level.FINE,
        "Param: " + name + " with value: " + paramObj.getString("value"));
    return new StringParameterValue(name, paramObj.getString("value"));
  }
}
