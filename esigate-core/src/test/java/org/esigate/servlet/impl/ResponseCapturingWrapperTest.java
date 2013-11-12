package org.esigate.servlet.impl;

import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.esigate.http.ContentTypeHelper;
import org.esigate.http.HttpResponseUtils;
import org.esigate.servlet.MockHttpServletResponse;
import org.esigate.servlet.impl.ResponseCapturingWrapper;

public class ResponseCapturingWrapperTest extends TestCase {

    public void testHtmlResponse() throws Exception {
        Properties properties = new Properties();
        ContentTypeHelper contentTypeHelper = new ContentTypeHelper(properties);
        HttpServletResponse response = new MockHttpServletResponse();
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(response, contentTypeHelper);
        tested.setContentType("text/html;charset=UTF-8");
        tested.getWriter().write("test");
        assertEquals("test", HttpResponseUtils.toString(tested.getResponse(), null));
    }

    public void testNonParseableResponse() throws Exception {
        Properties properties = new Properties();
        ContentTypeHelper contentTypeHelper = new ContentTypeHelper(properties);
        HttpServletResponse response = new MockHttpServletResponse();
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(response, contentTypeHelper);
        tested.setContentType("binary/octet-stream");
        tested.getOutputStream().print("test");
        assertNull(tested.getResponse().getEntity());
    }

}
