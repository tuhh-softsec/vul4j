
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
package org.apache.xml.security.utils.resolver;


import java.util.Map;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;


/**
 * During reference validation, we have to retrieve resources from somewhere.
 *
 * @author $Author$
 */
public abstract class ResourceResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    ResourceResolverSpi.class.getName());

   /** Field _properties */
   protected java.util.Map _properties = new java.util.HashMap(10);

   /**
    * This is the workhorse method used to resolve resources.
    *
    * @param uri
    * @param BaseURI
    * @return
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
    * @return
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

   /**
    * 
    * @param properties
    */
   public void engineAddProperies(Map properties) {
      this._properties.putAll(properties);
   }

   /**
    * This method helps the {@link ResourceResolver} to decide whether a
    * {@link ResourceResolverSpi} is able to perform the requested action.
    *
    * @param uri
    * @param BaseURI
    * @return
    */
   public abstract boolean engineCanResolve(Attr uri, String BaseURI);

   /**
    * Method engineGetPropertyKeys
    *
    * @return
    */
   public String[] engineGetPropertyKeys() {
      return new String[0];
   }

   /**
    * Method understandsProperty
    *
    * @param propertyToTest
    * @return
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
    * Fixes a platform dependent filename to standard URI form.
    *
    * @param str The string to fix.
    *
    * @return Returns the fixed URI string.
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
            if (log.isDebugEnabled())
            	log.debug("Found DOS filename: " + str);
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
