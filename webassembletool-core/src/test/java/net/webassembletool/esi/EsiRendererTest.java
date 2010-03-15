package net.webassembletool.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.MockDriver;

public class EsiRendererTest extends TestCase {
	private MockDriver provider;

	@Override
	protected void setUp() throws Exception {
		provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");
	}

	public void testInclude() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"$PROVIDER({mock})/test\" /> after";
		EsiRenderer tested = new EsiRenderer(null, null, provider);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("before test after", out.toString());
	}
	
	public void testIncludeAbsolute() throws IOException, HttpErrorPage {
		String page = "before <esi:include src=\"http://www.foo.com/test\" /> after";
		EsiRenderer tested = new EsiRenderer(null, null, provider);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("before test after", out.toString());
	}

	public void testEsiComment() throws IOException, HttpErrorPage {
		String page = "begin <!--esi<sometag> some text</sometag>--> end";
		EsiRenderer tested = new EsiRenderer(null, null, provider);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("begin <sometag> some text</sometag> end", out.toString());
	}
	
	public void testRemove() throws IOException, HttpErrorPage {
		String page = "begin <esi:remove>some text to be removed</esi:remove> end";
		EsiRenderer tested = new EsiRenderer(null, null, provider);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("begin  end", out.toString());
	}
	
	public void testComment() throws IOException, HttpErrorPage {
		String page = "begin <esi:comment text=\"some comment\" /> end";
		EsiRenderer tested = new EsiRenderer(null, null, provider);
		StringWriter out = new StringWriter();
		tested.render(page, out);
		assertEquals("begin  end", out.toString());
	}

}
