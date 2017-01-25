/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentContributor;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Extension
public class PerfSigEnvContributor extends EnvironmentContributor {
    static final String TESTRUN_ID_KEY = "DYNATRACE_TESTRUN_ID";
    static final String SESSIONCOUNT = "DYNATRACE_SESSIONCOUNT";

    @Override
    public void buildEnvironmentFor(@Nonnull final Run r, @Nonnull final EnvVars envs, @Nonnull final TaskListener listener)
            throws IOException, InterruptedException {

        List<PerfSigEnvInvisAction> envActions = r.getActions(PerfSigEnvInvisAction.class);
        if (envActions.isEmpty()) {
            return;
        }

        envs.put(SESSIONCOUNT, String.valueOf(envActions.size()));
        int i = 1;
        for (PerfSigEnvInvisAction action : envActions) {
            if (StringUtils.isNotBlank(action.getTestRunID())) {
                envs.put(TESTRUN_ID_KEY + i++, action.getTestRunID());
            }
        }
    }
}
