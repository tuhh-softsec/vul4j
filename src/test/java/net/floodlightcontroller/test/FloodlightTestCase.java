/**
 *    Copyright 2011, Big Switch Networks, Inc.
 *    Originally created by David Erickson, Stanford University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package net.floodlightcontroller.test;

import junit.framework.TestCase;
import net.floodlightcontroller.core.test.MockFloodlightProvider;

import org.junit.Test;

/**
 * This class gets a handle on the application context which is used to
 * retrieve Spring beans from during tests
 *
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public class FloodlightTestCase extends TestCase {
    protected MockFloodlightProvider mockFloodlightProvider;

    public MockFloodlightProvider getMockFloodlightProvider() {
        return mockFloodlightProvider;
    }

    public void setMockFloodlightProvider(MockFloodlightProvider mockFloodlightProvider) {
        this.mockFloodlightProvider = mockFloodlightProvider;
    }

    @Override
    public void setUp() throws Exception {
        mockFloodlightProvider = new MockFloodlightProvider();
    }

    @Test
    public void testSanity() throws Exception {
        assertTrue(true);
    }
}
