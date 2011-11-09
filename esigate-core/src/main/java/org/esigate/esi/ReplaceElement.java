package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

// FIXME: [saber] NYI
public class ReplaceElement implements Element {

	public final static ElementType TYPE = new BaseElementType("<esi:replace", "</esi:replace") {
		public ReplaceElement newInstance() {
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

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
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

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// TODO
		return this;
	}

}
