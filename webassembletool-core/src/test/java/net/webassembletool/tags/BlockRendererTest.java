package net.webassembletool.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.output.MockStringOutput;
import net.webassembletool.output.StringOutput;

public class BlockRendererTest extends TestCase {

	public void testRenderBlockNull() throws IOException, HttpErrorPage {
		final StringOutput expectedOutput = new MockStringOutput(null);
		expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
		BlockRenderer tested = new BlockRenderer(null, null);
		tested.render(null, null);
	}

	public void testRenderBlock() throws IOException, HttpErrorPage {
		final String expectedOutput = "abc some<!--$beginblock$myblock$-->some text goes here<!--$endblock$myblock$--> cdf hello";
		Writer out = new StringWriter();
		BlockRenderer tested = new BlockRenderer("myblock", null);
		tested.render(expectedOutput, out);
		assertEquals("some text goes here", out.toString());
		// null name means whole page
		out = new StringWriter();
		tested = new BlockRenderer(null, null);
		tested.render(expectedOutput, out);
		assertEquals(expectedOutput, out.toString());
	}

	public void testUnknownTag() throws IOException, HttpErrorPage {
		final String input = "abc some<!--$hello$world$-->some text goes here";
		Writer out = new StringWriter();
		BlockRenderer tested = new BlockRenderer(null, null);
		tested.render(input, out);
		// input should remain unchanged
		assertEquals(input, out.toString());
	}

}
