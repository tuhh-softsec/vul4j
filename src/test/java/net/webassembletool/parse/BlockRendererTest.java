package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.webassembletool.RetrieveException;
import net.webassembletool.output.StringOutput;
import net.webassembletool.util.MockStringOutput;

public class BlockRendererTest extends TestCase {

    public void testRenderBlockError() throws IOException {
        final StringOutput expectedOutput = new MockStringOutput("expected");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK + 1);
        expectedOutput.setStatusMessage("abc");

        BlockRenderer tested = new BlockRenderer(null, null);
        try {
            tested.render(expectedOutput, null, null);
            fail("should throw RetrieveException");
        } catch (RetrieveException e) {
            assertEquals(HttpServletResponse.SC_OK + 1, e.getStatusCode());
            assertEquals("abc", e.getStatusMessage());
            assertEquals("expected", e.getErrorPageContent());
        }
    }

    public void testRenderBlockNull() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(null);
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        BlockRenderer tested = new BlockRenderer(null, null);

        tested.render(expectedOutput, null, null);
    }

    public void testRenderBlock() throws IOException, RetrieveException {
        final StringOutput expectedOutput = new MockStringOutput(
                "abc some<!--$beginblock$A-->some text goes here<!--$endblock$A--> cdf hello");
        expectedOutput.setStatusCode(HttpServletResponse.SC_OK);
        Writer out = new StringWriter();

        BlockRenderer tested = new BlockRenderer("A", null);
        tested.render(expectedOutput, out, null);
        assertEquals("some text goes here", out.toString());

        // null name means whole page
        out = new StringWriter();
        tested = new BlockRenderer(null, null);
        tested.render(expectedOutput, out, null);
        assertEquals(expectedOutput.toString(), out.toString());
    }

}
