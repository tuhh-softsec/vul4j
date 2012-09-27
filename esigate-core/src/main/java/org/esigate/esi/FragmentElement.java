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

	private EsiRenderer esiRenderer;
	private boolean nameMatches;
	private CharSequence replacement = null;
	private boolean initialStateWrite = false;

	FragmentElement() {
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException {
		if (replacement != null) {
			esiRenderer.setWrite(true);
			characters(replacement, 0, replacement.length());
		}
		esiRenderer.setWrite(initialStateWrite);
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String name = tag.getAttribute("name");
		esiRenderer = ctx.findAncestor(EsiRenderer.class);
		initialStateWrite = esiRenderer.isWrite();
		// If name matches, start writing
		if (esiRenderer != null) {
			nameMatches = name.equals(esiRenderer.getName());
			if (nameMatches) {
				esiRenderer.setWrite(true);
			} else if (esiRenderer.getFragmentsToReplace() != null) {
				replacement = esiRenderer.getFragmentsToReplace().get(name);
				if (replacement != null)
					esiRenderer.setWrite(false);
			}
		}
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		esiRenderer.append(csq, start, end);
	}
}
