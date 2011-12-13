package org.esigate.parser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;

public class ParserTest extends TestCase {
	private Parser tested;

	@Override
	protected void setUp() {
		tested = new Parser(Pattern.compile("(<test:[^>]*>)|(</test:[^>]*>)"), SIMPLE, BODY);
		MockHttpRequest request = new MockHttpRequest("http://a.b?request=updated");
		tested.setResourceContext(new ResourceContext(null, null, null, request, null));
	}

	@Override
	protected void tearDown() {
		tested = null;
	}

	public void testParse() throws IOException, HttpErrorPage {
		String page = "begin "
				+ "<test:simple name='ignored'> this text will be ignored </test:simple>"
				+ "<test:body>this text should be {request} </test:body>"
				+ "<test:unknown name='value' />"
				+ "<test:simple name='also ignored'/>"
				+ " end";
		StringWriter out = new StringWriter();

		tested.parse(page, out);
		assertEquals("begin this text should be updated <test:unknown name='value' /> end", out.toString());
	}

	private static final ElementType SIMPLE = new MockElementType("<test:simple", "</test:simple") {
		public Element newInstance() { return new SimpleElement(); }
	};
	private static final ElementType BODY = new MockElementType("<test:body", "</test:body") {
		public Element newInstance() { return new BodyElement(); }
	};

	private static abstract class MockElementType implements ElementType {
		private final String startTag;
		private final String endTag;

		protected MockElementType(String startTag, String endTag) {
			this.startTag = startTag;
			this.endTag = endTag;
		}

		public final boolean isStartTag(String tag) { return tag.startsWith(startTag); }
		public final boolean isEndTag(String tag) { return tag.startsWith(endTag); }
	}
	private static class BodyElement extends SimpleElement {
		private final StringBuilder buf = new StringBuilder();

		public BodyElement() { }

		@Override
		public void onTagEnd(String tag, ParserContext ctx) throws IOException {
			String result = buf.toString().replaceAll("\\{request\\}", ctx.getResourceContext().getOriginalRequest().getParameter("request"));
			ctx.getCurrent().characters(result, 0, result.length());
		}

		@Override
		public void characters(CharSequence csq, int start, int end) {
			buf.append(csq, start, end);
		}
	}

	private static class SimpleElement implements Element {
		private boolean closed;

		public SimpleElement() { }

		public boolean isClosed() { return closed; }

		public void onTagStart(String tag, ParserContext ctx) {
			closed = tag.endsWith("/>");
		}
		public void onTagEnd(String tag, ParserContext ctx) throws IOException { }

		public void characters(CharSequence csq, int start, int end) { }

		public boolean onError(Exception e, ParserContext ctx) { return false; }

	}
}
