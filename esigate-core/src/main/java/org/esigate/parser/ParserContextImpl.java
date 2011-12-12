package org.esigate.parser;

import java.io.IOException;
import java.util.Stack;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;

/**
 * 
 * The stack of tags corresponding to the current position in the document
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
class ParserContextImpl implements ParserContext {
	private final RootAdapter root;
	private final ResourceContext resourceContext;

	private final Stack<Pair> stack = new Stack<Pair>();

	ParserContextImpl(Appendable root, ResourceContext resourceContext) {
		this.root = new RootAdapter(root);
		this.resourceContext = resourceContext;
	}

	public <T> T findAncestor(Class<T> type) {
		T result = null;
		for (int i = stack.size() - 1; i > -1; i--) {
			Element currentElement = stack.elementAt(i).element;
			if (type.isInstance(currentElement)) {
				result = type.cast(currentElement);
				break;
			}
		}
		// try with root
		if (result == null && type.isInstance(root.root)) {
			result = type.cast(root.root);
		}

		return result;
	}

	/** {@inheritDoc} */
	public boolean reportError(Exception e) {
		boolean result = false;
		for (int i = stack.size() - 1; i > -1; i--) {
			Element element = stack.elementAt(i).element;
			if (element.onError(e, this)) {
				result= true;
				break;
			}
		}
		return result;
	}

	void startElement(ElementType type, Element element, String tag) throws IOException, HttpErrorPage {
		element.onTagStart(tag, this);
		stack.push(new Pair(type, element));
	}
	void endElement(String tag) throws IOException, HttpErrorPage {
		Element element = stack.pop().element;
		element.onTagEnd(tag, this);
	}
	boolean isCurrentTagEnd(String tag) {
		return !stack.isEmpty() && stack.peek().type.isEndTag(tag);
	}

	/** Writes characters into current writer. */
	void characters(CharSequence cs) throws IOException {
		characters(cs, 0, cs.length());
	}
	/** Writes characters into current writer. */
	void characters(CharSequence csq, int start, int end) throws IOException {
		getCurrent().characters(csq, start, end);
	}

	public Element getCurrent() {
		return (!stack.isEmpty()) ? stack.peek().element : root;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}

	private static class Pair {
		private final ElementType type;
		private final Element element;

		public Pair(ElementType type, Element element) {
			this.type = type;
			this.element = element;
		}
	}
	
	private static class RootAdapter implements Element {
		private final Appendable root;

		public RootAdapter(Appendable root) {
			this.root = root;
		}

		public void onTagStart(String tag, ParserContext ctx) { }

		public void onTagEnd(String tag, ParserContext ctx) { }

		public boolean onError(Exception e, ParserContext ctx) { return false; }

		public void characters(CharSequence csq, int start, int end) throws IOException {
			root.append(csq, start, end);
		}

		public boolean isClosed() { return false; }
	}
}
