package net.webassembletool.parse;

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

        tested.render(expectedOutput, null, null);
    }

    public void testRenderBlock() throws IOException, HttpErrorPage {
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
