/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

package org.apache.commons.functor.generator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @deprecated BaseTransformer is going to be removed.
 */
public class TestBaseTransformer extends TestCase {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBaseTransformer(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestBaseTransformer.class);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    // Tests
    // ------------------------------------------------------------------------

    public void testEvaluateDelegatesToTransform() {
        Transformer t = new MockTransformer();
        assertEquals(new Integer(1),t.evaluate(null));
        assertEquals(new Integer(2),t.evaluate(null));
        assertEquals(new Integer(3),t.evaluate(null));
    }
    
    // Classes
    // ------------------------------------------------------------------------

    static class MockTransformer extends BaseTransformer {
        public Object transform(Generator gen) {
            return new Integer(++timesCalled);
        }
        
        public int timesCalled = 0;
    }
}