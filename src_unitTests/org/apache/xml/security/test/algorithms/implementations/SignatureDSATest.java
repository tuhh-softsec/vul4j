/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.test.algorithms.implementations;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.DERConstructedSequence;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DEROutputStream;
import org.apache.xml.security.utils.HexDump;
import org.apache.xml.security.utils.Base64;


/**
 * Tests the conversion methods between ASN.1 and XML Signature DSA format.
 * @author $Author$
 */
public class SignatureDSATest {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(SignatureDSATest.class.getName());

   /**
    * Method main
    *
    * @param unused
    * @throws IOException
    */
   public static void main(String unused[]) throws IOException {

      // BigInteger r = new BigInteger("797387772302493209021291765790742058535532839360");
      String rStr = "8BAC1AB6 6410435C B7181F95 B16AB97C 92B341C0";

      // BigInteger s = new BigInteger(376128930725767930183372771997677399746658752712");
      String sStr = "41E2345F 1F56DF24 58F426D1 55B4BA2D B6DCD8C8";
      String xmlEncoded =
         "i6watmQQQ1y3GB+VsWq5fJKzQcBB4jRfH1bfJFj0JtFVtLotttzYyA==";
      String asn1Encoded =
         "MC0CFQCLrBq2ZBBDXLcYH5Wxarl8krNBwAIUQeI0Xx9W3yRY9CbRVbS6Lbbc2Mg=";
      BigInteger r = new BigInteger(1, HexDump.hexStringToByteArray(rStr));
      BigInteger s = new BigInteger(1, HexDump.hexStringToByteArray(sStr));

      System.out.println("r: " + HexDump.byteArrayToHexString(r.toByteArray()));
      System.out.println(r.toString());
      System.out.println("s: " + HexDump.byteArrayToHexString(s.toByteArray()));
      System.out.println(s.toString());
      System.out.println(Base64.encode(convertBIGINTtoXMLDSIG(r, s)));
      System.out.println(Base64.encode(convertBIGINTtoASN1(r, s)));
   }

   void test1 () throws Exception {
      String rStr = "8BAC1AB6 6410435C B7181F95 B16AB97C 92B341C0";

      String sStr = "41E2345F 1F56DF24 58F426D1 55B4BA2D B6DCD8C8";
      String xmlEncoded =
         "i6watmQQQ1y3GB+VsWq5fJKzQcBB4jRfH1bfJFj0JtFVtLotttzYyA==";

      BigInteger r = new BigInteger(1, HexDump.hexStringToByteArray(rStr));
      BigInteger s = new BigInteger(1, HexDump.hexStringToByteArray(sStr));

      String encoded = Base64.encode(convertBIGINTtoXMLDSIG(r, s));
      // assertTrue(encoded.equals(xmlEncoded));
   }

   /**
    * Method convertBIGINTtoASN1
    *
    * @param r
    * @param s
    *
    * @throws IOException
    */
   private static byte[] convertBIGINTtoASN1(BigInteger r, BigInteger s)
           throws IOException {

      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      DEROutputStream dOut = new DEROutputStream(bOut);
      DERConstructedSequence seq = new DERConstructedSequence();

      seq.addObject(new DERInteger(r));
      seq.addObject(new DERInteger(s));
      dOut.writeObject(seq);

      return bOut.toByteArray();
   }

   /**
    * Method convertBIGINTtoXMLDSIG
    *
    * @param r
    * @param s
    *
    * @throws IOException
    */
   private static byte[] convertBIGINTtoXMLDSIG(BigInteger r, BigInteger s)
           throws IOException {

      byte rbytes[] = r.toByteArray();
      byte sbytes[] = s.toByteArray();

      rbytes = normalizeBigIntegerArray(rbytes);
      sbytes = normalizeBigIntegerArray(sbytes);

      byte result[] = new byte[40];

      System.arraycopy(rbytes, 0, result, 0, 20);
      System.arraycopy(sbytes, 0, result, 20, 20);

      return result;
   }

   /**
    * Method convertASN1toBIGINT
    *
    * @param derbytes
    *
    * @throws IOException
    */
   private static BigInteger[] convertASN1toBIGINT(byte derbytes[])
           throws IOException {

      ByteArrayInputStream bIn = new ByteArrayInputStream(derbytes);
      DERInputStream dIn = new DERInputStream(bIn);
      DERConstructedSequence seq = (DERConstructedSequence) dIn.readObject();
      BigInteger r = ((DERInteger) seq.getObjectAt(0)).getValue();
      BigInteger s = ((DERInteger) seq.getObjectAt(1)).getValue();
      BigInteger result[] = new BigInteger[2];

      result[0] = r;
      result[1] = s;

      return result;
   }

   /**
    * Method convertXMLDSIGtoBIGINT
    *
    * @param xmldsigbytes
    *
    * @throws IOException
    */
   private static BigInteger[] convertXMLDSIGtoBIGINT(byte[] xmldsigbytes)
           throws IOException {

      byte rbytes[] = new byte[21];
      byte sbytes[] = new byte[21];

      rbytes[0] = (byte) 0x00;
      sbytes[0] = (byte) 0x00;

      System.arraycopy(xmldsigbytes, 0, rbytes, 1, 20);
      System.arraycopy(xmldsigbytes, 20, sbytes, 1, 20);

      BigInteger rs[] = new BigInteger[2];

      rs[0] = new BigInteger(rbytes);
      rs[1] = new BigInteger(sbytes);

      return rs;
   }

   /**
    * Converts an ASN.1 DSA value to a XML Signature DSA Value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param derbytes
    *
    * @throws IOException
    * @see org.bouncycastle.jce.provider.JDKDSASigner#derDecode
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
    */
   private static byte[] convertASN1toXMLDSIG(byte derbytes[])
           throws IOException {

      BigInteger rs[] = convertASN1toBIGINT(derbytes);

      return convertBIGINTtoXMLDSIG(rs[0], rs[1]);
   }

   /**
    * Converts a XML Signature DSA Value to an ASN.1 DSA value.
    *
    * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
    * pairs; the XML Signature requires the core BigInteger values.
    *
    * @param xmldsigbytes
    *
    * @throws IOException
    * @see org.bouncycastle.jce.provider.JDKDSASigner#derEncode
    * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
    */
   private static byte[] convertXMLDSIGtoASN1(byte[] xmldsigbytes)
           throws IOException {

      BigInteger rs[] = convertXMLDSIGtoBIGINT(xmldsigbytes);

      return convertBIGINTtoASN1(rs[0], rs[1]);
   }

   /**
    * Method normalizeBigIntegerArray
    *
    * @param bigIntegerArray
    *
    */
   private static byte[] normalizeBigIntegerArray(byte bigIntegerArray[]) {

      byte resultBytes[] = new byte[20];

      for (int i = 0; i < 20; i++) {
         resultBytes[i] = (byte) 0x00;
      }

      if (bigIntegerArray.length == 21) {
         System.arraycopy(bigIntegerArray, 1, resultBytes, 0, 20);
      } else {
         System.arraycopy(bigIntegerArray, 0, resultBytes,
                          20 - bigIntegerArray.length, bigIntegerArray.length);
      }

      return resultBytes;
   }
}
