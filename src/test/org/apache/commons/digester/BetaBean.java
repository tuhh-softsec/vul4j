/* $Id: BetaBean.java,v 1.6 2004/05/07 01:29:59 skitching Exp $
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

package org.apache.commons.digester;

public class BetaBean implements Nameable {
    private String name = "BETA";
    
    private Nameable child;
    private Nameable parent;

    public BetaBean() {}
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setParent(Nameable parent) {
        this.parent = parent;
    }
    
    public Nameable getParent() {
        return parent;
    }
    
    public void setChild(Nameable child) {
        this.child = child;
    }
    
    public Nameable getChild() {
        return child;
    }
    
    public String toString()
    {
        return "[BetaBean] name=" + name + " child=" + child;
    }
}
