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

import hudson.util.Scrambler;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by rapi on 25.08.2014.
 */
public class ProxyBlock {
    private final String proxyServer, proxyUser, proxyPassword;
    private final int proxyPort;

    @DataBoundConstructor
    public ProxyBlock(final String proxyServer, final int proxyPort, final String proxyUser, final String proxyPassword) {
        this.proxyServer = proxyServer;
        this.proxyUser = proxyUser;
        this.proxyPassword = Scrambler.scramble(proxyPassword);
        this.proxyPort = proxyPort;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public String getProxyPassword() {
        return Scrambler.descramble(proxyPassword);
    }

    public int getProxyPort() {
        return proxyPort;
    }
}
