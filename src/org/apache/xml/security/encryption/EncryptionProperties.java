package org.apache.xml.security.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * This class maps to the <CODE>xenc:EncryptionProperties</CODE> element.
 *
 * @author $Author$
 */
public class EncryptionProperties extends EncryptionElementProxy {

   public EncryptionProperties(Document doc) {
      super(doc, EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
   }

   public EncryptionProperties(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
   }

   public void add(EncryptionProperty encryptionProperty) {
      ;
   }

   public int getLength() {
      NodeList nl = this._constructionElement.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS, EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
      return nl.getLength();
   }

   public EncryptionProperty item(int i) throws XMLSecurityException {
      NodeList nl = this._constructionElement.getElementsByTagNameNS(EncryptionConstants.EncryptionSpecNS, EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
      return new EncryptionProperty((Element)nl.item(i), this._baseURI);
   }
}