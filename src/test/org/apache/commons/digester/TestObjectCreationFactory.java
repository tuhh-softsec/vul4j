/* $Id: TestObjectCreationFactory.java,v 1.6 2004/05/07 01:29:59 skitching Exp $
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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Object creation factory used for testing.
 *
 * @author Robert Burrell Donkin
 */

public class TestObjectCreationFactory extends AbstractObjectCreationFactory {
    public boolean called = false;
    public Attributes attributes;
    
    public Object createObject(Attributes attributes) {
        this.attributes = new AttributesImpl(attributes);
        called = true;
        return this;
    }
}



