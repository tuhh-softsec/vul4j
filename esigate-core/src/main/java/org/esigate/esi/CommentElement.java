package org.esigate.esi;

import org.esigate.parser.ElementType;

/**
 * The comment element allows developers to comment their ESI instructions, without making the comments available in the
 * processor's output. comment is an empty element, and must not have an end tag.
 * 
 * @author Francois-Xavier Bonnet
 * @see <a href="http://www.w3.org/TR/esi-lang">ESI Language Specification 1.0</a>
 * 
 */
class CommentElement extends BaseElement {
	public final static ElementType TYPE = new BaseElementType("<esi:comment", "</esi:comment") {
		public CommentElement newInstance() {
			return new CommentElement();
		}

	};

	CommentElement() {
		super(TYPE);
	}
}
