/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

/**
 *
 * @author $Author$
 */

public class Version {

    /** Version string. */
    public static String fVersion = "@@VERSION@@";

   private Version() {
     // we don't allow instantiation
   }

   /**
    * version
    * @return
    */
    public static final String getVersion() {
       return Version.fVersion;
    }

    /**
     * Prints out the version number to System.out. This is needed
     * for the build system.
     * @param argv
     */
    public static void main(String argv[]) {
        System.out.println(org.apache.xml.security.utils.Version.getVersion());
    }
}