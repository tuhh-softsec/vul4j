package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.test.MockHttpServletRequest;
import org.esigate.test.MockHttpServletResponse;

public class CommentElementTest extends TestCase {
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Override
	protected void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	public void testCommentEmpty() throws IOException, HttpErrorPage {
		String page = "begin <esi:comment text=\"some comment\" /> end";
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin  end", out.toString());
	}

	public void testComment() throws IOException, HttpErrorPage {
		String page = "begin <esi:comment text=\"some comment\" > some text </esi:comment> end";
		EsiRenderer tested = new EsiRenderer(request, response, null);
		StringWriter out = new StringWriter();
		tested.render(null, page, out);
		assertEquals("begin  end", out.toString());
	}
}
