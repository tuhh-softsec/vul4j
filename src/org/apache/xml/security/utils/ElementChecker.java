package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;

public interface ElementChecker {
	 /**
	  * Check that the elemnt is the one expect
	  *
	  * @throws XMLSecurityException
	  */
	   void guaranteeThatElementInCorrectSpace(ElementProxy expected, Element actual)
	           throws XMLSecurityException;
}
