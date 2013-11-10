package org.esigate.server;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.Executors;

import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Esigate server testing.
 * 
 * @author Nicolas Richeton
 * 
 */
public class EsigateServerTest {
    static final int WAIT_RETRIES = 50;
    static final int WAIT_SLEEP = 100;
    static final int STATUS_OK = 200;

    /**
     * Test control handler.
     * 
     * @throws Exception
     */
    @Test
    public void testControlConnection() throws Exception {
        Executors.newSingleThreadExecutor().execute(new EsigateServerRunnable());

        for (int i = 0; i < WAIT_RETRIES; i++) {
            if (EsigateServer.isStarted()) {
                break;
            }
            Thread.sleep(WAIT_SLEEP);
        }
        WebConversation webConversation;

        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest req = new GetMethodWebRequest("http://localhost:8081/server-status?auto");
        WebResponse resp = webConversation.getResponse(req);

        assertEquals(STATUS_OK, resp.getResponseCode());
        System.out.println(resp.getText());

        assertTrue(StatusReader.getLong(resp.getText(), "Uptime") > 0);
        assertTrue(StatusReader.getDouble(resp.getText(), "CPULoad") > 0);
        assertEquals(0, StatusReader.getLong(resp.getText(), "Total Accesses").longValue());
        assertEquals(0d, StatusReader.getDouble(resp.getText(), "ReqPerSec"));
        EsigateServer.stop();
    }

}
