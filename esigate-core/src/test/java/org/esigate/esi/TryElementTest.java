package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;

public class TryElementTest extends TestCase {

	private ResourceContext ctx;
	private EsiRenderer tested;

	@Override
	protected void setUp() {
		MockDriver provider = new MockDriver("mock");
		provider.addResource("/test", "test");
		provider.addResource("http://www.foo.com/test", "test");

		MockHttpRequest request = new MockHttpRequest();

		ctx = new ResourceContext(provider, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testTry() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt><esi:include src=\"http://www.foo.com/test\" /></esi:attempt>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin test end", out.toString());
	}

	public void testAttempt1() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt>abc <esi:include src=\"http://www.foo.com/test\" /> cba</esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin abc test cba end", out.toString());
	}

	public void testAttempt2() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt>abc "
				+ "<esi:include src=\"http://www.foo.com/test\" />"
				+ "<esi:include src='http://www.foo.com/not-found' onerror='continue' />"
				+ " cba</esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin abc test cba end", out.toString());
	}

	public void testExcept1() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt>abc <esi:include src=\"http://www.foo2.com/test\" /> cba</esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside except end", out.toString());
	}

	public void testExcept2() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt> "
				+ "<esi:include src='http://www.foo.com/test' /> abc <esi:include src=\"http://www.foo2.com/test\" /> cba"
				+ "</esi:attempt>"
				+ "<esi:except>inside except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside except end", out.toString());
	}

	public void testMultipleExcept() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt> "
				+ "<esi:attempt>abc <esi:include src='http://www.foo2.com/test' /> cba</esi:attempt>"
				+ "</esi:attempt>"
				+ "<esi:except code='500'>inside incorrect except</esi:except>"
				+ "<esi:except code='404'>inside correct except</esi:except>"
				+ "<esi:except code='412'>inside incorrect except</esi:except>"
				+ "<esi:except>inside default except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside correct except end", out.toString());
	}

	public void testDefaultExcept() throws IOException, HttpErrorPage {
		String page = "begin <esi:try>"
				+ "<esi:attempt> "
				+ "<esi:attempt>abc <esi:include src='http://www.foo2.com/test' /> cba</esi:attempt>"
				+ "</esi:attempt>"
				+ "<esi:except code='500'>inside incorrect except</esi:except>"
				+ "<esi:except code='412'>inside incorrect except</esi:except>"
				+ "<esi:except>inside default except</esi:except>"
				+ "</esi:try> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin inside default except end", out.toString());
	}
}
