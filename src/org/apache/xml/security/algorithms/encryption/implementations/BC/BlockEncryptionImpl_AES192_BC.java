package org.apache.xml.security.algorithms.encryption.implementations.BC;

import org.apache.xml.security.utils.EncryptionConstants;

/**
 *
 * @author $Author$
 */
public class BlockEncryptionImpl_AES192_BC extends BlockEncryptionImpl {
  public int engineGetIvLength() {
    return 16;
  }
  public String getImplementedAlgorithmURI() {
     return EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192;
  }
   public String getRequiredProviderName() {
      return "BC";
   }
}