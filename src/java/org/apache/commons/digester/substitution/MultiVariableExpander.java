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

package org.apache.commons.digester.substitution;

import java.util.Map;
import java.util.ArrayList;

/**
 * <p>Expands variable references from multiple sources.</p>
 *
 * @since 1.6
 */

public class MultiVariableExpander implements VariableExpander {
    private int nEntries = 0;
    private ArrayList markers = new ArrayList(2);
    private ArrayList sources = new ArrayList(2);
    
    public MultiVariableExpander() {
    }
    
    public void addSource(String marker, Map source) {
        ++nEntries;
        markers.add(marker);
        sources.add(source);
    }

    /*    
     * Expands any variable declarations using any of the known
     * variable marker strings.
     * 
     * @throws IllegalArgumentException if the input param references
     * a variable which is not known to the specified source.
     */
    public String expand(String param) {
        for(int i=0; i<nEntries; ++i) {
            param = expand(
                param, 
                (String) markers.get(i), 
                (Map) sources.get(i));
        }
        return param;
    }
    
    /**
     * Replace any occurrences within the string of the form
     * "marker{key}" with the value from source[key].
     * <p>
     * Commonly, the variable marker is "$", in which case variables
     * are indicated by ${key} in the string.
     * <p>
     * Returns the string after performing all substitutions.
     * <p>
     * If no substitutions were made, the input string object is
     * returned (not a copy).
     *
     * @throws IllegalArgumentException if the input param references
     * a variable which is not known to the specified source.
     */
    public String expand(String str, String marker, Map source) {
        String startMark = marker + "{";
        int markLen = startMark.length();
        
        int index = 0;
        for(;;)
        {
            index = str.indexOf(startMark, index);
            if (index == -1)
            {
                return str;
            }
            
            int startIndex = index + markLen;
            if (startIndex > str.length())
            {
                throw new IllegalArgumentException(
                    "var expression starts at end of string");
            }
            
            int endIndex = str.indexOf("}", index + markLen);
            if (endIndex == -1)
            {
                throw new IllegalArgumentException(
                    "var expression starts but does not end");
            }
            
            String key = str.substring(index+markLen, endIndex);
            Object value =  source.get(key);
            if (value == null) {
                throw new IllegalArgumentException(
                    "parameter [" + key + "] is not defined.");
            }
            String varValue = value.toString();
            
            str = str.substring(0, index) + varValue + str.substring(endIndex+1);
            index += varValue.length();
        }
    }
        
}
