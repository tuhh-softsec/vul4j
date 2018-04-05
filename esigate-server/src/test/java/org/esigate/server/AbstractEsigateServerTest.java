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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Handler;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts Esigate Server in background for unit testing.
 * <p>
 * This class is intended to be extended by tests on esigate server.
 * 
 * @author Nicolas Richeton
 * 
 */
public class AbstractEsigateServerTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEsigateServerTest.class);

    private static final int DEFAULT_BACKEND_PORT = 8082;
    private static final int DEFAULT_CONTROL_PORT = 8081;
    private static final int DEFAULT_MAIN_PORT = 8080;
    static final int WAIT_RETRIES = 50;
    static final int WAIT_SLEEP = 200;
    private ExecutorService esigateExecutor = null;
    private ExecutorService backendExecutor = null;
    private int esigatePort;
    private int esigateControlPort;
    private int backendPort;
    private Handler backendHandler;
    private BackendServerRunnable backendServerRunnable = null;

    /**
     * Starts esigate instance on default ports : 8080 for main connection and 8081 for control connection.
     */
    public AbstractEsigateServerTest() {
        this(DEFAULT_MAIN_PORT, DEFAULT_CONTROL_PORT, DEFAULT_BACKEND_PORT, null);
    }

    /**
     * Starts esigate using the provided ports.
     * <p>
     * If backendHandler is not null, also starts a backend server on backendPort, which uses this handler to process
     * requests. This allows complete testing of esigate.
     * 
     * @param esigatePort
     *            port for the main connection.
     * @param esigateControlPort
     *            port for the control/status connection.
     * @param backendPort
     *            port for the backend server (provider).
     * @param backendHandler
     *            handler for the backend server.
     */
    public AbstractEsigateServerTest(int esigatePort, int esigateControlPort, int backendPort, Handler backendHandler) {
        this.esigatePort = esigatePort;
        this.esigateControlPort = esigateControlPort;
        this.backendPort = backendPort;
        this.backendHandler = backendHandler;
    }

    /**
     * Start esigate server before each test.
     * 
     */
    @Before
    public void setUp() {

        Properties p = new Properties();
        p.setProperty("controlPort", "" + this.esigateControlPort);
        p.setProperty("port", "" + this.esigatePort);

        this.esigateExecutor = Executors.newSingleThreadExecutor();
        this.esigateExecutor.execute(new EsigateServerRunnable(p));
        for (int i = 0; i < WAIT_RETRIES; i++) {
            if (EsigateServer.isStarted()) {
                break;
            }
            try {
                Thread.sleep(WAIT_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!EsigateServer.isStarted()) {
            LOG.error("Esigate server failed to start");
        }

        if (this.backendHandler != null) {
            this.backendServerRunnable = new BackendServerRunnable(this.backendPort, this.backendHandler);
            this.backendExecutor = Executors.newSingleThreadExecutor();
            this.backendExecutor.execute(this.backendServerRunnable);
            for (int i = 0; i < WAIT_RETRIES; i++) {
                if (this.backendServerRunnable.isStarted()) {
                    break;
                }
                try {
                    Thread.sleep(WAIT_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!backendServerRunnable.isStarted()) {
                LOG.error("Backend server failed to start");
            }
        }
    }

    /**
     * Stops esigate server.
     */
    @After
    public void tearDown() {
        EsigateServer.stop();
        this.esigateExecutor.shutdown();

    }

    /**
     * Get the current backend port.
     * 
     * @return current port.
     */
    public int getBackendPort() {
        return this.backendPort;
    }

    /**
     * Get the current esigate control connection port.
     * 
     * @return current port.
     */
    public int getEsigateControlPort() {
        return this.esigateControlPort;
    }

    /**
     * Get the current esigate main connection port.
     * 
     * @return current port.
     */
    public int getEsigatePort() {
        return this.esigatePort;
    }
}
