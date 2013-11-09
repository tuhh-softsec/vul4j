package org.esigate.server;

import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class EsigateServerTest {

    /**
     * Test control handler.
     * 
     * @throws Exception
     */
    @Test
    public void testControlConnection() throws Exception {
        Executors.newSingleThreadExecutor().execute(new EsigateServerRunnable());

        for (int i = 0; i < 50; i++) {
            if (EsigateServer.isStarted()) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        WebConversation webConversation;

        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
        WebRequest req = new GetMethodWebRequest("http://localhost:8081/server-status?auto");
        WebResponse resp = webConversation.getResponse(req);

        Assert.assertEquals(200, resp.getResponseCode());
        System.out.println(resp.getText());
        EsigateServer.stop();
    }

}
