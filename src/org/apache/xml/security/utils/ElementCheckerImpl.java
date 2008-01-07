package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;

public class ElementCheckerImpl {
	/** A checker for DOM that interns NS */
	public static class InternedNsChecker implements ElementChecker {
		public void guaranteeThatElementInCorrectSpace(ElementProxy expected,
				Element actual) throws XMLSecurityException {

		      String localnameSHOULDBE = expected.getBaseLocalName();
		      String namespaceSHOULDBE = expected.getBaseNamespace();
		      
		      String localnameIS = actual.getLocalName();
		      String namespaceIS = actual.getNamespaceURI();
		      if ((namespaceSHOULDBE!=namespaceIS) ||
		       !localnameSHOULDBE.equals(localnameIS) ) {      
		         Object exArgs[] = { namespaceIS +":"+ localnameIS, 
		           namespaceSHOULDBE +":"+ localnameSHOULDBE};
		         throw new XMLSecurityException("xml.WrongElement", exArgs);
		      }			
		}		
	}
	
	/** A checker for DOM that interns NS */
	public static class FullChecker implements ElementChecker {
		public void guaranteeThatElementInCorrectSpace(ElementProxy expected,
				Element actual) throws XMLSecurityException {

		      String localnameSHOULDBE = expected.getBaseLocalName();
		      String namespaceSHOULDBE = expected.getBaseNamespace();
		      
		      String localnameIS = actual.getLocalName();
		      String namespaceIS = actual.getNamespaceURI();
		      if ((!namespaceSHOULDBE.equals(namespaceIS)) ||
		       !localnameSHOULDBE.equals(localnameIS) ) {      
		         Object exArgs[] = { namespaceIS +":"+ localnameIS, 
		           namespaceSHOULDBE +":"+ localnameSHOULDBE};
		         throw new XMLSecurityException("xml.WrongElement", exArgs);
		      }			
		}		
	}
	
	/** An empty checker if schema checking is used */
	public static class EmptyChecker implements ElementChecker {
		public void guaranteeThatElementInCorrectSpace(ElementProxy expected,
				Element actual) throws XMLSecurityException {
		}		
	}
}
