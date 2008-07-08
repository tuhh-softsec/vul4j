package net.sf.xslthl;

import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class Params {

    private Element paramElem;

    interface ParamsLoader<T> {
	T load(Params params);
    }

    Params(Element paramElem) {
	this.paramElem = paramElem;
    }

    String getParam(String name) {
	return paramElem.getElementsByTagName(name).item(0).getTextContent()
		.trim();
    }

    String getParam() {
	return paramElem.getTextContent().trim();
    }

    String getParam(String name, String defaultValue) {
	NodeList nodes = paramElem.getElementsByTagName(name);
	if (nodes.getLength() == 0) {
	    return defaultValue;
	}
	return nodes.item(0).getTextContent().trim();
    }

    Params getParams(String name) {
	return new Params((Element) paramElem.getElementsByTagName(name)
		.item(0));
    }

    boolean isSet(String name) {
	return paramElem.getElementsByTagName(name).getLength() > 0;
    }

    void load(String name, Collection<String> list) {
	NodeList nodes = paramElem.getElementsByTagName(name);
	for (int i = 0; i < nodes.getLength(); i++) {
	    Element elem = (Element) nodes.item(i);
	    list.add(elem.getTextContent().trim());
	}
    }

    <T> void load(String name, Collection<T> list,
	    ParamsLoader<? extends T> loader) {
	NodeList nodes = paramElem.getElementsByTagName(name);
	for (int i = 0; i < nodes.getLength(); i++) {
	    Element elem = (Element) nodes.item(i);
	    list.add(loader.load(new Params(elem)));
	}
    }
}
