package org.apache.xml.security.algorithms.encryption.implementations.BC;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.algorithms.encryption.*;
import org.apache.xml.security.algorithms.encryption.params.*;
import org.apache.xml.security.algorithms.encryption.helper.AESWrapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.PRNG;
import org.w3c.dom.*;

/**
 *
 *
 *
 *
 * @author $Author$
 *
 */

public abstract class KeyWrapImpl_AES_BC extends EncryptionMethodSpi {

   /** Field _cipher           */
   Cipher _cipher;

   /**
    * Method getRequiredProviderName
    *
    * @return
    */
   public abstract String getRequiredProviderName();

   /**
    * Method getImplementedAlgorithmURI
    *
    * @return
    */
   public abstract String getImplementedAlgorithmURI();

   /**
    * Method getImplementedAlgorithmType
    *
    * @return
    */
   public int getImplementedAlgorithmType() {
      return EncryptionMethodSpi.ALGOTYPE_SYMMETRIC_KEY_WRAP;
   }

   /**
    * Method engineGetBlockSize
    *
    * @return
    */
   public int engineGetBlockSize() {
      return this._cipher.getBlockSize();
   }

   /**
    * Method engineGetIvLength
    *
    * @return
    */
   public abstract int engineGetIvLength();

   /**
    * Method engineInit
    *
    * @param doc
    * @param params
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public EncryptionMethodParams engineInit(
           Document doc, EncryptionMethodParams params)
              throws org.apache.xml.security.exceptions.XMLSecurityException {

      if (params != null) {
         throw new XMLSecurityException(
            "encryption.algorithmCannotEatInitParams");
      }

      try {
         this._cipher = Cipher.getInstance(this.getImplementedAlgorithmJCE(),
                                           this.getRequiredProviderName());
      } catch (NoSuchAlgorithmException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchProviderException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      }

      return params;
   }

   public EncryptionMethodParams engineInit(Element encryptionMethodElem)
              throws org.apache.xml.security.exceptions.XMLSecurityException {

      if (encryptionMethodElem.getChildNodes().getLength() != 0) {
         throw new XMLSecurityException(
            "encryption.algorithmCannotEatInitParams");
      }

      try {
         this._cipher = Cipher.getInstance(this.getImplementedAlgorithmJCE(),
                                           this.getRequiredProviderName());
      } catch (NoSuchAlgorithmException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchProviderException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      }

      return null;
   }

   /**
    * Method engineWrap
    *
    * @param contentKey
    * @param wrapKey
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public byte[] engineWrap(Key contentKey, Key wrapKey)
           throws org.apache.xml.security.exceptions.XMLSecurityException {
      return this.engineWrap(contentKey, wrapKey, null);
   }

   /**
    * Method engineWrap
    *
    * @param contentKey
    * @param wrapKey
    * @param IV
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public byte[] engineWrap(Key contentKey, Key wrapKey, byte[] IV)
           throws org.apache.xml.security.exceptions.XMLSecurityException {

         if (IV != null && IV.length != 8) {
            throw new XMLSecurityException("empty");
         }
         AESWrapper aw = new AESWrapper(this._cipher);

         return aw.wrap(contentKey, wrapKey, IV);
   }

   /**
    * Method engineUnwrap
    *
    * @param wrappedKey
    * @param wrapKey
    * @param wrappedKeyURI
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public Key engineUnwrap(byte[] wrappedKey, Key wrapKey, String wrappedKeyURI)
           throws org.apache.xml.security.exceptions.XMLSecurityException {

      try {
         String wrappedKeyAlgorithm =
            JCEMapper
               .translateURItoJCEID(wrappedKeyURI, this
                  .getRequiredProviderName()).getAlgorithmID();
         int wrappedKeyType = JCEMapper.getKeyTypeFromURI(wrappedKeyURI);
         AESWrapper wrapper = new AESWrapper(this._cipher);
         byte[] encoded = wrapper.unwrap(wrappedKey, wrapKey);

         if (wrappedKeyType == Cipher.SECRET_KEY) {
            return new SecretKeySpec(encoded, wrappedKeyAlgorithm);
         } else {
            try {
               KeyFactory kf =
                  KeyFactory.getInstance(wrappedKeyAlgorithm,
                                         this.getRequiredProviderName());

               if (wrappedKeyType == Cipher.PUBLIC_KEY) {
                  return kf.generatePublic(new X509EncodedKeySpec(encoded));
               } else if (wrappedKeyType == Cipher.PRIVATE_KEY) {
                  return kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
               }
            } catch (NoSuchProviderException e) {
               throw new InvalidKeyException("Unknown key type "
                                             + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
               throw new InvalidKeyException("Unknown key type "
                                             + e.getMessage());
            } catch (InvalidKeySpecException e2) {
               throw new InvalidKeyException("Unknown key type "
                                             + e2.getMessage());
            }

            throw new InvalidKeyException("Unknown key type " + wrappedKeyType);
         }
      } catch (InvalidKeyException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method engineEncrypt
    *
    * @param plaintextBytes
    * @param contentKey
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public byte[] engineEncrypt(byte[] plaintextBytes, Key contentKey)
           throws org.apache.xml.security.exceptions.XMLSecurityException {
      throw new XMLSecurityException(
         "encryption.algorithmCannotEncryptDecrypt");
   }

   /**
    * Method engineEncrypt
    *
    * @param plaintextBytes
    * @param contentKey
    * @param IV
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public byte[] engineEncrypt(byte[] plaintextBytes, Key contentKey, byte[] IV)
           throws org.apache.xml.security.exceptions.XMLSecurityException {
      throw new XMLSecurityException(
         "encryption.algorithmCannotEncryptDecrypt");
   }

   /**
    * Method engineDecrypt
    *
    * @param ciphertextBytes
    * @param contentKey
    * @return
    * @throws org.apache.xml.security.exceptions.XMLSecurityException
    */
   public byte[] engineDecrypt(byte[] ciphertextBytes, Key contentKey)
           throws org.apache.xml.security.exceptions.XMLSecurityException {
      throw new XMLSecurityException(
         "encryption.algorithmCannotEncryptDecrypt");
   }
}