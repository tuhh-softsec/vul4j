package net.webassembletool.xml;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

class HtmlNamespaceContext implements NamespaceContext {

	public Iterator<String> getPrefixes(String s) {
		return null;
	}

	public String getPrefix(String s) {
		return null;
	}

	public String getNamespaceURI(String s) {
		if ("html".equals(s))
			return "http://www.w3.org/1999/xhtml";
		return null;
	}

}
