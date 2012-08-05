/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2009 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An iterator over the child elements of a given element, filtered by a set of
 * element names.
 * 
 * @author Michiel Hendriks
 */
public class FilteredElementIterator implements Iterator<Element> {

	/**
	 * Defines a filter used by this iterator
	 * 
	 * @author Michiel Hendriks
	 */
	public interface Acceptor {
		/**
		 * Accept or reject an element
		 * 
		 * @param elm
		 *            The current element
		 * @return True if the element is accepted
		 */
		boolean accepted(Element elm);
	}

	/**
	 * Acceptor that accepts elements based on the tag name
	 */
	public static class TagNameAcceptor implements Acceptor {
		protected Set<String> names;

		public TagNameAcceptor(Set<String> acceptNames) {
			if (acceptNames == null) {
				throw new NullPointerException("Set can not be null");
			}
			names = new HashSet<String>(acceptNames);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.xslthl.FilteredElementIterator.Acceptor#accepted(org.w3c.dom
		 * .Element)
		 */
		public boolean accepted(Element elm) {
			return names.contains(elm.getTagName());
		}

	}

	/**
	 * The current element
	 */
	protected Element base;

	/**
	 * The current element
	 */
	protected Element current;

	/**
	 * The used acceptor for filtering
	 */
	protected Acceptor acceptor;

	/**
	 * If true a new value should be retrieved
	 */
	protected boolean dirty = true;

	/**
	 * Will be true if the search reached an end
	 */
	protected boolean finished;

	public FilteredElementIterator(Element baseElement, Acceptor filter) {
		if (baseElement == null) {
			throw new NullPointerException("Base Element can not be null");
		}
		if (filter == null) {
			throw new NullPointerException("Acceptor can not be null");
		}
		acceptor = filter;
		base = baseElement;
		dirty = true;
	}

	/**
	 * Creates a filtered element iterator that accepts elements with a given
	 * tag name
	 * 
	 * @param baseElement
	 * @param acceptTagNames
	 */
	public FilteredElementIterator(Element baseElement,
	        Set<String> acceptTagNames) {
		this(baseElement, new TagNameAcceptor(acceptTagNames));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (!finished && dirty) {
			// not finished, and the current value is dirty
			getNextElement();
		}
		return current != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public Element next() {
		if (!finished && dirty) {
			// if dirty first get a new value
			getNextElement();
		}
		if (finished) {
			// no more elements
			throw new NoSuchElementException();
		}
		dirty = true;
		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException("Elements can not be removed");
	}

	/**
	 * Get the next valid element
	 */
	protected void getNextElement() {
		assert !finished : "Should not call getNextElements on a finished list";
		if (current == null) {
			current = getAcceptedElement(base.getFirstChild());
		} else {
			current = getAcceptedElement(current.getNextSibling());
		}
		// iteration is finished when the current element is null
		finished = current == null;
		dirty = false;
	}

	/**
	 * Get the next accepted element. Will continue searching for the next
	 * sibling nodes.
	 * 
	 * @param node
	 *            The first node to test
	 * @return The accepted element, or null
	 */
	protected Element getAcceptedElement(Node node) {
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE
			        && node instanceof Element) {
				Element result = (Element) node;
				if (acceptor.accepted(result)) {
					return result;
				}
			}
			node = node.getNextSibling();
		}
		return null;
	}
}
