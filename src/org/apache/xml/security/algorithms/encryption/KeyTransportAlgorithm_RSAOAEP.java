package org.apache.xml.security.algorithms.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 *
 * @author $Author$
 */
public class KeyTransportAlgorithm_RSAOAEP extends EncryptionMethod {

  public KeyTransportAlgorithm_RSAOAEP(Document doc, String AlgorithmURI, String digestAlgorithm, byte[] params)
           throws XMLSecurityException {

      super(doc, AlgorithmURI);
      this._OAEPdigestAlgorithm = MessageDigestAlgorithm.getInstance(doc, digestAlgorithm);
      this._OAEPparams = params;
  }
}