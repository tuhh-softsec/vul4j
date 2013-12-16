/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.server;

import java.util.Properties;
 
/**
 * Starts esigate server.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class EsigateServerRunnable implements Runnable {

    private Properties configuration;

    /**
     * Starts esigate-server using the provided configuration. Same as using -D or server.properties.
     * 
     * @param configuration
     *            configuration to use.
     */
    public EsigateServerRunnable(Properties configuration) {
        this.configuration = configuration;
    }

    @Override
    public void run() {
        EsigateServer.init(this.configuration);
        try {
            EsigateServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
