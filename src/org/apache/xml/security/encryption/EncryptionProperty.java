package org.apache.xml.security.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.IdResolver;

/**
 * This class maps to the <CODE>xenc:EncryptionProperty</CODE> element.
 *
 * @author $Author$
 */
public class EncryptionProperty extends EncryptionElementProxy {

   public EncryptionProperty(Document doc) {
      super(doc, EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
   }

   public EncryptionProperty(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI, EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
    */
   public void setId(String Id) {

      if ((this._state == MODE_CREATE) && (Id != null) && (Id.length() != 0)) {
         this._constructionElement.setAttribute(EncryptionConstants._ATT_ID, Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute
    *
    * @return the <code>Id</code> attribute
    */
   public String getId() {
      return this._constructionElement.getAttribute(EncryptionConstants._ATT_ID);
   }

   /**
    * Sets the <code>Target</code> attribute
    *
    * @param Target
    */
   public void setTarget(String Target) {

      if ((this._state == MODE_CREATE) && (Target != null)) {
         this._constructionElement.setAttribute(EncryptionConstants._ATT_TARGET, Target);
      }
   }

   /**
    * Returns the <code>Target</code> attribute
    *
    * @return the <code>Target</code> attribute
    */
   public String getTarget() {
      return this._constructionElement.getAttribute(EncryptionConstants._ATT_TARGET);
   }
}