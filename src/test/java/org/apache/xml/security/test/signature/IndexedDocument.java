/*
 * Copyright  1999-2009 The Apache Software Foundation.
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

package org.apache.xml.security.test.signature;

import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A quick and dirty document implementation, that
 * implements the getElementById function by looking at "Id" attributes.
 * 
 * @author wglas
 */
public class IndexedDocument extends DocumentImpl
{
    private static final long serialVersionUID = -1342041999864449753L;

    private static final String ID_ATTR = "Id";
    
    private Map idMap;
   
    private void addToIdMap(Element element)
    {
        String id = element.getAttribute(ID_ATTR);
        
        if (id != null)
            this.idMap.put(id,element);
        
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling())
        {
            if (node instanceof Element)
            {
                Element e = (Element) node;
                this.addToIdMap(e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.dom.CoreDocumentImpl#getElementById(java.lang.String)
     */
    public Element getElementById(String id)
    {
        if (this.idMap == null)
        {
            this.idMap = new HashMap();
            this.addToIdMap(this.getDocumentElement());
        }
        
        return (Element)this.idMap.get(id);
    }
    
}
