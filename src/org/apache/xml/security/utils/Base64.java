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
package org.apache.xml.security.utils;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.math.BigInteger;
import org.w3c.dom.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.exceptions.Base64DecodingException;


/**
 * Implementation of MIME's Base64 encoding and decoding conversions.
 * Optimized code. (raw version taken from oreilly.jonathan.util)
 *
 * @author Anli Shundi
 * @author Christian Geuer-Pollmann
 * @see <A HREF="ftp://ftp.isi.edu/in-notes/rfc2045.txt">RFC 2045</A>
 * @see org.apache.xml.security.transforms.implementations.TransformBase64Decode
 */
public class Base64 {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Base64.class.getName());

   /** Field LINE_SEPARATOR */
   public static final String LINE_SEPARATOR = "\n";

   /** Field BASE64DEFAULTLENGTH */
   public static final int BASE64DEFAULTLENGTH = 76;

   /** Field _base64length */
   static int _base64length = Base64.BASE64DEFAULTLENGTH;

   private Base64() {
     // we don't allow instantiation
   }

   /**
    * Method setBase64WrapLength
    *
    * @param length
    */
   public static void setBase64WrapLength(int length) {
      Base64._base64length = length;
   }

   /**
    * Method getBase64WrapLength
    *
    * @return
    */
   public static int getBase64WrapLength() {
      return Base64._base64length;
   }

   /**
    * Returns a byte-array representation of a <code>{@link BigInteger}<code>.
    * No sign-bit is outputed.
    *
    * <p><b>N.B.:</B> <code>{@link BigInteger}<code>'s toByteArray
    * retunrs eventually longer arrays because of the leading sign-bit.
    *
    * @param big <code>BigInteger<code> to be converted
    * @param bitlen <code>int<code> the desired length in bits of the representation
    * @return a byte array with <code>bitlen</code> bits of <code>big</code>
    */
   static byte[] getBytes(BigInteger big, int bitlen) {

      //round bitlen
      bitlen = ((bitlen + 7) >> 3) << 3;

      if (bitlen < big.bitLength()) {
         throw new IllegalArgumentException(I18n
            .translate("utils.Base64.IllegalBitlength"));
      }

      byte[] bigBytes = big.toByteArray();

      if (((big.bitLength() % 8) != 0)
              && (((big.bitLength() / 8) + 1) == (bitlen / 8))) {
         return bigBytes;
      } else {

         // some copying needed
         int startSrc = 0;    // no need to skip anything
         int bigLen = bigBytes.length;    //valid length of the string

         if ((big.bitLength() % 8) == 0) {    // correct values
            startSrc = 1;    // skip sign bit

            bigLen--;    // valid length of the string
         }

         int startDst = bitlen / 8 - bigLen;    //pad with leading nulls
         byte[] resizedBytes = new byte[bitlen / 8];

         System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);

         return resizedBytes;
      }
   }

   /**
    * Encode in Base64 the given <code>{@link BigInteger}<code>.
    *
    * @param big
    * @return String with Base64 encoding
    */
   public static String encode(BigInteger big) {
      return encode(getBytes(big, big.bitLength()));
   }

   /**
    * Returns a byte-array representation of a <code>{@link BigInteger}<code>.
    * No sign-bit is outputed.
    *
    * <p><b>N.B.:</B> <code>{@link BigInteger}<code>'s toByteArray
    * retunrs eventually longer arrays because of the leading sign-bit.
    *
    * @param big <code>BigInteger<code> to be converted
    * @param bitlen <code>int<code> the desired length in bits of the representation
    * @return a byte array with <code>bitlen</code> bits of <code>big</code>
    */
   public static byte[] encode(BigInteger big, int bitlen) {

      //round bitlen
      bitlen = ((bitlen + 7) >> 3) << 3;

      if (bitlen < big.bitLength()) {
         throw new IllegalArgumentException(I18n
            .translate("utils.Base64.IllegalBitlength"));
      }

      byte[] bigBytes = big.toByteArray();

      if (((big.bitLength() % 8) != 0)
              && (((big.bitLength() / 8) + 1) == (bitlen / 8))) {
         return bigBytes;
      } else {

         // some copying needed
         int startSrc = 0;    // no need to skip anything
         int bigLen = bigBytes.length;    //valid length of the string

         if ((big.bitLength() % 8) == 0) {    // correct values
            startSrc = 1;    // skip sign bit

            bigLen--;    // valid length of the string
         }

         int startDst = bitlen / 8 - bigLen;    //pad with leading nulls
         byte[] resizedBytes = new byte[bitlen / 8];

         System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);

         return resizedBytes;
      }
   }

   /**
    * Method decodeBigIntegerFromElement
    *
    * @param element
    * @return
    * @throws Base64DecodingException
    */
   public static BigInteger decodeBigIntegerFromElement(Element element)
           throws Base64DecodingException {
      return new BigInteger(1, Base64.decode(element));
   }

   /**
    * Method decodeBigIntegerFromText
    *
    * @param text
    * @return
    * @throws Base64DecodingException
    */
   public static BigInteger decodeBigIntegerFromText(Text text)
           throws Base64DecodingException {
      return new BigInteger(1, Base64.decode(text.getData()));
   }

   /**
    * This method takes an (empty) Element and a BigInteger and adds the
    * base64 encoded BigInteger to the Element.
    *
    * @param element
    * @param biginteger
    */
   public static void fillElementWithBigInteger(Element element,
           BigInteger biginteger) {

      String encodedInt = encode(biginteger);

      if (encodedInt.length() > 76) {
         encodedInt = "\n" + encodedInt + "\n";
      }

      Document doc = element.getOwnerDocument();
      Text text = doc.createTextNode(encodedInt);

      element.appendChild(text);
   }

   /**
    * Method decode
    *
    * Takes the <CODE>Text</CODE> children of the Element and interprets
    * them as input for the <CODE>Base64.decode()</CODE> function.
    *
    * @param element
    * @return
    * @todo not tested yet
    * @throws Base64DecodingException
    */
   public static byte[] decode(Element element) throws Base64DecodingException {

      NodeList nl = element.getChildNodes();
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < nl.getLength(); i++) {
         if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
            Text t = (Text) nl.item(i);

            sb.append(t.getData());
         }
      }

      return decode(sb.toString());
   }

   /**
    * Method encodeToElement
    *
    * @param doc
    * @param localName
    * @param bytes
    * @return
    */
   public static Element encodeToElement(Document doc, String localName,
                                         byte[] bytes) {

      Element el = XMLUtils.createElementInSignatureSpace(doc, localName);
      Text text = doc.createTextNode(encode(bytes));

      el.appendChild(text);

      return el;
   }

   /**
    * Method decode
    *
    *
    * @param base64
    *
    * @return
    * @throws Base64DecodingException
    */
   public static byte[] decode(byte[] base64) throws Base64DecodingException {

      try {
         return decode(new String(base64, "UTF-8"));
      } catch (java.io.UnsupportedEncodingException ex) {

         // should never be reached because Encoding is valid and fixed
         return new byte[0];
      }
   }

   /**
    * <p>Decode a Base64-encoded string to a byte array</p>
    *
    * @param base64 <code>String</code> encoded string (single line only !!)
    * @return Decoded data in a byte array
    * @throws Base64DecodingException
    */
   public static byte[] decode(String base64) throws Base64DecodingException {

      try {
         if (base64.length() < 30) {
            cat.debug("I was asked to decode \"" + base64 + "\"");
         } else {
            cat.debug("I was asked to decode \"" + base64.substring(0, 20)
                      + "...\"");
         }

         //strip whitespace from anywhere in the string.  Not the most memory
         //efficient solution but elegant anyway :-)
         StringTokenizer tok = new StringTokenizer(base64, " \n\r\t", false);
         StringBuffer buf = new StringBuffer(base64.length());

         while (tok.hasMoreElements()) {
            buf.append(tok.nextToken());
         }

         base64 = buf.toString();

         int pad = 0;

         for (int i = base64.length() - 1;
                 (i > 0) && (base64.charAt(i) == '='); i--) {
            pad++;
         }

         int length = base64.length() / 4 * 3 - pad;
         byte[] raw = new byte[length];

         for (int i = 0, rawIndex = 0; i < base64.length();
                 i += 4, rawIndex += 3) {
            int block = (getValue(base64.charAt(i)) << 18)
                        + (getValue(base64.charAt(i + 1)) << 12)
                        + (getValue(base64.charAt(i + 2)) << 6)
                        + (getValue(base64.charAt(i + 3)));

            for (int j = 2; j >= 0; j--) {
               if (rawIndex + j < raw.length) {
                  raw[rawIndex + j] = (byte) (block & 0xff);
               }

               block >>= 8;
            }
         }

         return raw;
      } catch (IndexOutOfBoundsException ex) {
         throw new Base64DecodingException("utils.Base64.IllegalBitlength", ex);
      }
   }

   /**
    * <p>Encode a byte array in Base64 format and return an optionally
    * wrapped line</p>
    *
    * @param raw <code>byte[]</code> data to be encoded
    * @param wrap <code>int<code> length of wrapped lines; No wrapping if less than 4.
    * @return a <code>String</code> with encoded data
    */
   public static String encode(byte[] raw, int wrap) {

      //calculate length of encoded string
      int encLen = ((raw.length + 2) / 3) * 4;

      //adjust for newlines
      if (wrap > 3) {
         wrap -= wrap % 4;
         encLen += 2 * (encLen / wrap);
      } else {    //disable wrapping
         wrap = Integer.MAX_VALUE;
      }

      StringBuffer encoded = new StringBuffer(encLen);
      int len3 = (raw.length / 3) * 3;
      int outLen = 0;    //length of output line

      for (int i = 0; i < len3; i += 3, outLen += 4) {
         if (outLen + 4 > wrap) {
            encoded.append(LINE_SEPARATOR);

            outLen = 0;
         }

         encoded.append(encodeFullBlock(raw, i));
      }

      if (outLen >= wrap) {    //this will produce an extra newline if needed !? Sun had it this way...
         encoded.append(LINE_SEPARATOR);
      }

      if (len3 < raw.length) {
         encoded.append(encodeBlock(raw, len3));
      }

      return encoded.toString();
   }

   /**
    * Encode a byte array and fold lines at the standard 76th character.
    *
    * @param raw <code>byte[]<code> to be base64 encoded
    * @return the <code>String<code> with encoded data
    */
   public static String encode(byte[] raw) {
      return encode(raw, Base64.getBase64WrapLength());
   }

   /**
    * Base64 decode the lines from the reader and return an InputStream
    * with the bytes.
    *
    *
    * @param reader
    * @return InputStream with the decoded bytes
    * @Exception IOException passes what the reader throws
    * @throws Base64DecodingException
    * @throws IOException
    */
   public static byte[] decode(BufferedReader reader)
           throws IOException, Base64DecodingException {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      String line;

      while (null != (line = reader.readLine())) {
         byte[] bytes = decode(line);

         baos.write(bytes);
      }

      return baos.toByteArray();
   }

   /**
    * Method encodeBlock
    *
    * @param raw
    * @param offset
    * @return
    */
   protected static char[] encodeBlock(byte[] raw, int offset) {

      int block = 0;
      int slack = raw.length - offset - 1;
      int end = (slack >= 2)
                ? 2
                : slack;

      for (int i = 0; i < 3; i++) {
         byte b = (offset + i < raw.length)
                  ? raw[offset + i]
                  : 0;
         int neuter = (b < 0)
                      ? b + 256
                      : b;

         block <<= 8;
         block += neuter;
      }

      char[] base64 = new char[4];

      for (int i = 3; i >= 0; i--) {
         int sixBit = block & 0x3f;

         base64[i] = getChar(sixBit);
         block >>= 6;
      }

      if (slack < 1) {
         base64[2] = '=';
      }

      if (slack < 2) {
         base64[3] = '=';
      }

      return base64;
   }

   /**
    * Method encodeFullBlock
    *
    * @param raw
    * @param offset
    * @return
    */
   protected static char[] encodeFullBlock(byte[] raw, int offset) {

      int block = 0;

      for (int i = 0; i < 3; i++) {

         //byte b = raw[offset + i];
         //int neuter = (b < 0) ? b + 256 : b;
         block <<= 8;
         block += (0xff & raw[offset + i]);
      }

      block = ((raw[offset] & 0xff) << 16) + ((raw[offset + 1] & 0xff) << 8)
              + (raw[offset + 2] & 0xff);

      char[] base64 = new char[4];

      for (int i = 3; i >= 0; i--) {
         int sixBit = block & 0x3f;

         base64[i] = getChar(sixBit);
         block >>= 6;
      }

      return base64;
   }

   /**
    * Method getChar
    *
    * @param sixBit
    * @return
    */
   protected static char getChar(int sixBit) {

      if ((sixBit >= 0) && (sixBit < 26)) {
         return (char) ('A' + sixBit);
      }

      if ((sixBit >= 26) && (sixBit < 52)) {
         return (char) ('a' + (sixBit - 26));
      }

      if ((sixBit >= 52) && (sixBit < 62)) {
         return (char) ('0' + (sixBit - 52));
      }

      if (sixBit == 62) {
         return '+';
      }

      if (sixBit == 63) {
         return '/';
      }

      return '?';
   }

   /**
    * Method getValue
    *
    * @param c
    * @return
    */
   protected static int getValue(char c) {

      if ((c >= 'A') && (c <= 'Z')) {
         return c - 'A';
      }

      if ((c >= 'a') && (c <= 'z')) {
         return c - 'a' + 26;
      }

      if ((c >= '0') && (c <= '9')) {
         return c - '0' + 52;
      }

      if (c == '+') {
         return 62;
      }

      if (c == '/') {
         return 63;
      }

      if (c == '=') {
         return 0;
      }

      return -1;
   }

   //        boolean bInWSpace = false;//?
   //        for(int i=0, j=0, len=base64.length();  i < len; i++) {
   //            if( bInWSpace ) {
   //                if( Character.isWhitespace(base64.charAt(i)) ) {
   //                    skipLen++;
   //                } else {
   //                    //copy here & reset
   //                }
   //            } else {
   //                if( Character.isWhitespace(base64.charAt(i)) ) {
   //                    bInWSpace = true;
   //                    skipLen++;
   //                } else {
   //                    //copy here & reset
   //                }
   //            }
   //        }

   /**
    * Method main
    *
    *
    * @param args
    *
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

      DocumentBuilderFactory docBuilderFactory =
         DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      String testString1 =
         "<container><base64 value=\"Should be 'Hallo'\">SGFsbG8=</base64></container>";
      InputSource inputSource = new InputSource(new StringReader(testString1));
      Document doc = docBuilder.parse(inputSource);
      Element base64Elem =
         (Element) doc.getDocumentElement().getChildNodes().item(0);

      System.out.println(new String(decode(base64Elem)));
   }
}
