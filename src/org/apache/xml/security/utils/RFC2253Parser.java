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



import java.io.*;
import java.util.*;


/**
 *
 * @author $Author$
 */
public class RFC2253Parser {

   /** Field cat */

   /*   static org.apache.log4j.Category cat =
         org.apache.log4j.Category.getInstance(RFC2253Parser.class.getName());
   */
   static boolean _TOXML = true;

   /**
    * Method rfc2253toXMLdsig
    *
    * @param dn
    * @return
    */
   public static String rfc2253toXMLdsig(String dn) {

      _TOXML = true;

      // transfrom from RFC1779 to RFC2253
      String normalized = normalize(dn);

      return rfctoXML(normalized);
   }

   /**
    * Method xmldsigtoRFC2253
    *
    * @param dn
    * @return
    */
   public static String xmldsigtoRFC2253(String dn) {

      _TOXML = false;

      // transfrom from RFC1779 to RFC2253
      String normalized = normalize(dn);

      return xmltoRFC(normalized);
   }

   /**
    * Method normalize
    *
    * @param dn
    * @return
    */
   public static String normalize(String dn) {

      //if empty string
      if ((dn == null) || dn.equals("")) {
         return "";
      }

      try {
         String _DN = semicolonToComma(dn);
         StringBuffer sb = new StringBuffer();
         int i = 0;
         int l = 0;
         int k;

         //for name component
         for (int j = 0; (k = _DN.indexOf(",", j)) >= 0; j = k + 1) {
            l += countQuotes(_DN, j, k);

            if ((k > 0) && (_DN.charAt(k - 1) != '\\') && (l % 2) != 1) {
               sb.append(parseRDN(_DN.substring(i, k).trim()) + ",");

               i = k + 1;
               l = 0;
            }
         }

         sb.append(parseRDN(trim(_DN.substring(i))));

         return sb.toString();
      } catch (IOException ex) {
         return dn;
      }
   }

   /**
    * Method parseRDN
    *
    * @param str
    * @return
    * @throws IOException
    */
   static String parseRDN(String str) throws IOException {

      StringBuffer sb = new StringBuffer();
      int i = 0;
      int l = 0;
      int k;

      for (int j = 0; (k = str.indexOf("+", j)) >= 0; j = k + 1) {
         l += countQuotes(str, j, k);

         if ((k > 0) && (str.charAt(k - 1) != '\\') && (l % 2) != 1) {
            sb.append(parseATAV(trim(str.substring(i, k))) + "+");

            i = k + 1;
            l = 0;
         }
      }

      sb.append(parseATAV(trim(str.substring(i))));

      return sb.toString();
   }

   /**
    * Method parseATAV
    *
    * @param str
    * @return
    * @throws IOException
    */
   static String parseATAV(String str) throws IOException {

      int i = str.indexOf("=");

      if ((i == -1) || ((i > 0) && (str.charAt(i - 1) == '\\'))) {
         return str;
      } else {
         String attrType = normalizeAT(str.substring(0, i));
         String attrValue = normalizeV(str.substring(i + 1));

         return attrType + "=" + attrValue;
      }
   }

   /**
    * Method normalizeAT
    *
    * @param str
    * @return
    */
   static String normalizeAT(String str) {

      String at = str.toUpperCase().trim();

      if (at.startsWith("OID")) {
         at = at.substring(3);
      }

      return at;
   }

   /**
    * Method normalizeV
    *
    * @param str
    * @return
    * @throws IOException
    */
   static String normalizeV(String str) throws IOException {

      String value = trim(str);

      if (value.startsWith("\"")) {
         StringBuffer sb = new StringBuffer();
         StringReader sr = new StringReader(value.substring(1,
                              value.length() - 1));
         int i = 0;
         char c;

         for (; (i = sr.read()) > -1; ) {
            c = (char) i;

            //the following char is defined at 4.Relationship with RFC1779 and LDAPv2 inrfc2253
            if ((c == ',') || (c == '=') || (c == '+') || (c == '<')
                    || (c == '>') || (c == '#') || (c == ';')) {
               sb.append('\\');
            }

            sb.append(c);
         }

         value = trim(sb.toString());
      }

      if (_TOXML == true) {
         if (value.startsWith("#")) {
            value = '\\' + value;
         }
      } else {
         if (value.startsWith("\\#")) {
            value = value.substring(1);
         }
      }

      return value;
   }

   /**
    * Method rfctoXML
    *
    * @param string
    * @return
    */
   static String rfctoXML(String string) {

      try {
         String s = changeLess32toXML(string);

         return changeWStoXML(s);
      } catch (Exception e) {
         return string;
      }
   }

   /**
    * Method xmltoRFC
    *
    * @param string
    * @return
    */
   static String xmltoRFC(String string) {

      try {
         String s = changeLess32toRFC(string);

         return changeWStoRFC(s);
      } catch (Exception e) {
         return string;
      }
   }

   /**
    * Method changeLess32toRFC
    *
    * @param string
    * @return
    * @throws IOException
    */
   static String changeLess32toRFC(String string) throws IOException {

      StringBuffer sb = new StringBuffer();
      StringReader sr = new StringReader(string);
      int i = 0;
      char c;

      for (; (i = sr.read()) > -1; ) {
         c = (char) i;

         if (c == '\\') {
            sb.append(c);

            char c1 = (char) sr.read();
            char c2 = (char) sr.read();

            //65 (A) 97 (a)
            if ((((c1 >= 48) && (c1 <= 57)) || ((c1 >= 65) && (c1 <= 70)) || ((c1 >= 97) && (c1 <= 102)))
                    && (((c2 >= 48) && (c2 <= 57))
                        || ((c2 >= 65) && (c2 <= 70))
                        || ((c2 >= 97) && (c2 <= 102)))) {
               char ch = (char) Byte.parseByte("" + c1 + c2, 16);

               sb.append(ch);
            } else {
               sb.append(c1);
               sb.append(c2);
            }
         } else {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   /**
    * Method changeLess32toXML
    *
    * @param string
    * @return
    * @throws IOException
    */
   static String changeLess32toXML(String string) throws IOException {

      StringBuffer sb = new StringBuffer();
      StringReader sr = new StringReader(string);
      int i = 0;

      for (; (i = sr.read()) > -1; ) {
         if (i < 32) {
            sb.append('\\');
            sb.append(Integer.toHexString(i));
         } else {
            sb.append((char) i);
         }
      }

      return sb.toString();
   }

   /**
    * Method changeWStoXML
    *
    * @param string
    * @return
    * @throws IOException
    */
   static String changeWStoXML(String string) throws IOException {

      StringBuffer sb = new StringBuffer();
      StringReader sr = new StringReader(string);
      int i = 0;
      char c;

      for (; (i = sr.read()) > -1; ) {
         c = (char) i;

         if (c == '\\') {
            char c1 = (char) sr.read();

            if (c1 == ' ') {
               sb.append('\\');

               String s = "20";

               sb.append(s);
            } else {
               sb.append('\\');
               sb.append(c1);
            }
         } else {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   /**
    * Method changeWStoRFC
    *
    * @param string
    * @return
    * @throws IOException
    */
   static String changeWStoRFC(String string) throws IOException {

      StringBuffer sb = new StringBuffer();
      int i = 0;
      int k;

      for (int j = 0; (k = string.indexOf("\\20", j)) >= 0; j = k + 3) {
         sb.append(trim(string.substring(i, k)) + "\\ ");

         i = k + 3;
      }

      sb.append(string.substring(i));

      return sb.toString();
   }

   /**
    * Method semicolonToComma
    *
    * @param str
    * @return
    */
   static String semicolonToComma(String str) {
      return removeWSandReplace(str, ";", ",");
   }

   /**
    * Method removeWhiteSpace
    *
    * @param str
    * @param symbol
    * @return
    */
   static String removeWhiteSpace(String str, String symbol) {
      return removeWSandReplace(str, symbol, symbol);
   }

   /**
    * Method removeWSandReplace
    *
    * @param str
    * @param symbol
    * @param replace
    * @return
    */
   static String removeWSandReplace(String str, String symbol, String replace) {

      StringBuffer sb = new StringBuffer();
      int i = 0;
      int l = 0;
      int k;

      for (int j = 0; (k = str.indexOf(symbol, j)) >= 0; j = k + 1) {
         l += countQuotes(str, j, k);

         if ((k > 0) && (str.charAt(k - 1) != '\\') && (l % 2) != 1) {
            sb.append(trim(str.substring(i, k)) + replace);

            i = k + 1;
            l = 0;
         }
      }

      sb.append(trim(str.substring(i)));

      return sb.toString();
   }

   /**
    * Returns the number of Quotation from i to j
    *
    * @param s
    * @param i
    * @param j
    * @return
    */
   private static int countQuotes(String s, int i, int j) {

      int k = 0;

      for (int l = i; l < j; l++) {
         if (s.charAt(l) == '"') {
            k++;
         }
      }

      return k;
   }

   //only for the end of a space character occurring at the end of the string from rfc2253

   /**
    * Method trim
    *
    * @param str
    * @return
    */
   static String trim(String str) {

      String trimed = str.trim();
      int i = str.indexOf(trimed.substring(0)) + trimed.length();

      if ((str.length() > i) && trimed.endsWith("\\")
              &&!trimed.endsWith("\\\\")) {
         if (str.charAt(i) == ' ') {
            trimed = trimed + " ";
         }
      }

      return trimed;
   }

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {

      testToXML("CN=\"Steve, Kille\",  O=Isode Limited, C=GB");
      testToXML("CN=Steve Kille    ,   O=Isode Limited,C=GB");
      testToXML("\\ OU=Sales+CN=J. Smith,O=Widget Inc.,C=US\\ \\ ");
      testToXML("CN=L. Eagle,O=Sue\\, Grabbit and Runn,C=GB");
      testToXML("CN=Before\\0DAfter,O=Test,C=GB");
      testToXML("CN=\"L. Eagle,O=Sue, = + < > # ;Grabbit and Runn\",C=GB");
      testToXML("1.3.6.1.4.1.1466.0=#04024869,O=Test,C=GB");

      {
         StringBuffer sb = new StringBuffer();

         sb.append('L');
         sb.append('u');
         sb.append('\uc48d');
         sb.append('i');
         sb.append('\uc487');

         String test7 = "SN=" + sb.toString();

         testToXML(test7);
      }

      testToRFC("CN=\"Steve, Kille\",  O=Isode Limited, C=GB");
      testToRFC("CN=Steve Kille    ,   O=Isode Limited,C=GB");
      testToRFC("\\20OU=Sales+CN=J. Smith,O=Widget Inc.,C=US\\20\\20 ");
      testToRFC("CN=L. Eagle,O=Sue\\, Grabbit and Runn,C=GB");
      testToRFC("CN=Before\\12After,O=Test,C=GB");
      testToRFC("CN=\"L. Eagle,O=Sue, = + < > # ;Grabbit and Runn\",C=GB");
      testToRFC("1.3.6.1.4.1.1466.0=\\#04024869,O=Test,C=GB");

      {
         StringBuffer sb = new StringBuffer();

         sb.append('L');
         sb.append('u');
         sb.append('\uc48d');
         sb.append('i');
         sb.append('\uc487');

         String test7 = "SN=" + sb.toString();

         testToRFC(test7);
      }
   }

   /** Field i */
   static int i = 0;

   /**
    * Method test
    *
    * @param st
    */
   static void testToXML(String st) {

      System.out.println("start " + i++ + ": " + st);
      System.out.println("         " + rfc2253toXMLdsig(st));
      System.out.println("");
   }

   /**
    * Method testToRFC
    *
    * @param st
    */
   static void testToRFC(String st) {

      System.out.println("start " + i++ + ": " + st);
      System.out.println("         " + xmldsigtoRFC2253(st));
      System.out.println("");
   }
}
