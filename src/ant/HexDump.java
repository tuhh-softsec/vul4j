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

package ant;

import java.io.*;
import sun.misc.HexDumpEncoder;

/**
 *
 * @author Niko Schweitzer
 */
public class HexDump {
   /**
    * Method prettyPrintHex
    *
    *
    * @param baToConvert
    * @return hexdump string
    */
   public static String prettyPrintHex(byte[] baToConvert) {

      HexDumpEncoder hde = new HexDumpEncoder();

      return hde.encodeBuffer(baToConvert);
   }

   /**
    * Method prettyPrintHex
    *
    *
    * @param sToConvert
    * @return hexdump string
    */
   public static String prettyPrintHex(String sToConvert) {
      return prettyPrintHex(sToConvert.getBytes());
   }

   /** Field DEBUG */
   private static boolean DEBUG = false;

   /** Field HEX_DIGITS */
   private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6',
                                              '7', '8', '9', 'A', 'B', 'C', 'D',
                                              'E', 'F' };

   /** Field BIT_DIGIT */
   private static char[] BIT_DIGIT = { '0', '1' };

   /** Field COMPARE_BITS */
   private final static byte[] COMPARE_BITS = { (byte) 0x80, (byte) 0x40,
                                                (byte) 0x20, (byte) 0x10,
                                                (byte) 0x08, (byte) 0x04,
                                                (byte) 0x02, (byte) 0x01 };

   /** Field BYTE_SEPARATOR */
   private static char BYTE_SEPARATOR = ' ';

   /** Field WITH_BYTE_SEPARATOR */
   private static boolean WITH_BYTE_SEPARATOR = true;

   /**
    *  Sets the Debug attribute of the Convert object
    *
    * @param  dbg  The new Debug value
    */
   public static void setDebug(boolean dbg) {
      DEBUG = dbg;
   }

   /**
    *  Sets the WithByteSeparator attribute of the Convert class
    *
    * @param  bs  The new WithByteSeparator value
    */
   public static void setWithByteSeparator(boolean bs) {
      WITH_BYTE_SEPARATOR = bs;
   }

   /**
    *  Sets the ByteSeparator attribute of the Convert class
    *
    * @param  bs  The new ByteSeparator value
    */
   public static void setByteSeparator(char bs) {
      BYTE_SEPARATOR = bs;
   }

   /**
    *  Sets the BitDigits attribute of the Convert class
    *
    * @param  bd             The new BitDigits value
    * @exception  Exception  Description of Exception
    */
   public static void setBitDigits(char[] bd) throws Exception {

      if (bd.length != 2) {
         throw new Exception("wrong number of characters!");
      }

      BIT_DIGIT = bd;
   }

   /**
    * Method setBitDigits
    *
    * @param zeroBit
    * @param oneBit
    */
   public static void setBitDigits(char zeroBit, char oneBit) {
      BIT_DIGIT[0] = zeroBit;
      BIT_DIGIT[1] = oneBit;
   }

   /*
    * Converts a byte array to hex string
    */

   /**
    *  Description of the Method
    *
    * @param  block  Description of Parameter
    * @return        Description of the Returned Value
    */
   public static String byteArrayToBinaryString(byte[] block) {

      StringBuffer strBuf = new StringBuffer();
      int iLen = block.length;

      //---- for all bytes of array
      for (int i = 0; i < iLen; i++) {
         byte2bin(block[i], strBuf);

         //---- if bit i is set   ----//
         if ((i < iLen - 1) & WITH_BYTE_SEPARATOR) {
            strBuf.append(BYTE_SEPARATOR);
         }
      }

      return strBuf.toString();
   }

   /**
    * Method toBinaryString
    *
    * @param ba
    * @return
    */
   public static String toBinaryString(byte[] ba) {
      return byteArrayToBinaryString(ba);
   }

   /**
    * Method toBinaryString
    *
    * @param b
    * @return
    */
   public static String toBinaryString(byte b) {

      byte[] ba = new byte[1];

      ba[0] = b;

      return byteArrayToBinaryString(ba);
   }

   /**
    * Method toBinaryString
    *
    * @param s
    * @return
    */
   public static String toBinaryString(short s) {
      return toBinaryString(toByteArray(s));
   }

   /**
    * Method toBinaryString
    *
    * @param i
    * @return
    */
   public static String toBinaryString(int i) {
      return toBinaryString(toByteArray(i));
   }

   /**
    * Method toBinaryString
    *
    * @param l
    * @return
    */
   public static String toBinaryString(long l) {
      return toBinaryString(toByteArray(l));
   }

   /**
    * Method toByteArray
    *
    * @param s
    * @return
    */
   public static final byte[] toByteArray(short s) {

      byte[] baTemp = new byte[2];

      baTemp[1] = (byte) (s);
      baTemp[0] = (byte) (s >> 8);

      return baTemp;
   }

   /**
    * Method toByteArray
    *
    * @param i
    * @return
    */
   public static final byte[] toByteArray(int i) {

      byte[] baTemp = new byte[4];

      baTemp[3] = (byte) i;
      baTemp[2] = (byte) (i >> 8);
      baTemp[1] = (byte) (i >> 16);
      baTemp[0] = (byte) (i >> 24);

      return baTemp;
   }

   /**
    * Method toByteArray
    *
    * @param l
    * @return
    */
   public static final byte[] toByteArray(long l) {

      byte[] baTemp = new byte[8];

      baTemp[7] = (byte) l;
      baTemp[6] = (byte) (l >> 8);
      baTemp[5] = (byte) (l >> 16);
      baTemp[4] = (byte) (l >> 24);
      baTemp[3] = (byte) (l >> 32);
      baTemp[2] = (byte) (l >> 40);
      baTemp[1] = (byte) (l >> 48);
      baTemp[0] = (byte) (l >> 56);

      return baTemp;
   }

   /**
    *  Description of the Method
    *
    * @param  block  Description of Parameter
    * @return        Description of the Returned Value
    */
   public static String byteArrayToHexString(byte[] block) {

      long lTime = System.currentTimeMillis();
      StringBuffer buf = new StringBuffer();
      int len = block.length;

      for (int i = 0; i < len; i++) {
         byte2hex(block[i], buf);

         if ((i < len - 1) & WITH_BYTE_SEPARATOR) {
            buf.append(BYTE_SEPARATOR);
         }
      }

      return buf.toString();
   }

   /**
    *  Description of the Method
    *
    * @param  in  string to be converted
    * @return     String in readable hex encoding
    */
   public static String stringToHexString(String in) {

      byte[] ba = in.getBytes();

      return toHexString(ba);
   }

   /**
    *  Description of the Method
    *
    * @param  block   Description of Parameter
    * @param  offset  Description of Parameter
    * @param  length  Description of Parameter
    * @return         Description of the Returned Value
    */
   public static String byteArrayToHexString(byte[] block, int offset,
                                             int length) {

      long lTime = System.currentTimeMillis();
      StringBuffer buf = new StringBuffer();
      int len = block.length;

      length = length + offset;

      if ((len < length)) {
         length = len;
      }

      for (int i = 0 + offset; i < length; i++) {
         byte2hex(block[i], buf);

         if (i < length - 1) {
            buf.append(":");
         }
      }

      return buf.toString();
   }

   /**
    *  Returns a string of hexadecimal digits from a byte array. Each byte is
    *  converted to 2 hex symbols.
    *
    * @param  ba  Description of Parameter
    * @return     Description of the Returned Value
    */
   public static String toHexString(byte[] ba) {
      return toHexString(ba, 0, ba.length);
   }

   /**
    * Method toHexString
    *
    * @param b
    * @return
    */
   public static String toHexString(byte b) {

      byte[] ba = new byte[1];

      ba[0] = b;

      return toHexString(ba, 0, ba.length);
   }

   /**
    *  Description of the Method
    *
    * @param s
    * @return               Description of the Returned Value
    */
   public static String toHexString(short s) {
      return toHexString(toByteArray(s));
   }

   /**
    * Method toHexString
    *
    * @param i
    * @return
    */
   public static String toHexString(int i) {
      return toHexString(toByteArray(i));
   }

   /**
    * Method toHexString
    *
    * @param l
    * @return
    */
   public static String toHexString(long l) {
      return toHexString(toByteArray(l));
   }

   /**
    * Method toString
    *
    * @param ba
    * @return
    */
   public static String toString(byte[] ba) {
      return new String(ba).toString();
   }

   /**
    * Method toString
    *
    * @param b
    * @return
    */
   public static String toString(byte b) {

      byte[] ba = new byte[1];

      ba[0] = b;

      return new String(ba).toString();
   }

   /**
    *  converts String to Hex String. Example: niko ->6E696B6F
    *
    * @param  ba      Description of Parameter
    * @param  offset  Description of Parameter
    * @param  length  Description of Parameter
    * @return         Description of the Returned Value
    */
   public static String toHexString(byte[] ba, int offset, int length) {

      long lTime = System.currentTimeMillis();
      char[] buf;

      if (WITH_BYTE_SEPARATOR) {
         buf = new char[length * 3];
      } else {
         buf = new char[length * 2];
      }

      for (int i = offset, j = 0, k; i < offset + length; ) {
         k = ba[i++];
         buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
         buf[j++] = HEX_DIGITS[k & 0x0F];

         if (WITH_BYTE_SEPARATOR) {
            buf[j++] = BYTE_SEPARATOR;
         }
      }

      return new String(buf);
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

   /*
    * Converts a byte to hex digit and writes to the supplied buffer
    */

   /**
    *  Description of the Method
    *
    * @param  b    Description of Parameter
    * @param  buf  Description of Parameter
    */
   private static void byte2hex(byte b, StringBuffer buf) {

      char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                          'B', 'C', 'D', 'E', 'F' };
      int high = ((b & 0xf0) >> 4);
      int low = (b & 0x0f);

      buf.append(hexChars[high]);
      buf.append(hexChars[low]);
   }

   /**
    *  Description of the Method
    *
    * @param  b    Description of Parameter
    * @param  buf  Description of Parameter
    */
   private static void byte2bin(byte b, StringBuffer buf) {

      // test every 8 bit
      for (int i = 0; i < 8; i++) {

         //---test if bit is set
         if ((b & COMPARE_BITS[i]) != 0) {
            buf.append(BIT_DIGIT[1]);
         } else {
            buf.append(BIT_DIGIT[0]);
         }
      }
   }

   /**
    *  Returns a string of 8 hexadecimal digits (most significant digit first)
    *  corresponding to the integer <i>n</i> , which is treated as unsigned.
    *
    * @param  n  Description of Parameter
    * @return    Description of the Returned Value
    */
   private static String intToHexString(int n) {

      char[] buf = new char[8];

      for (int i = 7; i >= 0; i--) {
         buf[i] = HEX_DIGITS[n & 0x0F];
         n >>>= 4;
      }

      return new String(buf);
   }

   /**
    *  test and demo for the Convert class
    *
    * @param  args  none needed
    */
   public static void main(String args[]) {

      System.out.println("-test and demo of the converter ");

      // enable debug outputs
      setDebug(false);

      String str = new String("Niko");
      byte[] ba = str.getBytes();

      System.out.println("to convert: " + str);
      System.out.println("converted1: " + byteArrayToHexString(ba));
      System.out.println("converted1: "
                         + byteArrayToHexString(ba, 0, ba.length));
      System.out.println("converted3: " + stringToHexString(str));
      System.out.println("----Convert integer to hexString...");

      int i = -2;

      System.out.println("to convert: " + i + " -> " + intToHexString(i));
      System.out.println("----Convert byte[] to binary String...");

      byte[] baToConvert = { (byte) 0xff, (byte) 0x00, (byte) 0x33, (byte) 0x11,
                             (byte) 0xff, (byte) 0x5f, (byte) 0x5f, (byte) 0x4f,
                             (byte) 0x1f, (byte) 0xff };

      System.out.println("to convert: " + toHexString(baToConvert) + " -> "
                         + byteArrayToBinaryString(baToConvert));

      //---- modify line separator
      setByteSeparator('-');
      System.out.println("to convert: " + toHexString(baToConvert) + " -> "
                         + byteArrayToBinaryString(baToConvert));

      //---- modify line separator
      setByteSeparator('*');
      setWithByteSeparator(true);
      System.out.println("to convert: " + toHexString(baToConvert) + " -> "
                         + byteArrayToBinaryString(baToConvert));

      //---- modify bit digits
      char[] bd = { 'a', 'b' };

      try {
         setBitDigits(bd);
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      System.out.println("to convert: " + toHexString(baToConvert) + " -> "
                         + byteArrayToBinaryString(baToConvert));

      //------------------------------------------------//
      setBitDigits('0', '1');
      System.out.println("---- Convert.toByteArray(int) ");

      for (int iToConvert = -10; iToConvert < 10; iToConvert++) {
         System.out.println("to convert = " + iToConvert + " = "
                            + HexDump.toBinaryString(iToConvert));

         byte[] baConvInt = new byte[4];

         baConvInt = HexDump.toByteArray(iToConvert);

         System.out.println("convertet byteArray = "
                            + HexDump.toBinaryString(baConvInt));
      }

      System.out.println("---- toHexString(int) ");

      i = -1;

      System.out.println(i + " = 0x" + toHexString(i) + " = "
                         + toBinaryString(i));

      i++;

      System.out.println(i + " = 0x" + toHexString(i) + " = "
                         + toBinaryString(i));

      //------------------------------------------------//
      System.out.println("---- toHexString(long) ");

      long l = 100;

      System.out.println(l + " = 0x" + toHexString(l) + " = "
                         + toBinaryString(l));

      java.util.Random rnd = new java.util.Random();

      l = rnd.nextLong();

      System.out.println(l + " = 0x" + toHexString(l) + " = "
                         + toBinaryString(l));

      //------------------------------------------------//
      System.out.println("---- toHexString(short) ");

      short s = 100;

      System.out.println(s + " = 0x" + toHexString(s) + " = "
                         + toBinaryString(s));

      rnd = new java.util.Random();
      s = (short) rnd.nextInt();

      System.out.println(s + " = 0x" + toHexString(s) + " = "
                         + toBinaryString(s));

      //---------------------------------------------------------------------------//
      // convert dezimal-String to binary
      System.out.println("---- read file in Hex-Format ");

      String strToConvert = "12345654321";

      System.out.println(strToConvert + " = "
                         + stringToHexString(strToConvert));
      System.out.println("Das ist die Hex-Darstellung des obigen Strings");

      byte[] baConverted = new byte[strToConvert.length()];

      baConverted = hexStringToByteArray(strToConvert);

      System.out.println("ba = " + toHexString(ba));
   }
}
