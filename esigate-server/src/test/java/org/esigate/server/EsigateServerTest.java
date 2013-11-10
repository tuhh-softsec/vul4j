package org.esigate.server;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
public class EsigateServerTest extends AbstractEsigateServerTest {

    static final int STATUS_OK = 200;
    static final int STATUS_NOTFOUND = 404;

    /**
     * Test control handler (auto mode).
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    @SuppressWarnings("static-method")
    public void testControlConnectionAuto() throws Exception {

        WebConversation webConversation;

        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest req = new GetMethodWebRequest("http://localhost:8081/server-status");
        req.setParameter("auto", "");
        WebResponse resp = webConversation.getResponse(req);

        assertEquals(STATUS_OK, resp.getResponseCode());
        System.out.println(resp.getText());
        assertFalse(resp.getText().contains("Esigate Server Status"));

        assertTrue(StatusReader.getLong(resp.getText(), "Uptime") > 0);
        assertTrue(StatusReader.getDouble(resp.getText(), "CPULoad") > 0);
        assertEquals(0, StatusReader.getLong(resp.getText(), "Total Accesses").longValue());
        assertEquals(0d, StatusReader.getDouble(resp.getText(), "ReqPerSec"));

    }

    /**
     * Test control handler.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    @SuppressWarnings("static-method")
    public void testControlConnection() throws Exception {

        WebConversation webConversation;

        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest req = new GetMethodWebRequest("http://localhost:8081/server-status");
        WebResponse resp = webConversation.getResponse(req);

        assertEquals(STATUS_OK, resp.getResponseCode());
        assertTrue(resp.getText().contains("Esigate Server Status"));
        System.out.println(resp.getText());
    }

    /**
     * Ensure control handler only process requests on control port.
     * 
     * @throws Exception
     *             on error.
     */
    @SuppressWarnings("static-method")
    @Test
    public void testControlConnectionPort() throws Exception {

        WebConversation webConversation;

        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest req = new GetMethodWebRequest("http://localhost:8080/server-status?auto");
        WebResponse resp = webConversation.getResponse(req);

        assertEquals(STATUS_NOTFOUND, resp.getResponseCode());
    }

}
