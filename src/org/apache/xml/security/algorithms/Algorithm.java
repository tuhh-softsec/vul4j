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
package org.apache.xml.security.algorithms;



import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The Algorithm class which stores the Algorithm URI as a string.
 *
 */
public abstract class Algorithm extends ElementProxy {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Algorithm.class.getName());

   /**
    *
    * @param doc
    * @param algorithmURI is the URI of the algorithm as String
    */
   public Algorithm(Document doc, String algorithmURI) {

      super(doc);

      this.setAlgorithmURI(algorithmURI);
   }

   /**
    * Constructor Algorithm
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public Algorithm(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getAlgorithmURI
    *
    * @return
    */
   public String getAlgorithmURI() {
      return this._constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
   }

   /**
    * Sets the algorithm's URI as used in the signature.
    *
    * @param algorithmURI is the URI of the algorithm as String
    */
   protected void setAlgorithmURI(String algorithmURI) {

      if ((this._state == MODE_CREATE) && (algorithmURI != null)) {
         this._constructionElement.setAttributeNS(null, Constants._ATT_ALGORITHM,
                                                algorithmURI);
      }
   }
}
