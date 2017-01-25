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

package de.tsystems.mms.apm.performancesignature.viewer.util;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.Messages;
import de.tsystems.mms.apm.performancesignature.viewer.ViewerGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.JenkinsServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import hudson.AbortException;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public final class ViewerUtils {
    private ViewerUtils() {
    }

    public static ListBoxModel listToListBoxModel(final List<?> arrayList) {
        final ListBoxModel listBoxModel = new ListBoxModel();
        for (Object item : arrayList) {
            if (item instanceof String)
                listBoxModel.add((String) item);
            else if (item instanceof JenkinsServerConfiguration) {
                JenkinsServerConfiguration conf = (JenkinsServerConfiguration) item;
                if (CollectionUtils.isNotEmpty(conf.getCredJobPairs()))
                    for (CredJobPair credJobPair : conf.getCredJobPairs()) {
                        String listItem = credJobPair.getJenkinsJob() + " (" + credJobPair.getCredentials().getUsername() + ") @ " +
                                conf.getName();
                        listBoxModel.add(listItem);
                    }
            }
        }
        return listBoxModel;
    }

    public static List<JenkinsServerConfiguration> getJenkinsConfigurations() {
        return ViewerGlobalConfiguration.get().getConfigurations();
    }

    public static JenkinsServerConfiguration getServerConfiguration(final String jenkinsServer) {
        for (JenkinsServerConfiguration serverConfiguration : getJenkinsConfigurations()) {
            String strippedName = jenkinsServer.replaceAll(".*@", "").trim();
            if (strippedName.equals(serverConfiguration.getName())) {
                return serverConfiguration;
            }
        }
        return null;
    }

    //duplicated method @PerfSigUtils
    public static UsernamePasswordCredentials getCredentials(final String credsId) {
        return (credsId == null) ? null : CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentials(UsernamePasswordCredentials.class, PerfSigUIUtils.getInstance(), ACL.SYSTEM,
                        Collections.<DomainRequirement>emptyList()), CredentialsMatchers.withId(credsId));
    }

    public static JenkinsServerConnection createJenkinsServerConnection(final String jenkinsJob) throws AbortException, RESTErrorException {
        JenkinsServerConfiguration serverConfiguration = ViewerUtils.getServerConfiguration(jenkinsJob);
        if (serverConfiguration == null) {
            throw new AbortException(Messages.ViewerRecorder_FailedToLookupServer());
        }

        CredJobPair pair = serverConfiguration.getCredJobPair(jenkinsJob);
        if (pair == null) {
            throw new AbortException(Messages.ViewerRecorder_FailedToLookupJob());
        }

        JenkinsServerConnection serverConnection = new JenkinsServerConnection(serverConfiguration, pair);
        if (!serverConnection.validateConnection()) {
            throw new RESTErrorException(Messages.ViewerRecorder_ConnectionError());
        }
        return serverConnection;
    }
}
