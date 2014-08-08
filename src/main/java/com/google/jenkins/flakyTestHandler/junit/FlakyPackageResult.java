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

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hudson.model.AbstractBuild;
import hudson.tasks.junit.Messages;
import hudson.tasks.junit.TestNameTransformer;
import hudson.tasks.test.MetaTabulatedResult;
import hudson.tasks.test.TestResult;

/**
 * Cumulative test result for a package augmented with flaky information.
 * Majority of code copied from hudson.tasks.junit.PackageResult
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/
 * PackageResult.java
 *
 * @author Qingzhou Luo
 */
public final class FlakyPackageResult extends MetaTabulatedResult implements Comparable<FlakyPackageResult> {

  private final String packageName;
  private transient String safeName;
  /**
   * All {@link FlakyClassResult}s keyed by their short name.
   */
  private final Map<String,FlakyClassResult> classes = new TreeMap<String,FlakyClassResult>();
  private int passCount,failCount,skipCount,flakeCount;
  private final FlakyTestResult parent;
  private float duration;

  FlakyPackageResult(FlakyTestResult parent, String packageName) {
    this.packageName = packageName;
    this.parent = parent;
  }

  @Override
  public AbstractBuild<?, ?> getOwner() {
    return (parent == null ? null : parent.getOwner());
  }

  public FlakyTestResult getParent() {
    return parent;
  }

  @Exported(visibility=999)
  public String getName() {
    return packageName;
  }

  @Override
  public synchronized String getSafeName() {
    if (safeName != null) {
      return safeName;
    }
    Collection<FlakyPackageResult> siblings = (parent == null ? Collections.EMPTY_LIST : parent.getChildren());
    return safeName = uniquifyName(
        siblings,
        safe(getName()));
  }

  @Override
  public TestResult findCorrespondingResult(String id) {
    String myID = safe(getName());

    int base = id.indexOf(myID);
    String className = id; // fall back value
    if (base > 0) {
      int classNameStart = base + myID.length() + 1;
      if (classNameStart<id.length())
        className = id.substring(classNameStart);
    }

    String subId = null;
    int classNameEnd = className.indexOf('/');
    if (classNameEnd > 0) {
      subId = className.substring(classNameEnd + 1);
      if (subId.length() == 0) {
        subId = null;
      }
      className = className.substring(0, classNameEnd);
    }

    FlakyClassResult child = getClassResult(className);
    if (child != null && subId != null)
      return child.findCorrespondingResult(subId);

    return child;
  }

  @Override
  public String getTitle() {
    return Messages.PackageResult_getTitle(getDisplayName());
  }

  @Override
  public String getChildTitle() {
    return Messages.PackageResult_getChildTitle();
  }

  @Override
  public float getDuration() {
    return duration;
  }

  @Exported
  @Override
  public int getPassCount() {
    return passCount;
  }

  @Exported
  @Override
  public int getFailCount() {
    return failCount;
  }

  @Exported
  @Override
  public int getSkipCount() {
    return skipCount;
  }

  @Exported
  public int getFlakeCount() {
    return flakeCount;
  }

  @Override
  public int getTotalCount() {
    return passCount + failCount + skipCount + flakeCount;
  }


  @Override
  public Object getDynamic(String name, StaplerRequest req, StaplerResponse rsp) {
    FlakyClassResult result = getClassResult(name);
    if (result != null) {
      return result;
    } else {
      return super.getDynamic(name, req, rsp);
    }
  }

  public FlakyClassResult getClassResult(String name) {
    return classes.get(name);
  }

  @Exported(name="child")
  public Collection<FlakyClassResult> getChildren() {
    return classes.values();
  }

  /**
   * Whether this test result has children.
   */
  @Override
  public boolean hasChildren() {
    int totalTests = passCount + failCount + skipCount + flakeCount;
    return (totalTests != 0);
  }

  /**
   * Returns a list of the failed cases, in no particular
   * sort order
   */
  public List<FlakyCaseResult> getFailedTests() {
    List<FlakyCaseResult> r = new ArrayList<FlakyCaseResult>();
    for (FlakyClassResult clr : classes.values()) {
      for (FlakyCaseResult cr : clr.getChildren()) {
        if (cr.isFailed()) {
          r.add(cr);
        }
      }
    }
    return r;
  }

  /**
   * Gets the "children" of this test result that passed without a flake
   *
   * @return the children of this test result, if any, or an empty collection
   */
  @Override
  public Collection<? extends hudson.tasks.test.TestResult> getPassedTests() {
    List<FlakyCaseResult> r = new ArrayList<FlakyCaseResult>();
    for (FlakyClassResult clr : classes.values()) {
      for (FlakyCaseResult cr : clr.getChildren()) {
        if (cr.isPassed() && !cr.isFlaked()) {
          r.add(cr);
        }
      }
    }
    return r;
  }

  /**
   * Gets the "children" of this test result that were skipped
   *
   * @return the children of this test result, if any, or an empty list
   */
  @Override
  public Collection<? extends TestResult> getSkippedTests() {
    List<FlakyCaseResult> r = new ArrayList<FlakyCaseResult>();
    for (FlakyClassResult clr : classes.values()) {
      for (FlakyCaseResult cr : clr.getChildren()) {
        if (cr.isSkipped()) {
          r.add(cr);
        }
      }
    }
    return r;
  }

  /**
   * Gets the "children" of this test result that were flaky
   *
   * @return the children of this test result, if any, or an empty list
   */
  public List<FlakyCaseResult> getFlakyTests() {
    List<FlakyCaseResult> r = new ArrayList<FlakyCaseResult>();
    for (FlakyClassResult clr : classes.values()) {
      for (FlakyCaseResult cr : clr.getChildren()) {
        if (cr.isFlaked()) {
          r.add(cr);
        }
      }
    }
    return r;
  }

  /**
   * @return true if every test was not skipped and every test did not fail, false otherwise.
   */
  @Override
  public boolean isPassed() {
    return (failCount == 0 && skipCount == 0);
  }

  void add(FlakyCaseResult r) {
    String n = r.getSimpleName(), sn = safe(n);
    FlakyClassResult c = getClassResult(sn);
    if (c == null) {
      classes.put(sn, c = new FlakyClassResult(this, n));
    }
    c.add(r);
    duration += r.getDuration();
  }

  /**
   * Recount my children
   */
  @Override
  public void tally() {
    passCount = 0;
    failCount = 0;
    skipCount = 0;
    flakeCount = 0;
    duration = 0;

    for (FlakyClassResult cr : classes.values()) {
      cr.tally();
      passCount += cr.getPassCount();
      failCount += cr.getFailCount();
      skipCount += cr.getSkipCount();
      flakeCount += cr.getFlakeCount();
      duration += cr.getDuration();
    }
  }

  void freeze() {
    passCount = failCount = skipCount = flakeCount = 0;
    for (FlakyClassResult cr : classes.values()) {
      cr.freeze();
      passCount += cr.getPassCount();
      failCount += cr.getFailCount();
      flakeCount += cr.getFlakeCount();
      skipCount += cr.getSkipCount();
    }
  }

  public int compareTo(FlakyPackageResult that) {
    return this.packageName.compareTo(that.packageName);
  }

  public String getDisplayName() {
    return TestNameTransformer.getTransformedName(packageName);
  }
}
