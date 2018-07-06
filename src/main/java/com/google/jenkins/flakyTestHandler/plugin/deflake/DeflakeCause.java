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

import hudson.model.Cause;
import hudson.model.Run;

/**
 * Represents the cause of a deflake build. It is used to communicate with other build actions.
 *
 * @author Qingzhou Luo
 */
public class DeflakeCause extends Cause.UpstreamCause {

  /**
   * DeflakeCause constructor.
   * @param up upstream failing build which is being deflaked
   */
  public DeflakeCause(Run<?, ?> up) {
    super(up);
  }

  @Override
  public String getShortDescription() {
    return ("Deflake Build #" + getUpstreamBuild());
  }

}
