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
package org.apache.xml.security.utils.resolver.implementations;



import java.io.FileInputStream;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.utils.URI;
import org.w3c.dom.Attr;


/**
 * A simple ResourceResolver for requests into the local filesystem.
 *
 * @author $Author$
 */
public class ResolverLocalFilesystem extends ResourceResolverSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    ResolverLocalFilesystem.class.getName());

   /**
    * Method resolve
    *
    * @param uri
    * @param BaseURI
    * @return
    * @throws ResourceResolverException
    * @todo calculate the correct URI from the attribute and the BaseURI
    */
   public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
           throws ResourceResolverException {

     try {
        URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());

        // if the URI contains a fragment, ignore it
        URI uriNewNoFrag = new URI(uriNew);

        uriNewNoFrag.setFragment(null);

        String fileName =
           ResolverLocalFilesystem
              .translateUriToFilename(uriNewNoFrag.toString());
        FileInputStream inputStream = new FileInputStream(fileName);
        XMLSignatureInput result = new XMLSignatureInput(inputStream);

        result.setSourceURI(uriNew.toString());

        return result;
     } catch (Exception e) {
        throw new ResourceResolverException("generic.EmptyMessage", e, uri,
                                            BaseURI);
      }
   }

   /**
    * Method translateUriToFilename
    *
    * @param uri
    * @return
    */
   private static String translateUriToFilename(String uri) {

      String subStr = uri.substring("file:/".length());

      if (subStr.indexOf("%20") > -1)
      {
        int offset = 0;
        int index = 0;
        StringBuffer temp = new StringBuffer(subStr.length());
        do
        {
          index = subStr.indexOf("%20",offset);
          if (index == -1) temp.append(subStr.substring(offset));
          else
          {
            temp.append(subStr.substring(offset,index));
            temp.append(' ');
            offset = index+3;
          }
        }
        while(index != -1);
        subStr = temp.toString();
      }

      if (subStr.charAt(1) == ':') {
      	 // we're running M$ Windows, so this works fine
         return subStr;
      }
      // we're running some UNIX, so we have to prepend a slash
      return "/" + subStr;
   }

   /**
    * Method engineCanResolve
    *
    * @param uri
    * @param BaseURI
    * @return
    */
   public boolean engineCanResolve(Attr uri, String BaseURI) {

      if (uri == null) {
         return false;
      }

      String uriNodeValue = uri.getNodeValue();

      if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
         return false;
      }

      try {
         URI uriNew = new URI(new URI(BaseURI), uri.getNodeValue());
         if (log.isDebugEnabled())
         	log.debug("I was asked whether I can resolve " + uriNew.toString());

         if (uriNew.getScheme().equals("file")) {
            if (log.isDebugEnabled())
            	log.debug("I state that I can resolve " + uriNew.toString());

            return true;
         }
      } catch (Exception e) {}

      log.debug("But I can't");

      return false;
   }
}
