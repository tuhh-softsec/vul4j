
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
package org.apache.xml.security.keys.storage.implementations;



import java.io.*;
import java.util.*;
import java.security.cert.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.utils.*;


/**
 * This {@link StorageResolverSpi} makes all raw (binary) {@link X509Certificate}s
 * which reside as files in a single directory available to the {@link StorageResolver}.
 *
 * @author $Author$
 */
public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi {

   /** Field cat */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(CertsInFilesystemDirectoryResolver.class.getName());

   /** Field _merlinsCertificatesDir */
   String _merlinsCertificatesDir = null;

   /** Field _certs */
   private Vector _certs = new Vector();

   /** Field _iterator */
   Iterator _iterator = null;

   /**
    *
    *
    * @param directoryName
    * @throws StorageResolverException
    */
   public CertsInFilesystemDirectoryResolver(String directoryName)
           throws StorageResolverException {

      this._merlinsCertificatesDir = directoryName;

      this.readCertsFromHarddrive();

      this._iterator = new FilesystemIterator(this._certs);
   }

   /**
    * Method readCertsFromHarddrive
    *
    * @throws StorageResolverException
    */
   private void readCertsFromHarddrive() throws StorageResolverException {

      File certDir = new File(this._merlinsCertificatesDir);
      ArrayList al = new ArrayList();
      String[] names = certDir.list();

      for (int i = 0; i < names.length; i++) {
         String currentFileName = names[i];

         if (currentFileName.endsWith(".crt")) {
            al.add(names[i]);
         }
      }

      CertificateFactory cf = null;

      try {
         cf = CertificateFactory.getInstance("X.509");
      } catch (CertificateException ex) {
         throw new StorageResolverException("empty", ex);
      }

      if (cf == null) {
         throw new StorageResolverException("empty");
      }

      for (int i = 0; i < al.size(); i++) {
         String filename = certDir.getAbsolutePath() + File.separator
                           + (String) al.get(i);
         File file = new File(filename);
         boolean added = false;
         String dn = null;

         try {
            FileInputStream fis = new FileInputStream(file);
            X509Certificate cert =
               (X509Certificate) cf.generateCertificate(fis);

            fis.close();

            //add to ArrayList
            cert.checkValidity();
            this._certs.add(cert);

            dn = cert.getSubjectDN().getName();
            added = true;
         } catch (FileNotFoundException ex) {
            cat.debug("Could not add certificate from file " + filename, ex);
         } catch (IOException ex) {
            cat.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateNotYetValidException ex) {
            cat.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateExpiredException ex) {
            cat.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateException ex) {
            cat.debug("Could not add certificate from file " + filename, ex);
         }

         if (added) {
            cat.debug("Added certificate: " + dn);
         }
      }
   }

   /**
    * Method getIterator
    *
    * @return
    */
   public Iterator getIterator() {
      return this._iterator;
   }

   /**
    * Class FilesystemIterator
    *
    * @author $Author$
    * @version $Revision$
    */
   class FilesystemIterator implements Iterator {

      /** Field _certs */
      Vector _certs = null;

      /** Field _i */
      int _i;

      /**
       * Constructor FilesystemIterator
       *
       * @param certs
       * @throws StorageResolverException
       */
      public FilesystemIterator(Vector certs) throws StorageResolverException {
         this._certs = certs;
         this._i = 0;
      }

      /**
       * Method hasNext
       *
       * @return
       */
      public boolean hasNext() {
         return (this._i < this._certs.size());
      }

      /**
       * Method next
       *
       * @return
       */
      public Object next() {
         return this._certs.elementAt(this._i++);
      }

      /**
       * Method remove
       *
       */
      public void remove() {
         throw new UnsupportedOperationException(
            "Can't remove keys from KeyStore");
      }
   }

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      CertsInFilesystemDirectoryResolver krs =
         new CertsInFilesystemDirectoryResolver(
            "data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs");

      for (Iterator i = krs.getIterator(); i.hasNext(); ) {
         X509Certificate cert = (X509Certificate) i.next();
         byte[] ski =
            org.apache.xml.security.keys.content.x509.XMLX509SKI
               .getSKIBytesFromCert(cert);

         System.out.println();
         System.out.println("Base64(SKI())=                 \""
                            + Base64.encode(ski) + "\"");
         System.out.println("cert.getSerialNumber()=        \""
                            + cert.getSerialNumber().toString() + "\"");
         System.out.println("cert.getSubjectDN().getName()= \""
                            + cert.getSubjectDN().getName() + "\"");
         System.out.println("cert.getIssuerDN().getName()=  \""
                            + cert.getIssuerDN().getName() + "\"");
      }
   }
}
