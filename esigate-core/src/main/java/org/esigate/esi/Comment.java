package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

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
		@Override
		public Comment newInstance() {
			return new Comment();
		}

	};

	Comment() { }

	@Override
	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		// do not try to parse tag string
		super.onTagStart("<esi!-->", ctx);
	}
}
