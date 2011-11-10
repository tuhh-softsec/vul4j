package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

abstract class BaseElement implements Element {
	private ElementType type;
	private boolean closed = false;

	protected BaseElement(ElementType type) {
		this.type = type;
	}

	public Appendable append(CharSequence csq) throws IOException {
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		return this;
	}

	public Appendable append(char c) throws IOException {
		return this;
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack) throws IOException, HttpErrorPage {
		Tag tagObj = Tag.create(tag);
		closed = tagObj.isOpenClosed();
		parseTag(tagObj, parent, stack);
	}

	/** Additional tag initialization callback. */
	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) throws IOException, HttpErrorPage {
	}

	public void doEndTag(String tag) throws IOException, HttpErrorPage {
	}

	public ElementType getType() {
		return type;
	}

	public boolean isClosed() {
		return closed;
	}

}
