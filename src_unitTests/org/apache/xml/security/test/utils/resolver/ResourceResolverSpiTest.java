
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
package org.apache.xml.security.test.utils.resolver;



import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 *
 *
 *
 *
 * @author $Author$
 *
 */
public class ResourceResolverSpiTest extends TestCase {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                    ResourceResolverSpiTest.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(ResourceResolverSpiTest.class);
   }

   /**
    *
    * @param Name_
    */
   public ResourceResolverSpiTest(String Name_) {
      super(Name_);
   }

   /**
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                ResourceResolverSpiTest.class.getName() };

      junit.textui.TestRunner.main(testCaseName);

      // junit.swingui.TestRunner.main(testCaseName);
   }



   static {
      org.apache.xml.security.Init.init();
   }
}
