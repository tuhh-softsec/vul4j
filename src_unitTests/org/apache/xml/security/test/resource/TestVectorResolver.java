
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
package org.apache.xml.security.test.resource;



import java.io.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.xml.security.utils.Constants;


/**
 * This package is responsible for retrieving test vectors for our unit tests.
 *
 * @author Christian Geuer-Pollmann
 * @todo Currently, the test vectors are in the file system under the data/ directory. It is planned to put them all into a single jar/zip which is deployed with the library.
 */
public class TestVectorResolver implements EntityResolver {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(TestVectorResolver.class.getName());

   /** Field _firstEntityResolved */
   boolean _firstEntityResolved = false;

   /** Field _firstEntitySystemId */
   String _firstEntitySystemIdDirectory = null;

   /**
    * Method getCurrentDir
    *
    * @return
    * @throws IOException
    */
   private String getCurrentDir() throws IOException {

      String currentDir = new java.io.File(".").getCanonicalPath();

      currentDir = currentDir.replace(File.separatorChar, '/');
      currentDir = "file:///" + currentDir + "/";

      return currentDir;
   }

   /**
    * Method getFileName
    *
    * @param systemId
    * @return
    * @throws IOException
    */
   private String getFileName(String systemId) throws IOException {

      // clean up file name
      String currentDir = getCurrentDir();

      if (systemId.startsWith(currentDir)) {
         return systemId.substring(currentDir.length());
      } else {
         return systemId;
      }
   }

   /**
    * Method getFilePath
    *
    * @param systemId
    * @return
    * @throws IOException
    */
   private String getFilePath(String systemId) throws IOException {

      String t = new File(systemId).getCanonicalPath();

      t = t.replace(File.separatorChar, '/');
      t = "file:///" + t;

      String currentDir = getCurrentDir();

      if (t.startsWith(currentDir)) {
         t = t.substring(currentDir.length());
      }

      t = t.substring(0, t.lastIndexOf("/"));

      return t;
   }

   /**
    * Method set1stSystemId
    *
    * @param systemId
    * @throws IOException
    */
   private void set1stSystemId(String systemId) throws IOException {

      this._firstEntitySystemIdDirectory = getFilePath(systemId);

      cat.debug("this._firstEntitySystemIdDirectory = "
                + this._firstEntitySystemIdDirectory);
   }

   /**
    * Method getInputSource
    *
    * @param systemId
    * @return
    */
   private InputStream getInputStream(String systemId) {

      cat.debug("getInputStream(" + systemId + ")");

      // we don't use the file system but the ZIP resource
      // return new InputSource(new FileInputStream(systemId));
      byte result[] = (byte[]) TestVectorResolver.vectors.get(systemId);

      if (result == null) {
         cat.fatal("requested " + systemId + " resulted in null");
      }

      return new ByteArrayInputStream(result);
   }

   /**
    * Method resolveEntity
    *
    * @param publicId
    * @param systemId
    * @return
    */
   public InputSource resolveEntity(String publicId, String systemId) {

      try {
         if (!this._firstEntityResolved) {
            this.set1stSystemId(systemId);
         }

         systemId = this.getFileName(systemId);

         cat.debug("publicId=\"" + publicId + "\" systemId=\"" + systemId
                   + "\"");

         // InputStream result = this.getInputStream(systemId);
         // return new InputSource(result);
         return new InputSource(new FileInputStream(systemId));
      } catch (FileNotFoundException ex) {
         return null;
      } catch (IOException ex) {
         return null;
      }
   }

   /** Field alreadyInitialized */
   static boolean alreadyInitialized = false;

   /** Field zis */
   static java.util.zip.ZipInputStream zis = null;

   /** Field vectors */
   static java.util.HashMap vectors = null;

   /**
    * Method init
    *
    */
   public static void init() {

      String thisClass =
         "org.apache.xml.security.test.resource.TestVectorResolver";
      String testVectorFile = "testvectors.zip";

      if (!TestVectorResolver.alreadyInitialized) {
         TestVectorResolver.alreadyInitialized = true;
         TestVectorResolver.vectors = new java.util.HashMap(30);

         try {
            zis = new java.util.zip
               .ZipInputStream(Class.forName(thisClass)
                  .getResourceAsStream(testVectorFile));

            java.util.zip.ZipEntry ze = null;

            while ((ze = zis.getNextEntry()) != null) {
               if (!ze.isDirectory()) {
                  byte data[] =
                     org.apache.xml.security.utils.JavaUtils
                        .getBytesFromStream(zis);

                  TestVectorResolver.vectors.put(ze.getName(), data);
                  cat.debug("Contents of " + thisClass + "/" + testVectorFile
                            + "#" + ze.getName() + " " + data.length
                            + " bytes");
               }
            }
         } catch (java.lang.ClassNotFoundException e) {}
         catch (java.io.IOException e) {}
      }
   }

   static {
      org.apache.xml.security.Init.init();
      TestVectorResolver.init();
   }
}
