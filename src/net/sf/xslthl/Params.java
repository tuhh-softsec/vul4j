/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
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

import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Syntax highlighter parameters
 */
public class Params {

	public interface ParamsLoader<T> {
		T load(Params params) throws HighlighterConfigurationException;
	}

	protected Element paramElem;

	public Params(Element paramElem) {
		this.paramElem = paramElem;
	}

	/**
	 * Return the text content for the given element. This is different from the
	 * Element.getTextContect() function in a way that normal text nodes are
	 * trimmed and CDATA is used AS IS
	 * 
	 * @param elm
	 * @return
	 */
	protected String getTextContent(Node elm) {
		if (elm == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		NodeList list = elm.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (sb.length() > 0) {
				sb.append(' ');
			}
			if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
				sb.append(n.getTextContent());
			} else {
				sb.append(n.getTextContent().trim());
			}
		}
		return sb.toString();
	}

	/**
	 * Get the current element as value
	 * 
	 * @return
	 */
	public String getParam() {
		return getTextContent(paramElem);
	}

	/**
	 * Get a single parameter. Returns null when the parameter doesn't exist.
	 * 
	 * @param name
	 * @return
	 */
	public String getParam(String name) {
		return getParam(name, null);
	}

	/**
	 * Get a single parameter with a default value.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getParam(String name, String defaultValue) {
		NodeList nodes = paramElem.getElementsByTagName(name);
		if (nodes.getLength() == 0) {
			return defaultValue;
		}
		return getTextContent(nodes.item(0));
	}

	/**
	 * @param name
	 * @return
	 */
	public Params getParams(String name) {
		return new Params((Element) paramElem.getElementsByTagName(name)
		        .item(0));
	}

	/**
	 * Return true if a parameter with the given name exists.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isSet(String name) {
		return paramElem.getElementsByTagName(name).getLength() > 0;
	}

	/**
	 * Load multiple parameters into a list
	 * 
	 * @param name
	 * @param list
	 */
	public void getMutliParams(String name, Collection<String> list) {
		NodeList nodes = paramElem.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			Element elem = (Element) nodes.item(i);
			list.add(getTextContent(elem));
		}
	}

	/**
	 * Get the parameters using a specialized loader
	 * 
	 * @param <T>
	 * @param name
	 * @param list
	 * @param loader
	 * @throws HighlighterConfigurationException
	 */
	public <T> void getMultiParams(String name, Collection<T> list,
	        ParamsLoader<? extends T> loader)
	        throws HighlighterConfigurationException {
		NodeList nodes = paramElem.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			Element elem = (Element) nodes.item(i);
			list.add(loader.load(new Params(elem)));
		}
	}
}
