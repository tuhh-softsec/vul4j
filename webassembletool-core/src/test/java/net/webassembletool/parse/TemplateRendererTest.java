package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.StringOutput;
import net.webassembletool.util.MockStringOutput;

public class TemplateRendererTest extends TestCase {

    public void testRenderTemplateError() throws IOException {
        final StringOutput expectedOutput = new MockStringOutput("expected");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
        expectedOutput.setStatusMessage("abc");

        TemplateRenderer tested = new TemplateRenderer(null, null, null);
        try {
            tested.render(expectedOutput, null, null);
            fail("should throw RetrieveException");
        } catch (RetrieveException e) {
            assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
            assertEquals("abc", e.getStatusMessage());
            assertEquals("expected", e.getErrorPageContent());
        }
    }

    public void testRenderTemplateNull1() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        StringWriter out = new StringWriter();

        TemplateRenderer tested = new TemplateRenderer(null, null, null);
        tested.render(expectedOutput, out, null);
        assertEquals(0, out.toString().length());
    }

    public void testRenderTemplateNull2() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");

        StringWriter out = new StringWriter();

        TemplateRenderer tested = new TemplateRenderer(null, params, null);
        tested.render(expectedOutput, out, null);

        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));
        assertTrue(out.toString().contains("'another value'"));
    }

    public void testRenderTemplate1() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(
                "some <!--$beginparam$key-->some hidden text goes here<!--$endparam$key--> printed");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");

        StringWriter out = new StringWriter();
        TemplateRenderer tested = new TemplateRenderer(null, params, null);
        tested.render(expectedOutput, out, null);

        assertFalse(out.toString().contains("key"));
        assertTrue(out.toString().contains("'value'"));
        assertFalse(out.toString().contains("some other key"));

        assertEquals("some 'value' printed", out.toString());
    }

    public void testRenderTemplate2() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(
                "abc some<!--$begintemplate$A-->some text goes here<!--$endtemplate$A--> cdf hello");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

        StringWriter out = new StringWriter();

        TemplateRenderer tested = new TemplateRenderer("A", null, null);
        tested.render(expectedOutput, out, null);
        assertEquals("some text goes here", out.toString());
    }
}
