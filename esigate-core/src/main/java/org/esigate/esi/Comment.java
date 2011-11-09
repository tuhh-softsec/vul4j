package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
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
public class Comment implements Element {
	public final static ElementType TYPE = new BaseElementType("<!--esi", "-->") {
		public Comment newInstance() {
			return new Comment();
		}

	};

	private Appendable parent;

	public void doEndTag(String tag) throws IOException {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack) throws IOException, HttpErrorPage {
		this.parent = parent;
	}

	public ElementType getType() {
		return TYPE;
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
