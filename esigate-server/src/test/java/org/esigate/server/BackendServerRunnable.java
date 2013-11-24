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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

/**
 * Starts esigate server.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class BackendServerRunnable implements Runnable {

    private int port;
    private Handler handler;
    private Server server = null;

    /**
     * Starts a backend server based on jetty. This server will use the provided handler to handler requests.
     * 
     * @param port
     *            the connector port to use for this backend.
     * @param handler
     *            the handler which will process requests.
     */
    public BackendServerRunnable(int port, Handler handler) {
        this.port = port;
        this.handler = handler;

    }

    @Override
    public void run() {
        try {
            this.server = new Server(this.port);
            this.server.setHandler(this.handler);

            this.server.start();
            this.server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the backend server.
     */
    public void stop() {
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if server is ready to handle requests.
     * 
     * @return true if server is started.
     */
    public boolean isStarted() {
        return this.server.isStarted();
    }
}
