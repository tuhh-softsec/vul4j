package org.esigate.test.cases;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexis Thaveau on 26/01/16.
 */
public abstract class BaseAggregatorTest extends TestCase {
    protected final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-aggregator/";
    protected final static String RESOURCES_PATH = "/";

    protected WebConversation webConversation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        webConversation = new WebConversation();
        webConversation.setExceptionsThrownOnErrorStatus(false);
    }

    protected WebResponse doSimpleTest(String page) throws Exception {
        // We assume the file name is the name of the resource
        return doSimpleTest(page, page);
    }

    protected WebResponse doSimpleTest(String page, String resultResource) throws Exception {
        WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + page);
        WebResponse resp = webConversation.getResponse(req);
        assertEquals("Status should be 200\n" + resp.getText(), HttpServletResponse.SC_OK, resp.getResponseCode());
        assertEqualsIgnoreLineFeeds(getResource(resultResource), resp.getText());
        return resp;
    }

    protected String getResource(String file) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(RESOURCES_PATH + file);
        String result = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
        return result;
    }

    protected void assertEqualsIgnoreCarriageReturn(String expected, String actual) {
        assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
    }

    protected void assertEqualsIgnoreLineFeeds(String expected, String actual) {
        assertEqualsIgnoreCarriageReturn(expected.replaceAll("\n", ""), actual.replaceAll("\n", ""));
    }

    protected void assertEqualsIgnoreWhiteSpaces(String expected, String actual) {
        assertEqualsIgnoreLineFeeds(expected.replaceAll("\t", "").replaceAll(" ", ""), actual.replaceAll("\t", "")
                .replaceAll(" ", ""));
    }
}
