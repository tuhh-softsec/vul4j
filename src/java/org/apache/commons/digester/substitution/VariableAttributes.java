/* $Id: VariableAttributes.java,v 1.5 2004/05/10 06:30:08 skitching Exp $
 *
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

import org.xml.sax.Attributes;

import java.util.Map;
import java.util.ArrayList;


/**
 * <p>Wrapper for an org.xml.sax.Attributes object which expands any 
 * "variables" referenced in the attribute value via ${foo} or similar. 
 * This is only done something actually asks for the attribute value, 
 * thereby imposing no performance penalty if the attribute is not used.</p>
 *
 * @since 1.6
 */

public class VariableAttributes implements Attributes {

    // list of mapped attributes.
    private ArrayList values = new ArrayList(10);

    private Attributes attrs;
    private VariableExpander expander;
    
    // ------------------- Public Methods
    
    /**
     * Specify which attributes class this object is a proxy for.
     */
    public void init(Attributes attrs, VariableExpander expander) {
        this.attrs = attrs;
        this.expander = expander;

        // I hope this doesn't release the memory for this array; for 
        // efficiency, this should just mark the array as being size 0.
        values.clear(); 
    }

    public String getValue(int index) {
        if (index >= values.size()) {
            // Expand the values array with null elements, so the later
            // call to set(index, s) works ok.
            //
            // Unfortunately, there is no easy way to set the size of
            // an arraylist; we must repeatedly add null elements to it..
            values.ensureCapacity(index+1);
            for(int i = values.size(); i<= index; ++i) {
                values.add(null);
            }
        }
        
        String s = (String) values.get(index);
        
        if (s == null) {
            // we have never been asked for this value before.
            // get the real attribute value and perform substitution
            // on it.
            s = attrs.getValue(index);
            if (s != null) {
                s = expander.expand(s);
                values.set(index, s);
            }
        }
        
        return s;
    }
    
    public String getValue(String qname) {
        int index = attrs.getIndex(qname);
        if (index == -1) {
            return null;
        }
        return getValue(index);
    }
    
    public String getValue(String uri, String localname) {
        int index = attrs.getIndex(uri, localname);
        if (index == -1) {
            return null;
        }
        return getValue(index);
    }
    
    // plain proxy methods follow : nothing interesting :-)
    public int getIndex(String qname) {
        return attrs.getIndex(qname); 
    }
    
    public int getIndex(String uri, String localpart) {
        return attrs.getIndex(uri, localpart); 
    }
    
    public int getLength() {
        return attrs.getLength();
    }
    
    public String getLocalName(int index) {
        return attrs.getLocalName(index);
    }
    
    public String getQName(int index) {
        return attrs.getQName(index);
    }
    
    public String getType(int index) {
        return attrs.getType(index);
    }

    public String getType(String qname) {
        return attrs.getType(qname);
    }
    
    public String getType(String uri, String localname) {
        return attrs.getType(uri, localname);
    }
    
    public String getURI(int index) {
        return attrs.getURI(index);
    }
 
}
