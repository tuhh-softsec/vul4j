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

package de.tsystems.mms.apm.performancesignature;

import de.tsystems.mms.apm.performancesignature.model.GenericTestCase;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestData;
import hudson.FilePath;
import hudson.Plugin;
import hudson.init.Initializer;
import hudson.model.AbstractProject;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.IOException;

import static hudson.init.InitMilestone.JOB_LOADED;
import static hudson.init.InitMilestone.PLUGINS_STARTED;

public class PerformanceSignaturePlugin extends Plugin {
    @Initializer(before = PLUGINS_STARTED)
    public static void addAliases() {
        // Moved in 1.6.3
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigTestData", PerfSigTestData.class);
        // Moved in 2.0.0
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigRegisterEnvVars", PerfSigEnvInvisAction.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.GeneralTestCase", GenericTestCase.class);
    }

    @Initializer(after = JOB_LOADED)
    public static void init1() throws IOException, InterruptedException {
        // Check for old dashboard configurations
        for (AbstractProject<?, ?> job : Jenkins.getActiveInstance().getAllItems(AbstractProject.class)) {
            FilePath jobPath = new FilePath(job.getConfigFile().getFile()).getParent();
            if (jobPath == null) continue;
            for (FilePath file : jobPath.list(new RegexFileFilter(".*-config.json"))) {
                file.delete();
            }
        }
    }
}
