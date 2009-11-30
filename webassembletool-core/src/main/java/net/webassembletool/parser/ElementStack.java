package net.webassembletool.parser;

import java.util.Stack;

/**
 * 
 * The stack of tags corresponding to the current position in the document
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ElementStack {
	private final Appendable root;
	private final Stack<Element> stack = new Stack<Element>();

	ElementStack(Appendable root) {
		this.root = root;
	}

	/**
	 * @param <T>
	 * @param from
	 * @param type
	 * @return The first matching upper Element in the stack
	 */
	@SuppressWarnings("unchecked")
	public <T extends Appendable> T findAncestorWithClass(Element from,
			Class<T> type) {
		boolean fromFound = false;
		for (int i = stack.size() - 1; i > -1; i--) {
			Element currentElement = stack.elementAt(i);
			if (fromFound && currentElement.getClass() == type)
				return (T) currentElement;
			if (currentElement == from)
				fromFound = true;
		}
		if (fromFound && root.getClass() == type)
			return (T) root;
		return null;
	}

	/**
	 * @return True if the stack is empty
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	/**
	 * @return The top element in the stack and removes it.
	 */
	public Element peek() {
		return stack.peek();
	}

	/**
	 * @return The tom element in the stack
	 */
	public Element pop() {
		return stack.pop();
	}

	/**
	 * Adds an Element on the top of the stack
	 * 
	 * @param element
	 *            The Element to add
	 */
	public void push(Element element) {
		stack.push(element);
	}

	/**
	 * @return the top element of the stack or the root writer if the stack is
	 *         empty.
	 */
	public Appendable getCurrentWriter() {
		if (stack.isEmpty())
			return root;
		else
			return stack.peek();

	}

}
