package org.apache.xml.security.algorithms.encryption;

import org.w3c.dom.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.exceptions.XMLSecurityException;

/**
 * This class is used for constructing block encryption algorithms.
 *
 * @author $Author$
 */
public class BlockEncryptionAlgorithm extends EncryptionMethod {
   public BlockEncryptionAlgorithm(Document doc, String AlgorithmURI)
           throws XMLSecurityException {

      super(doc, AlgorithmURI);
   }
}