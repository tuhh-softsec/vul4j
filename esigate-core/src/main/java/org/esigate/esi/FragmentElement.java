package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class FragmentElement implements Element {
	public final static ElementType TYPE = new BaseElementType("<esi:fragment", "</esi:fragment") {
		public FragmentElement newInstance() {
			return new FragmentElement();
		}

	};

	private EsiFragmentRenderer esiFragmentRenderer;
	private boolean nameMatches;

	public void doEndTag(String tag) {
		// Stop writing
		if (nameMatches) {
			esiFragmentRenderer.setWrite(false);
		}
	}

	public void doStartTag(String tagString, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag tag = new Tag(tagString);
		String name = tag.getAttributes().get("name");
		this.esiFragmentRenderer = stack.findAncestorWithClass(this, EsiFragmentRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(esiFragmentRenderer.getName());
		if (nameMatches) {
			esiFragmentRenderer.setWrite(true);
		}
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		esiFragmentRenderer.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		esiFragmentRenderer.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		esiFragmentRenderer.append(csq, start, end);
		return this;
	}

}
