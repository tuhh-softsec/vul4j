package org.apache.xml.security.encryption;

import org.apache.xml.security.encryption.type.ReferenceType;
import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * This class maps to the <CODE>xenc:DataReference</CODE> element.
 *
 * @author $Author$
 */
public class DataReference extends EncryptionElementProxy implements ReferenceType {

   public DataReference(Document doc, String URI) {
      super(doc, EncryptionConstants._TAG_DATAREFERENCE);
      this._constructionElement.setAttribute(EncryptionConstants._ATT_URI, URI);
   }

   public DataReference(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_DATAREFERENCE);
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