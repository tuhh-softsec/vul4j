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
package org.apache.xml.security.utils;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;


/**
 * A collection of different, general-purpose methods for JAVA-specific things
 * @author Christian Geuer-Pollmann
 *
 */
public class JavaUtils {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(JavaUtils.class.getName());

   private JavaUtils() {
     // we don't allow instantiation
   }

   /**
    * Checks whether an object implements a specific interface.
    *
    * <pre>
    * org.w3c.dom.Document doc = ...; // some init
    *
    * boolean this_is_true =
    *    JavaUtils.implementsInterface(doc.getDocumentElement(),
    *                                  "org.w3c.dom.Element");
    *
    * boolean this_is_true_too =
    *    JavaUtils.implementsInterface(doc.getDocumentElement(),
    *                                  "org.w3c.dom.Node");
    *
    * </pre>
    *
    * @param object which is to be checked
    * @param interfaceName is the String of the Interface
    * @return <code>true</code> if the object implements the specified interface
    */
   public static boolean implementsInterface(Object object,
                                             String interfaceName) {

      Vector allClasses = new Vector();
      Vector allInterfaces = new Vector();
      Class c = object.getClass();

      while (!c.getName().equals("java.lang.Object")) {
         Class interfaces[] = c.getInterfaces();
         String className = c.getName();

         allClasses.add(className);

         for (int i = 0; i < interfaces.length; i++) {
            String ifName = interfaces[i].getName();

            allInterfaces.add(ifName);
         }

         c = c.getSuperclass();
      }

      for (int i = 0; i < allInterfaces.size(); i++) {
         if (((String) allInterfaces.get(i)).equals(interfaceName)) {
            return true;
         }
      }

      return false;
   }

   /**
    *
    * @param object
    * @param className
    * @return
    */
   public static boolean instanceOf(Object object, String className) {

      if (object.getClass().getName().equals(className)) {
         return true;
      }

      return implementsInterface(object, className);
   }

   /**
    * Returns true if both byte arrays are bytewise equal, false if the differ.
    *
    * @param refBytes
    * @param c14nBytes
    * @return true if both byte arrays are bytewise equal, false if the differ.
    * @see java.security.MessageDigest#isEqual
    */
   public static boolean binaryCompare(byte refBytes[], byte c14nBytes[]) {

      /*
      {
         if (refBytes.length != c14nBytes.length) {
            return false;
         }

         for (int i = 0; i < refBytes.length; i++) {
            if (refBytes[i] != c14nBytes[i]) {
               return false;
            }
         }
      }
      return true;
      */
      return java.security.MessageDigest.isEqual(refBytes, c14nBytes);
   }

   /*
    * Checks whether an object extends a specific class.
    *
    * @param object which is to be checked
    * @param className is the String of the Class
    * @return <code>true</code> if the object extends the specified class
    * public static boolean extendsClass(Object object,
    *                                  String className) {
    *
    *  String cn = object.getClass().getName();
    *  while (!cn.equals("java.lang.object")) {
    *     Object o = null;
    *     try {
    *     o = Class.forName(cn);
    *     Class superC = o.getClass().getSuperclass();
    *     cn = superC.getName();
    *     } catch (Exception e) {}
    *  }
    *
    *  Class ancestors[] = object.getClass().getDeclaredClasses();
    *
    *  for (int i=0; i<ancestors.length; i++) {
    *     System.out.println(i + " " + ancestors[i].getName());
    *  }
    *
    *  for (int i = 0; i < ancestors.length; i++) {
    *     if (ancestors[i].getName().equals(className)) {
    *        return true;
    *     }
    *  }
    *
    *  return false;
    * }
    */

   /*
   public static boolean extendsClassOrImplementsInterface(Object object,
                                             String name) {
       return (extendsClass(object, name) ||
               implementsInterface(object, name));
   }
   */

   /**
    * Method getBytesFromFile
    *
    * @param fileName
    * @return
    *
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static byte[] getBytesFromFile(String fileName)
           throws FileNotFoundException, IOException {

      byte refBytes[] = null;

      {
         FileInputStream fisRef = new FileInputStream(fileName);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte buf[] = new byte[1024];
         int len;

         while ((len = fisRef.read(buf)) > 0) {
            baos.write(buf, 0, len);
         }

         refBytes = baos.toByteArray();
      }

      return refBytes;
   }

   /**
    * Method writeBytesToFilename
    *
    * @param filename
    * @param bytes
    */
   public static void writeBytesToFilename(String filename, byte[] bytes) {

      try {
         if (filename != null && bytes != null) {
            File f = new File(filename);

            FileOutputStream fos = new FileOutputStream(f);

            fos.write(bytes);
            fos.close();
         } else {
            log.debug("writeBytesToFilename got null byte[] pointed");
         }
      } catch (Exception ex) {}
   }

   /**
    * This method reads all bytes from the given InputStream till EOF and returns
    * them as a byte array.
    *
    * @param inputStream
    * @return
    *
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {

      byte refBytes[] = null;

      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte buf[] = new byte[1024];
         int len;

         while ((len = inputStream.read(buf)) > 0) {
            baos.write(buf, 0, len);
         }

         refBytes = baos.toByteArray();
      }

      return refBytes;
   }

   /**
    * Method runGC
    *
    */
   public static void runGC() {

      log.debug("<METHOD name=runGC()>");

      Runtime runtime = Runtime.getRuntime();
      long lFreeMemBefore = runtime.freeMemory();
      long lTotalMemBefore = runtime.totalMemory();
      long lStart = System.currentTimeMillis();

      runtime.gc();
      runtime.runFinalization();

      long lEnd = System.currentTimeMillis();
      double time = (lEnd - lStart) / 1000.0;
      long lFreeMemAfter = runtime.freeMemory();
      long lTotalMemAfter = runtime.totalMemory();

      if (log.isDebugEnabled()) {
      	log.debug("* Garbage collection took " + time + " seconds.");
      	log.debug("* Memory before gc()... free:" + lFreeMemBefore + "= "
                + lFreeMemBefore / 1024 + "KB,...total:" + lTotalMemBefore
                + "= " + lTotalMemBefore / 1024 + "KB,...  used:"
                + (lTotalMemBefore - lFreeMemBefore) + "= "
                + (lTotalMemBefore - lFreeMemBefore) / 1024 + "KB");
      	log.debug("* Memory after: gc()... free:" + lFreeMemAfter + "= "
                + lFreeMemAfter / 1024 + "KB,...total:" + lTotalMemAfter + "= "
                + lTotalMemAfter / 1024 + "KB,...  used:"
                + (lTotalMemAfter - lFreeMemAfter) + "= "
                + (lTotalMemAfter - lFreeMemAfter) / 1024 + "KB");
      	log.debug("</METHOD>");
      }
   }
}
