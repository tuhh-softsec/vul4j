package org.apache.xml.security.algorithms.encryption.implementations.BC;

import org.apache.xml.security.utils.EncryptionConstants;

/**
 *
 * @author $Author$
 */
public class BlockEncryptionImpl_TRIPLEDES_BC extends BlockEncryptionImpl {
  public int engineGetIvLength() {
    return this.engineGetBlockSize();
  }
  public String getImplementedAlgorithmURI() {
     return EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES;
  }
   public String getRequiredProviderName() {
      return "BC";
   }
}