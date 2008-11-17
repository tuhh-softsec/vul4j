package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.RenderException;
import net.webassembletool.ouput.StringOutput;

/**
 * TODO Type javadoc
 * 
 * @author satyr
 */
public class BlockRendererTest extends TestCase {

    public void testRenderBlockError() throws IOException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return "expected";
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
	expectedOutput.setStatusMessage("abc");

	BlockRenderer tested = new BlockRenderer(null, null, null, null);
	try {
	    tested.render(expectedOutput);
	    fail("should throw RenderException");
	} catch (RenderException e) {
	    assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
	    assertEquals("abc", e.getStatusMessage());
	    assertEquals("expected", e.getErrorPageContent());
	}
    }

    public void testRenderBlockNull() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return null;
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
	BlockRenderer tested = new BlockRenderer(null, null, null, null);

	tested.render(expectedOutput);
    }

    public void testRenderBlock() throws IOException, RenderException {
	final StringOutput expectedOutput = new StringOutput() {
	    @Override
	    public String toString() {
		return "abc some<!--$beginblock$A-->some text goes here<!--$endblock$A--> cdf hello";
	    }
	};
	expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
	Writer out = new StringWriter();

	BlockRenderer tested = new BlockRenderer("A", null, out, null);
	tested.render(expectedOutput);
	assertEquals("some text goes here", out.toString());
    }

}
