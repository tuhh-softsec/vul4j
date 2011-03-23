package net.webassembletool.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.BodyTagElement;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;
import net.webassembletool.vars.VariablesResolver;

public class OtherwiseElement implements BodyTagElement {

	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<esi:otherwise");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("</esi:otherwise");
		}

		public Element newInstance() {
			return new OtherwiseElement();
		}

	};

	private boolean closed = false;
	private HttpServletRequest request;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {
		Tag whenTag = new Tag(tag);
		closed = whenTag.isOpenClosed();
	}

	public ElementType getType() {
		return TYPE;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(char c) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		// Just ignore tag body
		return this;
	}

	public void doAfterBody(String body, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {

		Element e = stack.pop();
		Appendable parent = stack.getCurrentWriter();

		if (e instanceof ChooseElement && !((ChooseElement) e).isCondition()) {
			String result = VariablesResolver
					.replaceAllVariables(body, request);
			parent.append(result);
		}
		stack.push(e);
	}

}
