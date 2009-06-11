package net.webassembletool;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.output.StringOutput;
import net.webassembletool.util.MockStringOutput;

public class DriverTest extends TestCase {

    public void testRenderBlockError() throws IOException, RenderingException {
        final StringOutput expectedOutput = new MockStringOutput("expected");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
        expectedOutput.setStatusMessage("abc");
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        try {
            tested.renderBlock(null, null, null, null, null, null, false);
            fail("should throw RenderException");
        } catch (RetrieveException e) {
            assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
            assertEquals("abc", e.getStatusMessage());
            assertEquals("expected", e.getErrorPageContent());
        }
    }

    public void testRenderBlockNull() throws IOException, RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        tested.renderBlock(null, null, null, null, null, null, false);
    }

    public void testRenderBlock() throws IOException, RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(
                "abc some<!--$beginblock$A-->some text goes here<!--$endblock$A--> cdf hello");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        Writer out = new StringWriter();
        tested.renderBlock(null, "A", out, null, new HashMap<String, String>(),
                null, false);
        assertEquals("some text goes here", out.toString());
    }

    public void testRenderTemplateError() throws IOException,
            RenderingException {
        final StringOutput expectedOutput = new MockStringOutput("expected");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
        expectedOutput.setStatusMessage("abc");
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        try {
            tested.renderTemplate(null, null, null, null, null, null, null,
                    false);
            fail("should throw RenderException");
        } catch (RetrieveException e) {
            assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
            assertEquals("abc", e.getStatusMessage());
            assertEquals("expected", e.getErrorPageContent());
        }
    }

    public void testRenderTemplateNull1() throws IOException,
            RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        StringWriter out = new StringWriter();
        tested.renderTemplate(null, null, out, null, null, null, null, false);
        assertEquals(0, out.toString().length());
    }

    public void testRenderTemplateNull2() throws IOException,
            RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        StringWriter out = new StringWriter();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");

        tested.renderTemplate(null, null, out, null, params, null, null, false);
        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));
        assertTrue(out.toString().contains("'another value'"));
    }

    public void testRenderTemplate1() throws IOException, RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(
                "some <!--$beginparam$key-->some hidden text goes here<!--$endparam$key--> printed");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        StringWriter out = new StringWriter();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");

        tested.renderTemplate(null, null, out, null, params, null, null, false);
        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));

        assertEquals("some 'value' printed", out.toString());
    }

    public void testRenderTemplate2() throws IOException, RenderingException {
        final StringOutput expectedOutput = new MockStringOutput(
                "abc some<!--$begintemplate$A-->some text goes here<!--$endtemplate$A--> cdf hello");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Driver tested = new MockDriver("tested", new Properties(),
                expectedOutput);

        StringWriter out = new StringWriter();

        tested.renderTemplate(null, "A", out, null, null, null, null, false);
        assertEquals("some text goes here", out.toString());
    }

    private static final class MockDriver extends Driver {
        private final StringOutput expectedOutput;

        public MockDriver(String name, Properties props,
                StringOutput expectedOutput) {
            super(name, props);
            this.expectedOutput = expectedOutput;
        }

        @Override
        protected StringOutput getResourceAsString(RequestContext target) {
            return expectedOutput;
        }

    }
}
