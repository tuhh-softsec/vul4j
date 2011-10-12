package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class ReplaceElement implements Element {

	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<esi:fragment");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("</esi:fragment");
		}

		public Element newInstance() {
			return new ReplaceElement();
		}

	};

	private final boolean closed = false;

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {
		// TODO
	}

	public ElementType getType() {
		return TYPE;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// TODO
		return this;
	}

	public Appendable append(char c) throws IOException {
		// TODO
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		// TODO
		return this;
	}

}
