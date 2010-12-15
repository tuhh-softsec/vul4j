
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
package org.apache.xml.security.samples;



import java.io.ByteArrayInputStream;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;


/**
 * This sample app describes how to register and use self-programmed
 * Transforms.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformNoneUser {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      Transform.register(
         "http://www.xmlsecurity.org/NS/Transforms#none",
         "org.apache.xml.security.samples.transforms.SampleTransformNone");

      Transforms identity = new Transforms(null, null);
      identity.addTransform("http://www.xmlsecurity.org/NS/Transforms#none");
      XMLSignatureInput input =
         new XMLSignatureInput(new ByteArrayInputStream("This is the Input"
            .getBytes()));
      XMLSignatureInput result = identity.performTransforms(input);

      System.out.println(new String(result.getBytes()));
   }
}
