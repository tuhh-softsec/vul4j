package org.apache.xml.security.encryption;

import org.apache.xml.security.encryption.type.ReferenceType;
import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * This class maps to the <CODE>xenc:KeyReference</CODE> element.
 *
 * @author $Author$
 */
public class KeyReference extends EncryptionElementProxy implements ReferenceType {

   public KeyReference(Document doc, String URI) {
      super(doc, EncryptionConstants._TAG_KEYREFERENCE);
      this._constructionElement.setAttribute(EncryptionConstants._ATT_URI, URI);
   }

   public KeyReference(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_KEYREFERENCE);
   }

   /**
    * Returns the <code>URI</code> attribute
    *
    * @return the <code>URI</code> attribute
    */
   public String getURI() {
      return this._constructionElement.getAttribute(EncryptionConstants._ATT_URI);
   }
}