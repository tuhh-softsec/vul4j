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
import java.util.Vector;


/**
 * A collection of different, general-purpose methods for JAVA-specific things
 * @author Christian Geuer-Pollmann
 *
 */
public class JavaUtils {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(JavaUtils.class.getName());

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
            cat.debug("writeBytesToFilename got null byte[] pointed");
         }
      } catch (Exception ex) {}
   }

   /**
    * This method reads all bytes from the given InputStream till EOF and returns
    * them as a byte array.
    *
    * @param inputStream
    * @return
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

      cat.debug("<METHOD name=runGC()>");

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

      cat.debug("* Garbage collection took " + time + " seconds.");
      cat.debug("* Memory before gc()... free:" + lFreeMemBefore + "= "
                + lFreeMemBefore / 1024 + "KB,...total:" + lTotalMemBefore
                + "= " + lTotalMemBefore / 1024 + "KB,...  used:"
                + (lTotalMemBefore - lFreeMemBefore) + "= "
                + (lTotalMemBefore - lFreeMemBefore) / 1024 + "KB");
      cat.debug("* Memory after: gc()... free:" + lFreeMemAfter + "= "
                + lFreeMemAfter / 1024 + "KB,...total:" + lTotalMemAfter + "= "
                + lTotalMemAfter / 1024 + "KB,...  used:"
                + (lTotalMemAfter - lFreeMemAfter) + "= "
                + (lTotalMemAfter - lFreeMemAfter) / 1024 + "KB");
      cat.debug("</METHOD>");
   }
}
