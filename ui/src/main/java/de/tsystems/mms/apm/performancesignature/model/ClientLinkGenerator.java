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

import java.net.MalformedURLException;
import java.net.URL;

public class ClientLinkGenerator {
    public static final String LOADTEST_OVERVIEW = "LoadTest Overview";
    public static final String PUREPATH_OVERVIEW = "PurePath Overview";
    public static final String WEBSTART = "open Webstart client";
    private final String dashboardName;
    private final String sessionName;
    @Deprecated
    private transient int port;
    @Deprecated
    private transient String protocol;
    @Deprecated
    private transient String server;
    private String serverUrl;
    private final String target;

    public ClientLinkGenerator(final String serverUrl, final String dashboardName, final String sessionName, final String target) {
        this.serverUrl = serverUrl;
        this.dashboardName = dashboardName;
        this.sessionName = sessionName;
        this.target = target;
    }

    public String generateLink() throws MalformedURLException {
        String webstartTemplate = "%s/webstart/Client/client.jnlp?argument=-reuse&argument=-dashboard&argument=online://%s/%s?source=%s";
        String clientRESTTemplate = "http://localhost:8030/rest/integration/opendashboard?dashboardname=%s&server=%s&secure=true&source=stored:%s";
        String host = new URL(serverUrl).getHost();
        switch (target) {
            case WEBSTART:
                return String.format(webstartTemplate, serverUrl, host, dashboardName, sessionName);
            case PUREPATH_OVERVIEW:
                return String.format(clientRESTTemplate, "PurePaths", host, sessionName);
            case LOADTEST_OVERVIEW:
                return String.format(clientRESTTemplate, "LoadTest%20Overview", host, sessionName);
            default:
                return "";
        }
    }

    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        if (protocol != null && server != null && port != 0 && serverUrl == null) {
            serverUrl = protocol + "://" + server + ":" + port;
            protocol = null;
            server = null;
            port = 0;
        }
        return this;
    }
}
