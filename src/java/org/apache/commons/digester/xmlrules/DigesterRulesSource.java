/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.digester.xmlrules;


import org.apache.commons.digester.Digester;


/**
 * Interface for classes that initialize a Digester Rules object with
 * Digester Rules.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 */

public interface DigesterRulesSource {

    /**
     * Creates and adds Digester Rules to a given Rules object
     * @param digester the Digester to add the new Rule objects to
     */
    void getRules(Digester digester);

}

