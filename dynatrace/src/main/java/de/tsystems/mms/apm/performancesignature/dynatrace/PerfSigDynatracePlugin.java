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

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.*;
import hudson.Plugin;
import hudson.init.Initializer;
import hudson.model.Items;
import hudson.model.Run;

import static hudson.init.InitMilestone.PLUGINS_STARTED;

@SuppressWarnings("unused")
public class PerfSigDynatracePlugin extends Plugin {

    @Initializer(before = PLUGINS_STARTED)
    public static void addAliases() {
        // Moved in 2.2.0
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.GeneralTestCase", GenericTestCase.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.GenericTestCase", GenericTestCase.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase", ConfigurationTestCase.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.CredProfilePair", CredProfilePair.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.CustomProxy", CustomProxy.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.Dashboard", Dashboard.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.DynatraceServerConfiguration", DynatraceServerConfiguration.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.UnitTestCase", UnitTestCase.class);

        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigActivateConfiguration", PerfSigActivateConfiguration.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigRegisterEnvVars", PerfSigEnvInvisAction.class);
        Run.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigEnvInvisAction", PerfSigEnvInvisAction.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigMemoryDump", PerfSigMemoryDump.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigRecorder", PerfSigRecorder.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigStartRecording", PerfSigStartRecording.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigStopRecording", PerfSigStopRecording.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigTestDataPublisher", PerfSigTestDataPublisher.class);
        Items.XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.PerfSigThreadDump", PerfSigThreadDump.class);
    }
}
