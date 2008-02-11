package org.apache.xml.security.samples;

import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SampleUtils {

    /**
     * Method createDSctx
     *
     * @param doc
     * @param prefix
     * @param namespace
     * @return the element.
     */
    public static Element createDSctx
	(Document doc, String prefix, String namespace) {
	
	if ((prefix == null) || (prefix.trim().length() == 0)) {
            throw new IllegalArgumentException("You must supply a prefix");
	}
	
	Element ctx = doc.createElementNS(null, "namespaceContext");
	
	ctx.setAttributeNS
	    (Constants.NamespaceSpecNS, "xmlns:" + prefix.trim(), namespace);
	
	return ctx;
    }
}
