
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
package org.apache.xml.security.samples.transforms;



import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;


/**
 * Implements a null transform which leaved the input unmodified.
 *
 * @author Christian Geuer-Pollmann
 */
public class SampleTransformNone extends TransformSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    SampleTransformNone.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      "http://www.xmlsecurity.org/NS/Transforms#none";

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return SampleTransformNone.implementedTransformURI;
   }

   //J-
   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return true; }
   //J+

   /**
    * Method enginePerformTransform
    *
    * @param input
    *
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input) {
      return input;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
