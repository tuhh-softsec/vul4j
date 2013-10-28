package org.esigate.aggregator;

import java.io.IOException; 

import org.esigate.parser.Element;

class Adapter implements Appendable {
	private final Element adaptable;

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		adaptable.characters(csq, start, end);
		return this;
	}

	@Override
	public Appendable append(char c) throws IOException {
		return append(new StringBuilder(1).append(c), 0, 1);
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		return append(csq, 0, csq.length());
	}

	public Adapter(Element adaptable) {
		this.adaptable = adaptable;
	}
}