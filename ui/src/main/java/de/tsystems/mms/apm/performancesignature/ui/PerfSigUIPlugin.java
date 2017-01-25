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

package de.tsystems.mms.apm.performancesignature.ui;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestData;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestDataWrapper;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.Plugin;
import hudson.init.Initializer;
import hudson.model.Job;
import hudson.model.Run;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.IOException;
import java.util.List;

import static hudson.init.InitMilestone.JOB_LOADED;
import static hudson.init.InitMilestone.PLUGINS_STARTED;

public class PerfSigUIPlugin extends Plugin {

    @Initializer(before = PLUGINS_STARTED)
    public static void addAliases() {
        // Moved in 1.6.3
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigTestData", PerfSigTestData.class);
        // Moved in 2.2.0
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet", ChartDashlet.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport", DashboardReport.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentChart", IncidentChart.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentViolation", IncidentViolation.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure", Measure.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement", Measurement.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.TestResult", TestResult.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.TestResultStatus", TestResultStatus.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun", TestRun.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRunMeasure", TestRunMeasure.class);

        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.PerfSigTestData", PerfSigTestData.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigTestDataWrapper", PerfSigTestDataWrapper.class);

        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigBuildAction", PerfSigBuildAction.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigTestAction", PerfSigTestAction.class);
    }

    @Initializer(after = JOB_LOADED)
    public static void init1() throws IOException, InterruptedException {
        // Check for old dashboard configurations
        for (Job<?, ?> job : PerfSigUIUtils.getInstance().getAllItems(Job.class)) {
            FilePath jobPath = new FilePath(job.getConfigFile().getFile()).getParent();
            if (jobPath == null) {
                continue;
            }
            List<FilePath> files = jobPath.list(new RegexFileFilter(".*-config.json"));
            files.addAll(jobPath.list(new RegexFileFilter("gridconfig.*.json")));
            for (FilePath file : files) {
                file.delete();
            }
        }
    }
}
