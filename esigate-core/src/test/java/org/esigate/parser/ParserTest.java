package org.esigate.parser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.test.MockHttpServletRequest;

public class ParserTest extends TestCase {
	private Parser tested;

	@Override
	protected void setUp() {
		tested = new Parser(Pattern.compile("(<test:[^>]*>)|(</test:[^>]*>)"), SIMPLE, BODY);
		MockHttpServletRequest request = new MockHttpServletRequest("http://a.b?request=updated");
		tested.setRequest(request);
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
		public Element newInstance() { return new SimpleElement(this); }
	};
	private static final ElementType BODY = new MockElementType("<test:body", "</test:body") {
		public Element newInstance() { return new BodyElement(this); }
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
	private static class BodyElement extends SimpleElement implements BodyTagElement {
		private HttpServletRequest request;

		public BodyElement(ElementType type) { super(type); }

		public void setRequest(HttpServletRequest request) { this.request = request; }

		public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException {
			String result = body.replaceAll("\\{request\\}", request.getParameter("request"));
			out.append(result);
		}

	}

	private static class SimpleElement implements Element {
		private ElementType type;
		private boolean closed;

		public SimpleElement(ElementType type) {
			this.type = type;
		}

		public ElementType getType() { return type; }

		public boolean isClosed() { return closed; }

		public void doStartTag(String tag, Appendable parent, ElementStack stack) {
			closed = tag.endsWith("/>");
		}
		public void doEndTag(String tag) { }

		public Appendable append(CharSequence csq) { return this; }
		public Appendable append(CharSequence csq, int start, int end) { return this; }
		public Appendable append(char c) { return this; }

	}
}
