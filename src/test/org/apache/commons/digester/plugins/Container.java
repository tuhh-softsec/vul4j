/* $Id: Container.java,v 1.5 2004/05/07 01:30:00 skitching Exp $
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

package org.apache.commons.digester.plugins;

import java.util.List;
import java.util.LinkedList;

public class Container implements Widget {
    private LinkedList children = new LinkedList();

    public Container() {}
    
    public void addChild(Widget child) {
        children.add(child);
    }

    public List getChildren() {
        return children;
    }
}
