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

package org.apache.commons.digester;


/**
 * Regular expression matching strategy for RegexRules.
 * 
 * @see RegexRules
 * @author Robert Burrell Donkin
 * @version $Revision: 1.6 $ $Date: 2004/02/28 13:32:52 $
 */

abstract public class RegexMatcher {
    
    /** 
     * Returns true if the given pattern matches the given path 
     * according to the regex algorithm that this strategy applies.
     *
     * @param pathPattern the standard digester path representing the element
     * @param rulePattern the regex pattern the path will be tested against
     * @return true if the given pattern matches the given path
     */
    abstract public boolean match(String pathPattern, String rulePattern);
    
}
