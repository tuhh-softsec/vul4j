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
package org.apache.xml.security.keys.provider;



import java.io.*;
import java.util.*;
import java.security.Security;
import java.security.KeyStore;
import java.security.Key;
import java.security.cert.Certificate;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.utils.*;


/**
 *
 * @author $Author$
 */
public class ProviderTest {

   static {
      org.apache.xml.security.Init.init();
   }

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      String storePass = "passphrase";
      String storeFilename = "keystore.xml";

      Security
         .addProvider(new org.apache.xml.security.keys.provider
            .ApacheXMLProvider());

      KeyStore keyStore = KeyStore.getInstance("ApacheXML", "ApacheXML");
      boolean createKeyStore = true;

      if (createKeyStore) {
         keyStore.load(null, null);

         {
            String keystoreFile =
               "data/org/apache/xml/security/samples/input/keystore.jks";
            String keystorePass = "xmlsecurity";
            String certificateAlias = "test";
            KeyStore jks = KeyStore.getInstance("JKS");

            jks.load(new FileInputStream(keystoreFile),
                     keystorePass.toCharArray());

            Certificate cert = jks.getCertificate(certificateAlias);

            keyStore.setCertificateEntry(certificateAlias, cert);
         }

         {
            Key secretKey = new SecretKeySpec(
               HexDump.hexStringToByteArray(
               "0001020304050607 08090A0B0C0D0E0F"), "AES");

            keyStore.setKeyEntry("alias", secretKey, "pass".toCharArray(),
                                 null);
         }

         {
            String keystoreFile =
               "data/org/apache/xml/security/samples/input/keystore.jks";
            String keystorePass = "xmlsecurity";
            String certificateAlias = "test";
            KeyStore jks = KeyStore.getInstance("JKS");

            jks.load(new FileInputStream(keystoreFile),
                     keystorePass.toCharArray());

            Certificate cert = jks.getCertificate(certificateAlias);
            Certificate chain[] = new Certificate[1];

            chain[0] = cert;

            Key secretKey = new SecretKeySpec(
               HexDump.hexStringToByteArray(
               "0001020304050607 08090A0B0C0D0E0F"), "AES");

            keyStore.setKeyEntry("alias2", secretKey, "pass".toCharArray(), chain);
         }
      } else {
         keyStore.load(new FileInputStream(storeFilename),
                       storePass.toCharArray());

         // keyStore.load(new FileInputStream(storeFilename), null);
         System.out.println("The keyStore contains " + keyStore.size()
                            + " keys");
      }

      Enumeration aliases = keyStore.aliases();

      while (aliases.hasMoreElements()) {
         String alias = (String) aliases.nextElement();

         System.out.println("Alias = \"" + alias + "\"  "
                            + keyStore.getCreationDate(alias).toString());
      }

      keyStore.deleteEntry("alias2");

      // System.out.println("keyStore.isCertificateEntry(\"test\") = " + keyStore.isCertificateEntry("test"));
      // Certificate cert = keyStore.getCertificate("test");
      // System.out.println(cert);
      keyStore.store(new FileOutputStream(storeFilename), storePass.toCharArray());

      System.out.println(HexDump.byteArrayToHexString(keyStore.getKey("alias", "pass".toCharArray()).getEncoded()));

      //  keyStore.store(new FileOutputStream(storeFilename), null);
   }
}
