package org.apache.xml.security.test.algorithms.implementations;


import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.*;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 *
 * @author $Author$
 */
public class AESWrapperTest extends TestCase {

   /**
    * Method suite
    *
    * @return
    */
   public static Test suite() {
      return new TestSuite(AESWrapperTest.class);
   }

   /**
    * Constructor AESWrapperTest
    *
    * @param Name_
    */
   public AESWrapperTest(String Name_) {
      super(Name_);
   }

   /**
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading", AESWrapperTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method setUp
    *
    */
   public void setUp() {

      try {
         Provider provider =
            (Provider) Class
               .forName("org.bouncycastle.jce.provider.BouncyCastleProvider")
                  .newInstance();

         Security.addProvider(provider);
      } catch (ClassNotFoundException ex) {
         throw new RuntimeException(ex.getMessage());
      } catch (IllegalAccessException ex) {
         throw new RuntimeException(ex.getMessage());
      } catch (InstantiationException ex) {
         throw new RuntimeException(ex.getMessage());
      }
   }

   /**
    * Method wrapUnwrap
    *
    * @param KEK
    * @param keyData
    * @param ciphertext
    * @return
    * @throws Exception
    */
   private boolean wrapUnwrap(String KEK, String keyData, String ciphertext)
           throws Exception {

      byte KEKbytes[] = AESWrapperTest.hexStringToByteArray(KEK);
      byte keyDataBytes[] = AESWrapperTest.hexStringToByteArray(keyData);
      byte expectedCiphertextBytes[] = AESWrapperTest.hexStringToByteArray(ciphertext);
      Cipher aesWrapper = Cipher.getInstance("AESWrap", "BC");
      byte ivbytes[] = { (byte) 0xa6, (byte) 0xa6, (byte) 0xa6, (byte) 0xa6,
                         (byte) 0xa6, (byte) 0xa6, (byte) 0xa6, (byte) 0xa6 };
      IvParameterSpec iv = new IvParameterSpec(ivbytes);

      aesWrapper.init(Cipher.WRAP_MODE, new SecretKeySpec(KEKbytes, "AES"), iv);

      Key keyDataKey = new SecretKeySpec(keyDataBytes, "AES");
      byte realCipherTextBytes[] = aesWrapper.wrap(keyDataKey);

      if (!MessageDigest.isEqual(realCipherTextBytes,
                                 expectedCiphertextBytes)) {
         System.out.println("wrap failes");

         return false;
      }

      aesWrapper.init(Cipher.UNWRAP_MODE, new SecretKeySpec(KEKbytes, "AES"));

      Key plaintextKey = aesWrapper.unwrap(realCipherTextBytes, "AES",
                                           Cipher.SECRET_KEY);
      byte realPlainTextBytes[] = plaintextKey.getEncoded();

      if (!MessageDigest.isEqual(realPlainTextBytes, keyDataBytes)) {
         return false;
      }

      return true;
   }

   /**
    * Method test41
    *
    * @throws Exception
    */
   public void test41() throws Exception {
      //J-
       assertTrue("Wrap 128 bits of Key Data with a 128-bit KEK",
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F",
                  "0011223344556677 8899AABBCCDDEEFF",
                  "1FA68B0A8112B447 AEF34BD8FB5A7B82 9D3E862371D2CFE5"));
       //J+
   }

   /**
    * Method test42
    *
    * @throws Exception
    */
   public void test42() throws Exception {
      //J-
       assertTrue("4.2 Wrap 128 bits of Key Data with a 192-bit KEK",
       wrapUnwrap("00010203040506070 8090A0B0C0D0E0F 1011121314151617",
                  "0011223344556677 8899AABBCCDDEEFF",
                  "96778B25AE6CA435 F92B5B97C050AED2 468AB8A17AD84E5D"));

       //J+
   }

   /**
    * Method test43
    *
    * @throws Exception
    */
   public void test43() throws Exception {
      //J-
       assertTrue("4.3 Wrap 128 bits of Key Data with a 256-bit KEK",
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F",
                  "0011223344556677 8899AABBCCDDEEFF",
                  "64E8C3F9CE0F5BA2 63E9777905818A2A 93C8191E7D6E8AE7"));

       //J+
   }

   /**
    * Method test44
    *
    * @throws Exception
    */
   public void test44() throws Exception {
      //J-
       assertTrue("4.4 Wrap 192 bits of Key Data with a 192-bit KEK",
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607",
                  "031D33264E15D332 68F24EC260743EDC E1C6C7DDEE725A93 6BA814915C6762D2"));

       //J+
   }

   /**
    * Method test45
    *
    * @throws Exception
    */
   public void test45() throws Exception {
      //J-
       assertTrue("4.5 Wrap 192 bits of Key Data with a 256-bit KEK",
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607",
                  "A8F9BC1612C68B3F F6E6F4FBE30E71E4 769C8B80A32CB895 8CD5D17D6B254DA1"));

       //J+
   }

   /**
    * Method test46
    *
    * @throws Exception
    */
   public void test46() throws Exception {
      //J-
       assertTrue("4.6 Wrap 256 bits of Key Data with a 256-bit KEK",
       wrapUnwrap("0001020304050607 08090A0B0C0D0E0F 1011121314151617 18191A1B1C1D1E1F",
                  "0011223344556677 8899AABBCCDDEEFF 0001020304050607 08090A0B0C0D0E0F",
                  "28C9F404C4B810F4 CBCCB35CFB87F826 3F5786E2D80ED326 CBC7F0E71A99F43B FB988B9B7A02DD21"));

       //J+
   }

   /**
    * Converts readable hex-String to byteArray
    *
    * @param strA
    * @return
    */
   public static byte[] hexStringToByteArray(String strA) {
      ByteArrayOutputStream result = new ByteArrayOutputStream();

      // alle Hex-Zeichen konvertieren, den Rest Ignorieren
      // jedes Zeichen stellt einen 4-Bit Wert dar
      byte sum = (byte) 0x00;
      boolean nextCharIsUpper = true;

      for (int i = 0; i < strA.length(); i++) {
         char c = strA.charAt(i);

         switch (Character.toUpperCase(c)) {

         case '0' :
            if (nextCharIsUpper) {
               sum = (byte) 0x00;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x00;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '1' :
            if (nextCharIsUpper) {
               sum = (byte) 0x10;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x01;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '2' :
            if (nextCharIsUpper) {
               sum = (byte) 0x20;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x02;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '3' :
            if (nextCharIsUpper) {
               sum = (byte) 0x30;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x03;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '4' :
            if (nextCharIsUpper) {
               sum = (byte) 0x40;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x04;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '5' :
            if (nextCharIsUpper) {
               sum = (byte) 0x50;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x05;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '6' :
            if (nextCharIsUpper) {
               sum = (byte) 0x60;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x06;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '7' :
            if (nextCharIsUpper) {
               sum = (byte) 0x70;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x07;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '8' :
            if (nextCharIsUpper) {
               sum = (byte) 0x80;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x08;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case '9' :
            if (nextCharIsUpper) {
               sum = (byte) 0x90;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x09;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'A' :
            if (nextCharIsUpper) {
               sum = (byte) 0xA0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0A;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'B' :
            if (nextCharIsUpper) {
               sum = (byte) 0xB0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0B;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'C' :
            if (nextCharIsUpper) {
               sum = (byte) 0xC0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0C;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'D' :
            if (nextCharIsUpper) {
               sum = (byte) 0xD0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0D;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'E' :
            if (nextCharIsUpper) {
               sum = (byte) 0xE0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0E;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;

         case 'F' :
            if (nextCharIsUpper) {
               sum = (byte) 0xF0;
               nextCharIsUpper = false;
            } else {
               sum |= (byte) 0x0F;
               result.write(sum);
               nextCharIsUpper = true;
            }
            break;
         }
      }

      if (!nextCharIsUpper) {
         throw new RuntimeException("The String did not contain an equal number of hex digits");
      }

      return result.toByteArray();
   }
}
