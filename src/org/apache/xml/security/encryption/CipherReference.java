package org.apache.xml.security.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * This class maps to the <CODE>xenc:CipherReference</CODE> element.
 *
 * @author $Author$
 */
public class CipherReference extends EncryptionElementProxy {
   private Transforms _transforms = null;

   public CipherReference(Document doc, String URI) {
      super(doc, EncryptionConstants._TAG_CIPHERREFERENCE);
      this._constructionElement.setAttribute(EncryptionConstants._ATT_URI, URI);
   }

   public CipherReference(Document doc, String URI, Transforms transforms) {
      super(doc, EncryptionConstants._TAG_CIPHERREFERENCE);
      this._constructionElement.setAttribute(EncryptionConstants._ATT_URI, URI);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      this._constructionElement.appendChild(transforms.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      this._transforms = transforms;
   }

   public CipherReference(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_CIPHERREFERENCE);
   }

   public String getURI() {
      return this._constructionElement.getAttribute(EncryptionConstants._ATT_URI);
   }

   public Transforms getTransforms() {
      if (this._state == EncryptionElementProxy.MODE_CREATE) {
         return this._transforms;
      } else {
         // search for xenc:Transforms and create
         // return new Transforms(el, this._baseURI);
         return null;
      }
   }
}