package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class CommentTest extends TestCase {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testComment() throws IOException, HttpErrorPage {
		String page = "begin <!--esi<sometag> some text</sometag>--> end";
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin <sometag> some text</sometag> end", out.toString());
	}

	public void testCommentVars() throws IOException, HttpErrorPage {
		String page = "<!--esi <p><esi:vars>Hello, $(HTTP_COOKIE{name})!</esi:vars></p> -->";
		request.addCookie(new Cookie("name", "world"));
		EsiRenderer tested = new EsiRenderer(request, null, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals(" <p>Hello, world!</p> ", out.toString());
	}

}
