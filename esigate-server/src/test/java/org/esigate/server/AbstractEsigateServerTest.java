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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;

/**
 * Starts Esigate Server in background for unit testing.
 * <p>
 * This class is intended to be extended by tests on esigate server.
 * 
 * @author Nicolas Richeton
 * 
 */
public class AbstractEsigateServerTest {
    static final int WAIT_RETRIES = 50;
    static final int WAIT_SLEEP = 100;
    private ExecutorService executor = null;

    /**
     * Start esigate server before each test.
     * 
     */
    @Before
    public void setUp() {
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.execute(new EsigateServerRunnable());
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
    }

    /**
     * Stops esigate server.
     */
    @After
    public void tearDown() {
        EsigateServer.stop();
        this.executor.shutdown();

    }

}
