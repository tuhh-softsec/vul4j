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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportURLBuilder {
    private static final String RESTPATH_REPORTS = "/rest/management/reports/create/";
    private static final String RESTPATH_DASHBOARDS = "/rest/management/dashboard/";
    private static final Logger logger = Logger.getLogger(ReportURLBuilder.class.getName());
    private final List<String> parameter;
    private String dashboardName;
    private String serverAddress;

    public ReportURLBuilder() {
        parameter = new ArrayList<String>();
    }

    public ReportURLBuilder setSource(final String source) {
        parameter.add("source=stored:" + PerfSigUtils.encodeString(source));
        return this;
    }

    public ReportURLBuilder setComparison(final String comparison) {
        parameter.add("compare=stored:" + PerfSigUtils.encodeString(comparison));
        return this;
    }

    public ReportURLBuilder setType(final String type) {
        parameter.add("type=" + type);
        return this;
    }

    public ReportURLBuilder setDashboardName(final String dashboardName) {
        this.dashboardName = dashboardName;
        return this;
    }

    public ReportURLBuilder setServerAddress(final String name) {
        this.serverAddress = name;
        return this;
    }

    public URL buildURL(final boolean reportSwitch) {
        URL dashboardURL;
        try {
            final StringBuilder sb = new StringBuilder(this.serverAddress);
            if (reportSwitch) {
                sb.append(RESTPATH_REPORTS);
            } else {
                sb.append(RESTPATH_DASHBOARDS);
            }
            sb.append(dashboardName);
            if (!parameter.isEmpty()) sb.append("?");
            for (String param : parameter) {
                if (parameter.indexOf(param) != 0) sb.append("&");
                sb.append(param);
            }
            dashboardURL = new URL(sb.toString());
        } catch (Exception e) {
            throw new ContentRetrievalException("failed to build URL", e);
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("built URL: " + dashboardURL.toExternalForm());
        }
        return dashboardURL;
    }

}
