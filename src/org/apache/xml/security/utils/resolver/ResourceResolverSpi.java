
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
package org.apache.xml.security.utils.resolver;


import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.signature.XMLSignatureInput;


/**
 * During reference validation, we have to retrieve resources from somewhere.
 *
 * @author $Author$
 */
public abstract class ResourceResolverSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(ResourceResolverSpi.class.getName());

   /** Field _properties */
   protected java.util.Map _properties = new java.util.HashMap(10);

   /**
    * This is the workhorse method used to resolve resources.
    *
    * @param uri
    * @param BaseURI
    *
    * @throws ResourceResolverException
    */
   public abstract XMLSignatureInput engineResolve(Attr uri, String BaseURI)
      throws ResourceResolverException;

   /**
    * Method engineSetProperty
    *
    * @param key
    * @param value
    */
   public void engineSetProperty(String key, String value) {

      java.util.Iterator i = this._properties.keySet().iterator();

      while (i.hasNext()) {
         String c = (String) i.next();

         if (c.equals(key)) {
            key = c;

            break;
         }
      }

      this._properties.put(key, value);
   }

   /**
    * Method engineGetProperty
    *
    * @param key
    *
    */
   public String engineGetProperty(String key) {

      java.util.Iterator i = this._properties.keySet().iterator();

      while (i.hasNext()) {
         String c = (String) i.next();

         if (c.equals(key)) {
            key = c;

            break;
         }
      }

      return (String) this._properties.get(key);
   }

   public void engineAddProperies(Map properties) {
      this._properties.putAll(properties);
   }

   /**
    * This method helps the {@link ResourceResolver} to decide whether a
    * {@link ResourceResolverSpi} is able to perform the requested action.
    *
    * @param uri
    * @param BaseURI
    *
    */
   public abstract boolean engineCanResolve(Attr uri, String BaseURI);

   /**
    * Method engineGetPropertyKeys
    *
    *
    */
   public String[] engineGetPropertyKeys() {
      return new String[0];
   }

   /**
    * Method understandsProperty
    *
    * @param propertyToTest
    *
    */
   public boolean understandsProperty(String propertyToTest) {

      String[] understood = this.engineGetPropertyKeys();

      if (understood != null) {
         for (int i = 0; i < understood.length; i++) {
            if (understood[i].equals(propertyToTest)) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Expands a system id and returns the system id as a URL, if
    * it can be expanded. A return value of null means that the
    * identifier is already expanded. An exception thrown
    * indicates a failure to expand the id.
    *
    * @param systemId The systemId to be expanded.
    * @param currentSystemId
    *
    * @return Returns the URL object representing the expanded system
    *         identifier. A null value indicates that the given
    *         system identifier is already expanded.
    *
    * @throws Exception
    * @see org.apache.xerces.validators.schema.TraverseSchema
    */
   public static String expandSystemId(String systemId, String currentSystemId)
           throws Exception {

      String id = systemId;

      // check for bad parameters id
      if ((id == null) || (id.length() == 0)) {
         return systemId;
      }

      // if id already expanded, return
      try {
         URI url = new URI(id);

         if (url != null) {
            return systemId;
         }
      } catch (Exception e) {

         // continue on...
      }

      // normalize id
      id = ResourceResolverSpi.fixURI(id);

      // normalize base
      URI base = null;
      URI url = null;

      try {
         if (currentSystemId == null) {
            String dir;

            try {
               dir = ResourceResolverSpi.fixURI(System.getProperty("user.dir"));
            } catch (SecurityException se) {
               dir = "";
            }

            if (!dir.endsWith("/")) {
               dir = dir + "/";
            }

            final String protocol = "file";
            final String host = "";

            base = new URI(protocol, host, dir, null, null);
         } else {

            // should we fix currentSystemId?
            currentSystemId = ResourceResolverSpi.fixURI(currentSystemId);
            base = new URI(currentSystemId);
         }

         // expand id
         url = new URI(base, id);
      } catch (Exception e) {

         // let it go through
      }

      if (url == null) {
         return systemId;
      }

      return url.toString();
   }

   /**
    * Method makeFilesystemToURI
    *
    * @param str
    *
    */
   public static String makeFilesystemToURI(String str) {

      final String filePrefix = "file:/";

      return "";
   }

   /**
    * Method isDosFilename
    *
    * @param str
    *
    */
   private static boolean isDosFilename(String str) {

      if (str.length() >= 4) {

         // str =~ /^\W:\/([^/])/ # to speak perl ;-))
         //J-
         char ch0 = Character.toUpperCase(str.charAt(0));
         boolean isDriveLetter     = (('A' <= ch0) && (ch0 <= 'Z'));
         boolean isColon           = str.charAt(1) == ':';
         boolean isSlashAfterColon = str.charAt(2) == '/';
         boolean isOnlyOneSlash    = str.charAt(3) != '/';
         boolean isDosFilename = (isDriveLetter && isColon &&
                                  isSlashAfterColon && isOnlyOneSlash);
         //J+
         return isDosFilename;
      }

      return false;
   }

   /**
    * Fixes a platform dependent filename to standard URI form.
    *
    * @param str The string to fix.
    *
    * @return Returns the fixed URI string.
    * @see org.apache.xerces.validators.schema.TraverseSchema
    */
   public static String fixURI(String str) {

      // handle platform dependent strings
      str = str.replace(java.io.File.separatorChar, '/');

      if (str.length() >= 4) {

         // str =~ /^\W:\/([^/])/ # to speak perl ;-))
         char ch0 = Character.toUpperCase(str.charAt(0));
         char ch1 = str.charAt(1);
         char ch2 = str.charAt(2);
         char ch3 = str.charAt(3);
         boolean isDosFilename = ((('A' <= ch0) && (ch0 <= 'Z'))
                                  && (ch1 == ':') && (ch2 == '/')
                                  && (ch3 != '/'));

         if (isDosFilename) {
            cat.debug("Found DOS filename: " + str);
         }
      }

      // Windows fix
      if (str.length() >= 2) {
         char ch1 = str.charAt(1);

         if (ch1 == ':') {
            char ch0 = Character.toUpperCase(str.charAt(0));

            if (('A' <= ch0) && (ch0 <= 'Z')) {
               str = "/" + str;
            }
         }
      }

      // done
      return str;
   }
}
