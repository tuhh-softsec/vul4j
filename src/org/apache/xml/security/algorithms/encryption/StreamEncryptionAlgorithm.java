package org.apache.xml.security.algorithms.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 *
 * @author $Author$
 */
public class StreamEncryptionAlgorithm extends EncryptionMethod {

  public StreamEncryptionAlgorithm(Document doc, String AlgorithmURI, int keySize)
           throws XMLSecurityException {

      super(doc, AlgorithmURI);
      this._keySize = keySize;
   }
}