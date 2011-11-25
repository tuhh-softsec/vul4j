package org.esigate.esi;

import java.io.IOException;
import java.util.Map;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

/**
 * Fragment repalcement element which is used to replace &lt;esi:fragment/&gt; elements inside content.
 * @author <a href="stanislav.bernatskyi@smile.fr">Stanislav Bernatskyi</a>
 */
class FragmentReplacementElement extends BaseElement {
	private final Map<String, CharSequence> replacements;
	private String currentReplacement;

	public static ElementType createType(final Map<String, CharSequence> replacements) {
		return new BaseElementType("<esi:fragment", "</esi:fragment") {
			public FragmentReplacementElement newInstance() {
				return new FragmentReplacementElement(replacements);
			}
		};
	}

	FragmentReplacementElement(Map<String, CharSequence> replacements) {
		this.replacements = replacements;
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException {
		characters(tag, 0, tag.length());
		currentReplacement = null;
	}

	@Override
	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		super.onTagStart(tag, ctx);

		Tag tagObj = Tag.create(tag);
		String name = tagObj.getAttribute("name");
		if (replacements.containsKey(name)) {
			currentReplacement = name;
			CharSequence replacementBody = replacements.get(name);
			super.characters(replacementBody, 0, replacementBody.length());
		} else if (!tagObj.isOpenClosed()) {
			currentReplacement = null;
			super.characters(tag, 0, tag.length());
		}
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (currentReplacement == null) { // not in current fragment
			super.characters(csq, start, end);
		}
	}
}
