package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpServletRequest;

public class CommentElementTest extends TestCase {
	private ResourceContext ctx;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();

		ctx = new ResourceContext(null, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testCommentEmpty() throws IOException, HttpErrorPage {
		String page = "begin <esi:comment text=\"some comment\" /> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin  end", out.toString());
	}

	public void testComment() throws IOException, HttpErrorPage {
		String page = "begin <esi:comment text=\"some comment\" > some text </esi:comment> end";
		StringWriter out = new StringWriter();
		tested.render(ctx, page, out);
		assertEquals("begin  end", out.toString());
	}
}
