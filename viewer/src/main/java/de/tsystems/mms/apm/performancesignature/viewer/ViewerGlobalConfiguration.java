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

package de.tsystems.mms.apm.performancesignature.viewer;

import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

@Extension
public class ViewerGlobalConfiguration extends GlobalConfiguration {
    private List<JenkinsServerConfiguration> configurations = new ArrayList<JenkinsServerConfiguration>();

    public ViewerGlobalConfiguration() {
        load();
    }

    public static ViewerGlobalConfiguration get() {
        return GlobalConfiguration.all().get(ViewerGlobalConfiguration.class);
    }

    @Override
    public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
        setConfigurations(req.bindJSONToList(JenkinsServerConfiguration.class, formData.get("configurations")));
        return false;
    }

    public List<JenkinsServerConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(final List<JenkinsServerConfiguration> configurations) {
        this.configurations = configurations;
        save();
    }
}
