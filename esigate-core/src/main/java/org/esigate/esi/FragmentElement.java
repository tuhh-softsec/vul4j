package org.esigate.esi;

import java.io.IOException;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

class FragmentElement extends BaseElement {
	public final static ElementType TYPE = new BaseElementType("<esi:fragment", "</esi:fragment") {
		public FragmentElement newInstance() {
			return new FragmentElement();
		}

	};

	private EsiFragmentRenderer esiFragmentRenderer;
	private boolean nameMatches;

	FragmentElement() {
		super(TYPE);
	}

	@Override
	public void doEndTag(String tag) {
		// Stop writing
		if (nameMatches) {
			esiFragmentRenderer.setWrite(false);
		}
	}

	@Override
	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) {
		String name = tag.getAttribute("name");
		this.esiFragmentRenderer = stack.findAncestorWithClass(this, EsiFragmentRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(esiFragmentRenderer.getName());
		if (nameMatches) {
			esiFragmentRenderer.setWrite(true);
		}
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		esiFragmentRenderer.append(csq);
		return this;
	}

	@Override
	public Appendable append(char c) throws IOException {
		esiFragmentRenderer.append(c);
		return this;
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		esiFragmentRenderer.append(csq, start, end);
		return this;
	}

}
