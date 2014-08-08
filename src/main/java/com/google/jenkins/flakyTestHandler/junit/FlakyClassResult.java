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
package com.google.jenkins.flakyTestHandler.junit;

import com.google.jenkins.flakyTestHandler.plugin.JUnitFlakyAggregatedTestDataAction;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.tasks.junit.Messages;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestNameTransformer;
import hudson.tasks.test.TabulatedResult;
import hudson.tasks.test.TestObject;
import hudson.tasks.test.TestResult;

/**
 * Cumulative test result of a test class augmented with flaky information. Majority of code copied
 * from hudson.tasks.junit.ClassResult
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/
 * ClassResult.java
 *
 * @author Qingzhou Luo
 */
public final class FlakyClassResult extends TabulatedResult implements
    Comparable<FlakyClassResult> , ActionableFlakyTestObject {

  private final String className; // simple name
  private transient String safeName;

  private final List<FlakyCaseResult> cases = new ArrayList<FlakyCaseResult>();

  private int passCount, failCount, skipCount, flakeCount;

  private float duration;

  private final FlakyPackageResult parent;

  FlakyClassResult(FlakyPackageResult parent, String className) {
    this.parent = parent;
    this.className = className;
  }

  @Override
  public AbstractBuild<?, ?> getOwner() {
    return (parent == null ? null : parent.getOwner());
  }

  public FlakyPackageResult getParent() {
    return parent;
  }

  @Override
  public FlakyClassResult getPreviousResult() {
    if (parent == null) {
      return null;
    }
    TestResult pr = parent.getPreviousResult();
    if (pr == null) {
      return null;
    }
    if (pr instanceof FlakyPackageResult) {
      return ((FlakyPackageResult) pr).getClassResult(getName());
    }
    return null;
  }

  @Override
  public hudson.tasks.test.TestResult findCorrespondingResult(String id) {
    String myID = safe(getName());
    String caseName = id;
    int base = id.indexOf(myID);
    if (base > 0) {
      int caseNameStart = base + myID.length() + 1;
      if (id.length() > caseNameStart) {
        caseName = id.substring(caseNameStart);
      }
    }
    FlakyCaseResult child = getCaseResult(caseName);
    if (child != null) {
      return child;
    }
    return null;
  }

  public String getTitle() {
    return Messages.ClassResult_getTitle(getDisplayName());
  }

  @Override
  public String getChildTitle() {
    return "Class Reults";
  }

  @Exported(visibility = 999)
  public String getName() {
    int idx = className.lastIndexOf('.');
    if (idx < 0) {
      return className;
    } else {
      return className.substring(idx + 1);
    }
  }

  public
  @Override
  synchronized String getSafeName() {
    if (safeName != null) {
      return safeName;
    }
    return safeName = uniquifyName(parent.getChildren(), safe(getName()));
  }

  public FlakyCaseResult getCaseResult(String name) {
    for (FlakyCaseResult c : cases) {
      if (c.getSafeName().equals(name)) {
        return c;
      }
    }
    return null;
  }

  @Override
  public Object getDynamic(String name, StaplerRequest req, StaplerResponse rsp) {
    FlakyCaseResult c = getCaseResult(name);
    if (c != null) {
      return c;
    } else {
      return super.getDynamic(name, req, rsp);
    }
  }


  @Exported(name = "child")
  public List<FlakyCaseResult> getChildren() {
    return cases;
  }

  public boolean hasChildren() {
    return ((cases != null) && (cases.size() > 0));
  }

  @Exported
  public float getDuration() {
    return duration;
  }

  @Exported
  public int getPassCount() {
    return passCount;
  }

  @Exported
  public int getFailCount() {
    return failCount;
  }

  @Exported
  public int getSkipCount() {
    return skipCount;
  }

  @Exported
  public int getFlakeCount() {
    return flakeCount;
  }

  public void add(FlakyCaseResult r) {
    cases.add(r);
  }

  /**
   * Recount my children.
   */
  @Override
  public void tally() {
    passCount = failCount = skipCount = flakeCount = 0;
    duration = 0;
    for (FlakyCaseResult r : cases) {
      r.setClass(this);
      if (r.isSkipped()) {
        skipCount++;
      } else if (r.isFlaked()) {
        flakeCount++;
      } else if (r.isPassed()) {
        passCount++;
      } else {
        failCount++;
      }
      duration += r.getDuration();
    }
  }


  void freeze() {
    passCount = failCount = skipCount = flakeCount = 0;
    duration = 0;
    for (FlakyCaseResult r : cases) {
      r.setClass(this);
      if (r.isSkipped()) {
        skipCount++;
      } else if (r.isFlaked()) {
        flakeCount++;
      } else if (r.isPassed()) {
        passCount++;
      } else {
        failCount++;
      }
      duration += r.getDuration();
    }
    Collections.sort(cases);
  }

  public String getClassName() {
    return className;
  }

  public int compareTo(FlakyClassResult that) {
    return this.className.compareTo(that.className);
  }

  public String getDisplayName() {
    return TestNameTransformer.getTransformedName(getName());
  }

  /**
   * @since 1.515
   */
  public String getFullName() {
    return getParent().getName() + "." + className;
  }

  public String getFullDisplayName() {
    return getParent().getDisplayName() + "." + TestNameTransformer.getTransformedName(className);
  }

  /**
   * Gets the relative path to this test case from the given object.
   */
  @Override
  public String getRelativePathFrom(TestObject it) {
    if (it instanceof FlakyCaseResult) {
      return "..";
    } else {
      return super.getRelativePathFrom(it);
    }
  }

  @Override
  public TestAction getTestAction() {
    return new JUnitFlakyAggregatedTestDataAction(getPassCount(),
        getFailCount(), getFlakeCount());
  }
}
