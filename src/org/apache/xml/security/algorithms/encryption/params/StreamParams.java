package org.apache.xml.security.algorithms.encryption.params;

import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;

/**
 *
 * @author $Author$
 */
public class StreamParams extends EncryptionMethodParams {

  int _keySize;

  public String getAlgorithmURI() {
     return null;
  }

  public StreamParams(int keySize) {
     this._keySize = keySize;
  }

  public int getKeySize () {
     return this._keySize;
  }

  public DocumentFragment createChildNodes(Document doc) throws XMLSecurityException {
     DocumentFragment nl = doc.createDocumentFragment();

     Element keySizeElem = XMLUtils.createElementInEncryptionSpace(doc, EncryptionConstants._TAG_KEYSIZE);
     Text keySizeText = doc.createTextNode(Integer.toString(this.getKeySize()));
     keySizeElem.appendChild(keySizeText);

     nl.appendChild(doc.createTextNode("\n"));
     nl.appendChild(keySizeElem);
     nl.appendChild(doc.createTextNode("\n"));

     return nl;
  }
}