
/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.keys.storage.implementations;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xml.security.keys.storage.StorageResolverException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;
import org.apache.xml.security.utils.Base64;


/**
 * This {@link StorageResolverSpi} makes all raw (binary) {@link X509Certificate}s
 * which reside as files in a single directory available to the {@link StorageResolver}.
 *
 * @author $Author$
 */
public class CertsInFilesystemDirectoryResolver extends StorageResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    CertsInFilesystemDirectoryResolver.class.getName());

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
            log.debug("Could not add certificate from file " + filename, ex);
         } catch (IOException ex) {
            log.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateNotYetValidException ex) {
            log.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateExpiredException ex) {
            log.debug("Could not add certificate from file " + filename, ex);
         } catch (CertificateException ex) {
            log.debug("Could not add certificate from file " + filename, ex);
         }

         if (added) {
            log.debug("Added certificate: " + dn);
         }
      }
   }

   /**
    * Method getIterator
    *
    *
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
       *
       */
      public boolean hasNext() {
         return (this._i < this._certs.size());
      }

      /**
       * Method next
       *
       *
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
