
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
package org.apache.xml.security.test.resource;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


/**
 * This package is responsible for retrieving test vectors for our unit tests.
 *
 * @author Christian Geuer-Pollmann
 * $todo$ Currently, the test vectors are in the file system under the data/ directory. It is planned to put them all into a single jar/zip which is deployed with the library.
 */
public class TestVectorResolver implements EntityResolver {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
			TestVectorResolver.class.getName());

   /** Field _firstEntityResolved */
   boolean _firstEntityResolved = false;

   /** Field _firstEntitySystemId */
   String _firstEntitySystemIdDirectory = null;

   /**
    * Method getCurrentDir
    *
    *
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
    *
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
    *
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

      log.debug("this._firstEntitySystemIdDirectory = "
                + this._firstEntitySystemIdDirectory);
   }

   /**
    * Method getInputSource
    *
    * @param systemId
    *
    */
   private InputStream getInputStream(String systemId) {

      log.debug("getInputStream(" + systemId + ")");

      // we don't use the file system but the ZIP resource
      // return new InputSource(new FileInputStream(systemId));
      byte result[] = (byte[]) TestVectorResolver.vectors.get(systemId);

      if (result == null) {
         log.fatal("requested " + systemId + " resulted in null");
      }

      return new ByteArrayInputStream(result);
   }

   /**
    * Method resolveEntity
    *
    * @param publicId
    * @param systemId
    *
    */
   public InputSource resolveEntity(String publicId, String systemId) {

      try {
         if (!this._firstEntityResolved) {
            this.set1stSystemId(systemId);
         }

         systemId = this.getFileName(systemId);

         log.debug("publicId=\"" + publicId + "\" systemId=\"" + systemId
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
                  log.debug("Contents of " + thisClass + "/" + testVectorFile
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
