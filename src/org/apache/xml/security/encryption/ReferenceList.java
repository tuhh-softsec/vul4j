package org.apache.xml.security.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * This class maps to the <CODE>xenc:ReferenceList</CODE> element.
 *
 * @author $Author$
 */
public class ReferenceList extends EncryptionElementProxy {

   public ReferenceList(Document doc) {
      super(doc, EncryptionConstants._TAG_REFERENCELIST);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   public ReferenceList(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_REFERENCELIST);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   public void add(DataReference dataReference) {
      this._constructionElement.appendChild(dataReference.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   public void add(KeyReference keyReference) {
      this._constructionElement.appendChild(keyReference.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   public int getLengthDataReference() {
      return 0;
   }

   public int getLengthKeyReference() {
      return 0;
   }

   public DataReference itemDataReference(int i) {
      return null;
   }

   public KeyReference itemKeyReference(int i) {
      return null;
   }
}