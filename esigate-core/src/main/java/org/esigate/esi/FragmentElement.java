package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class FragmentElement extends BaseElement {
	public final static ElementType TYPE = new BaseElementType("<esi:fragment", "</esi:fragment") {
		public FragmentElement newInstance() {
			return new FragmentElement();
		}

	};

	private EsiFragmentRenderer esiFragmentRenderer;
	private boolean nameMatches;

	FragmentElement() { }

	@Override
	public void onTagEnd(String tag, ParserContext ctx) {
		if (nameMatches) {
			esiFragmentRenderer.setWrite(false);
		}
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String name = tag.getAttribute("name");
		this.esiFragmentRenderer = ctx.findAncestor(EsiFragmentRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(esiFragmentRenderer.getName());
		if (nameMatches) {
			esiFragmentRenderer.setWrite(true);
		}
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		esiFragmentRenderer.append(csq, start, end);
	}
}
