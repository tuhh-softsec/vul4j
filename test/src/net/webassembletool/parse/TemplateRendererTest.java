package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.RenderException;
import net.webassembletool.ouput.StringOutput;

/**
 * TODO Type javadoc
 * 
 * @author satyr
 */
public class TemplateRendererTest extends TestCase {

    public void testRenderTemplateError() throws IOException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return "expected";
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
	expectedOutput.setStatusMessage("abc");

	TemplateRenderer tested = new TemplateRenderer(null, null, null, null,
		null);
	try {
	    tested.render(expectedOutput);
	    fail("should throw RenderException");
	} catch (RenderException e) {
	    assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
	    assertEquals("abc", e.getStatusMessage());
	    assertEquals("expected", e.getErrorPageContent());
	}
    }

    public void testRenderTemplateNull1() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return null;
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
	StringWriter out = new StringWriter();

	TemplateRenderer tested = new TemplateRenderer(null, null, null, out,
		null);
	tested.render(expectedOutput);
	assertEquals(0, out.toString().length());
    }

    public void testRenderTemplateNull2() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return null;
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

	HashMap<String, String> params = new HashMap<String, String>();
	params.put("key", "'value'");
	params.put("some other key", "'another value'");

	StringWriter out = new StringWriter();

	TemplateRenderer tested = new TemplateRenderer(null, null, params, out,
		null);
	tested.render(expectedOutput);

	assertFalse(out.toString().contains("key"));
	assertTrue(out.toString().contains("'value'"));
	assertFalse(out.toString().contains("some other key"));
	assertTrue(out.toString().contains("'another value'"));
    }

    public void testRenderTemplate1() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return "some <!--$beginparam$key-->some hidden text goes here<!--$endparam$key--> printed";
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

	HashMap<String, String> params = new HashMap<String, String>();
	params.put("key", "'value'");
	params.put("some other key", "'another value'");

	StringWriter out = new StringWriter();
	TemplateRenderer tested = new TemplateRenderer(null, null, params, out,
		null);
	tested.render(expectedOutput);

	assertFalse(out.toString().contains("key"));
	assertTrue(out.toString().contains("'value'"));
	assertFalse(out.toString().contains("some other key"));

	assertEquals("some 'value' printed", out.toString());
    }

    public void testRenderTemplate2() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return "abc some<!--$begintemplate$A-->some text goes here<!--$endtemplate$A--> cdf hello";
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);

	StringWriter out = new StringWriter();

	TemplateRenderer tested = new TemplateRenderer("A", null, null, out,
		null);
	tested.render(expectedOutput);
	assertEquals("some text goes here", out.toString());
    }
}
