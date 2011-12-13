package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.cookie.BasicClientCookie;
import org.esigate.test.MockHttpRequest;

public class CommentTest extends TestCase {

	private MockHttpRequest request;
	private ResourceContext ctx;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws Exception {
		request = new MockHttpRequest();

		ctx = new ResourceContext(null, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testComment() throws IOException, HttpErrorPage {
		String page = "begin <!--esi<sometag> some text</sometag>--> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin <sometag> some text</sometag> end", out.toString());
	}

	public void testCommentVars() throws IOException, HttpErrorPage {
		String page = "<!--esi <p><esi:vars>Hello, $(HTTP_COOKIE{name})!</esi:vars></p> -->";
		request.addCookie(new BasicClientCookie("name", "world"));
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals(" <p>Hello, world!</p> ", out.toString());
	}

}
