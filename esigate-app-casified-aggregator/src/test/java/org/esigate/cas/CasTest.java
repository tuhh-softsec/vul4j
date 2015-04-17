package org.esigate.cas;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class CasTest extends TestCase {
    private WebConversation webConversation;

    @Override
    public void setUp() throws Exception {
        webConversation = new WebConversation();
        // webConversation.setExceptionsThrownOnErrorStatus(false);
        webConversation.getClientProperties().setAutoRedirect(false);
        webConversation.getClientProperties().setAcceptCookies(true);
    }

    public void testAuthenticationOk() throws MalformedURLException, IOException, SAXException {
        // Trying to access the application
        WebRequest req =
                new GetMethodWebRequest(
                        "http://localhost:8080/esigate-app-casified-aggregator/aggregated1/protected/block.jsp");
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("We should have been redirected to CAS", HttpServletResponse.SC_MOVED_TEMPORARILY,
                resp.getResponseCode());
        assertEquals(
                "We should have been redirected to CAS",
                "http://localhost:8080/esigate-app-cas/login?service=http%3A%2F%2Flocalhost%3A8080%2Fesigate-app-casified-aggregator%2Faggregated1%2Fprotected%2Fblock.jsp",
                resp.getHeaderField("Location"));
        // CAS authentication
        req =
                new GetMethodWebRequest(
                        "http://localhost:8080/esigate-app-cas/login?service=http%3A%2F%2Flocalhost%3A8080%2Fesigate-app-casified-aggregator%2Faggregated1%2Fprotected%2Fblock.jsp");
        resp = webConversation.getResponse(req);
        assertEquals("CAS not responding properly", HttpServletResponse.SC_OK, resp.getResponseCode());
        WebForm loginForm = resp.getForms()[0];
        loginForm.setParameter("username", "test");
        loginForm.setParameter("password", "test");
        resp = loginForm.submit();
        assertEquals("We should have been redirected to the application", HttpServletResponse.SC_MOVED_TEMPORARILY,
                resp.getResponseCode());
        String redirectLocation = resp.getHeaderField("Location");
        StringAssert.assertStartsWith("We should have been redirected to the application with a ticket",
                "http://localhost:8080/esigate-app-casified-aggregator/aggregated1/protected/block.jsp",
                redirectLocation);
        StringAssert.assertContains("We should have been redirected to the application with a ticket", "ticket=",
                redirectLocation);
        // Return to the application
        webConversation.getClientProperties().setAutoRedirect(true);
        req = new GetMethodWebRequest(redirectLocation);
        resp = webConversation.getResponse(req);
        assertTrue("We should have been redirected to the same page without the ticket", resp.getURL().toString()
                .startsWith("http://localhost:8080/esigate-app-casified-aggregator/aggregated1/protected/block.jsp"));
        String pageContent = resp.getText();
        StringAssert.assertContains("The page should contain a page from aggregated1", "Page from aggregated1",
                pageContent);
        StringAssert.assertContains("The page should contain a block from aggregated2",
                "This is a block from aggregated2", pageContent);
        StringAssert.assertContains("The user should be authenticated as test", "User: test", pageContent);
    }

    public void testUnauthenthorized() throws MalformedURLException, IOException, SAXException {
        // Trying to access the application
        WebRequest req =
                new GetMethodWebRequest("http://localhost:8080/esigate-app-casified-aggregator/aggregated1/block.jsp");
        try {
            webConversation.getResponse(req);
            fail("We should get a 401 Unauthorized");
        } catch (HttpException e) {
            assertEquals("We should get a 401 Unauthorized", HttpServletResponse.SC_UNAUTHORIZED, e.getResponseCode());

        }
    }
}
