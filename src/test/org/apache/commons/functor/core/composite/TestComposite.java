/*
 * Copyright 2003,2004 The Apache Software Foundation.
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
package org.apache.commons.functor.core.composite;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.NoOp;
import org.apache.commons.functor.core.comparator.IsGreaterThan;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestComposite extends TestCase {

    // Conventional
    // ------------------------------------------------------------------------

    public TestComposite(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComposite.class);
    }

    // Tests
    // ------------------------------------------------------------------------
    
    public void testHasNoArgConstructor() throws Exception {
        assertNotNull(new Composite());
    }

    public void testUnaryMethods() {
        assertNotNull(Composite.procedure(NoOp.instance(),Identity.instance()));   
        assertNotNull(Composite.predicate(Identity.instance(),Identity.instance()));   
        assertNotNull(Composite.function(Identity.instance(),Identity.instance()));   
    }

    public void testBinaryMethods() {
        assertNotNull(Composite.function(LeftIdentity.instance(),LeftIdentity.instance(),LeftIdentity.instance()));   
        assertNotNull(Composite.predicate(IsGreaterThan.instance(),Identity.instance(),Identity.instance()));   
        assertNotNull(Composite.function(LeftIdentity.instance(),Identity.instance(),Identity.instance()));   
    }
}
