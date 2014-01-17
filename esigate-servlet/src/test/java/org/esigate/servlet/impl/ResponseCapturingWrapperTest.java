package org.esigate.servlet.impl;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.esigate.http.ContentTypeHelper;
import org.esigate.servlet.MockHttpServletResponse;

public class ResponseCapturingWrapperTest extends TestCase {
    private static final int BUFFER_SIZE = 1024;
    private static final String SMALL_STRING = "test";
    private static final String BIG_STRING;
    private ContentTypeHelper contentTypeHelper;
    private MockHttpServletResponse httpServletResponse;
    static {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            stringBuilder.append(SMALL_STRING);
        }
        BIG_STRING = stringBuilder.toString();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Properties properties = new Properties();
        contentTypeHelper = new ContentTypeHelper(properties);
        httpServletResponse = new MockHttpServletResponse();
    }

    public void testSmallHtmlResponse() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, false,
                BUFFER_SIZE);
        tested.setContentType("text/html;charset=UTF-8");
        tested.getWriter().write(SMALL_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testSmallHtmlResponseWrittenToOutputStream() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, false,
                BUFFER_SIZE);
        tested.setContentType("text/html;charset=UTF-8");
        tested.getOutputStream().write(SMALL_STRING.getBytes("UTF-8"));
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testSmallHtmlResponseWrittenToOutputStreamAndClose() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, false,
                BUFFER_SIZE);
        tested.setContentType("text/html;charset=UTF-8");
        tested.getOutputStream().write(SMALL_STRING.getBytes("UTF-8"));
        tested.getOutputStream().close();
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testSmallNonParseableResponse() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, true,
                BUFFER_SIZE);
        tested.setContentType("binary/octet-stream");
        tested.getOutputStream().print(SMALL_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testBigHtmlResponse() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, false,
                BUFFER_SIZE);
        tested.setContentType("text/html;charset=UTF-8");
        tested.getWriter().write(BIG_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(BIG_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testBigNonParseableResponse() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, true,
                BUFFER_SIZE);
        tested.setContentType("binary/octet-stream");
        tested.getOutputStream().print(BIG_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(BIG_STRING.substring(0, BUFFER_SIZE), EntityUtils.toString(entity));
        assertNull("Nothing should have been written using the writer", httpServletResponse.getWriterContent());
        assertEquals("The response should have been sent entirely to the outputStream", BIG_STRING,
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testParseableResponseNoCharset() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, true,
                BUFFER_SIZE);
        tested.setContentType("text/html");
        tested.getWriter().write(SMALL_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

    public void testParseableResponseNoContentType() throws Exception {
        ResponseCapturingWrapper tested = new ResponseCapturingWrapper(httpServletResponse, contentTypeHelper, true,
                BUFFER_SIZE);
        tested.getWriter().write(SMALL_STRING);
        HttpEntity entity = tested.getResponse().getEntity();
        assertNotNull(entity);
        assertEquals(SMALL_STRING, EntityUtils.toString(entity));
        assertNull("Nothing should have been written to the response yet", httpServletResponse.getWriterContent());
        assertNull("Nothing should have been written to the response yet",
                httpServletResponse.getOutputStreamContentAsString("UTF-8"));
    }

}
