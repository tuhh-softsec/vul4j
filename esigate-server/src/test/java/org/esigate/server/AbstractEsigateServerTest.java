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
