package org.apache.xml.security.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.Base64;

/**
 * This class maps to the <CODE>xenc:CipherValue</CODE> element.
 *
 * @author $Author$
 */
public class CipherValue extends EncryptionElementProxy {

   public CipherValue(Document doc, byte ciphertext[]) {
      super(doc, EncryptionConstants._TAG_CIPHERVALUE);
      this.setCipherText(ciphertext);
   }

   public CipherValue(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_CIPHERVALUE);
   }

   public byte[] getCipherText() throws XMLSecurityException {
      NodeList nl = this._constructionElement.getChildNodes();
      if (nl.getLength() != 1 || nl.item(0).getNodeType() != Node.TEXT_NODE) {
         throw new XMLSecurityException("encryption.structure.CipherTextMustContainText");
      }
      Text t = (Text) nl.item(0);
      return Base64.decode(t.getData());
   }

   public void setCipherText(byte ciphertext[]) throws XMLSecurityException {
      while (this._constructionElement.hasChildNodes()) {
         this._constructionElement.removeChild(this._constructionElement.getLastChild());
      }
      Text textNode = this._doc.createTextNode(Base64.encode(ciphertext));
      this._constructionElement.appendChild(textNode);
   }
}