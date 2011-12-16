package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.vars.VariablesResolver;

/**
 * Support for &lt;esi:replace&gt; element inside of parent &lt;esi:include&gt;
 * @author <a href="stanislav.bernatskyi@smile.fr">Stanislav Bernatskyi</a>
 */
class ReplaceElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:replace", "</esi:replace") {
		public ReplaceElement newInstance() {
			return new ReplaceElement();
		}

	};

	private StringBuilder buf = null;
	private String fragment;
	private String regexp;

	@Override
	public void characters(CharSequence csq, int start, int end) {
		buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException {
		IncludeElement parent = ctx.findAncestor(IncludeElement.class);
		String result = VariablesResolver.replaceAllVariables(buf.toString(), ctx.getResourceContext().getOriginalRequest());
		if (fragment != null) {
			parent.addFragmentReplacement(fragment, (CharSequence) result);
		} else if (regexp != null) {
			parent.addRegexpReplacement(regexp, (CharSequence) result);
		} else {
			parent.characters(result, 0, result.length());
		}

		buf = null;
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		buf = new StringBuilder();
		fragment = tag.getAttribute("fragment");
		regexp = tag.getAttribute("regexp");
		if ((fragment == null && regexp == null) || (fragment != null && regexp != null)) {
			throw new IllegalArgumentException("only one of 'fragment' and 'regexp' attributes is allowed");
		}
	}

}
