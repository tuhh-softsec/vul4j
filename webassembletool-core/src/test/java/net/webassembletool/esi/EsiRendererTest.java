package net.webassembletool.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.MockDriver;

public class EsiRendererTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		MockDriver provider = new MockDriver("mock");
		provider.addResource("/test", "test");
	}

	public void testInclude() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/test\" /> after";
		EsiRenderer tested = new EsiRenderer(null);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("before test after", out.toString());
	}

	public void testEsiComment() throws IOException, HttpErrorPage {
		String page = "begin <!--esi<sometag> some text</sometag>--> end";
		EsiRenderer tested = new EsiRenderer(null);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("begin <sometag> some text</sometag> end", out.toString());
	}

}
