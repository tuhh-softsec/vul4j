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

package de.tsystems.mms.apm.performancesignature.model;

public class ClientLinkGenerator {
    public static final String LOADTEST_OVERVIEW = "LoadTest Overview";
    public static final String PUREPATH_OVERVIEW = "PurePath Overview";
    public static final String WEBSTART = "open Webstart client";
    private final int port;
    private final String protocol, server, dashboardName, sessionName;
    private final String target;

    public ClientLinkGenerator(final int port, final String protocol, final String server, final String dashboardName, final String sessionName,
                               final String target) {
        this.port = port;
        this.protocol = protocol;
        this.server = server;
        this.dashboardName = dashboardName;
        this.sessionName = sessionName;
        this.target = target;
    }

    public String generateLink() {
        String webstartTemplate = "%s://%s:%d/webstart/Client/client.jnlp?argument=-reuse&argument=-dashboard&argument=online://%s/%s?source=%s";
        String clientRESTTemplate = "http://localhost:8030/rest/integration/opendashboard?dashboardname=%s&server=%s&secure=true&source=stored:%s";
        if (target.equals(WEBSTART)) {
            return String.format(webstartTemplate, protocol, server, port, server, dashboardName, sessionName);
        } else if (target.equals(PUREPATH_OVERVIEW)) {
            return String.format(clientRESTTemplate, "PurePaths", server, sessionName);
        } else if (target.equals(LOADTEST_OVERVIEW)) {
            return String.format(clientRESTTemplate, "LoadTest%20Overview", server, sessionName);
        }
        return "";
    }
}
