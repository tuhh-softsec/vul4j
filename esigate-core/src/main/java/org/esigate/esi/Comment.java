package org.esigate.esi;

import java.io.IOException;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

/**
 * This is a special construct to allow HTML marked up with ESI to render without processing. ESI Processors will remove
 * the start ("&lt;!--esi") and end ("--&gt;") when the page is processed, while still processing the contents. If the
 * page is not processed, it will remain, becoming an HTML/XML comment tag.
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
class Comment extends BaseElement {
	public final static ElementType TYPE = new BaseElementType("<!--esi", "-->") {
		public Comment newInstance() {
			return new Comment();
		}

	};

	private Appendable parent;

	Comment() {
		super(TYPE);
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack) {
		this.parent = parent;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		parent.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		parent.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		parent.append(csq, start, end);
		return this;
	}

}
