/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Licensed to the Apache Software Foundation (ASF) under one
 ~ or more contributor license agreements.  See the NOTICE file
 ~ distributed with this work for additional information
 ~ regarding copyright ownership.  The ASF licenses this file
 ~ to you under the Apache License, Version 2.0 (the
 ~ "License"); you may not use this file except in compliance
 ~ with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package org.apache.sling.xss.impl;

import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.xss.XSSFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class XSSFilterImplTest {

    @Rule
    public SlingContext context = new SlingContext();

    private XSSFilter xssFilter;

    @Before
    public void setUp() {
        context.registerService(ServiceUserMapped.class, mock(ServiceUserMapped.class));
        context.registerInjectActivateService(new XSSFilterImpl());
        xssFilter = context.getService(XSSFilter.class);
    }

    @After
    public void tearDown() {
        xssFilter = null;
    }

    @Test
    public void isValidHref() {
        checkIsValid("javascript:alert(1)", false);
        checkIsValid("", true);
        checkIsValid("%26%23x6a%3b%26%23x61%3b%26%23x76%3b%26%23x61%3b%26%23x73%3b%26%23x63%3b%26%23x72%3b%26%23x69%3b%26%23x70%3b%26%23x74%3b%26%23x3a%3balert%281%29", false);
        checkIsValid("&#x6a;&#x61;&#x76;&#x61;&#x73;&#x63;&#x72;&#x69;&#x70;&#x74;&#x3a;alert(1)", false);
    }

    private void checkIsValid(String input, boolean valid) {
        if (valid) {
            assertTrue("Expected valid href value for: " + input, xssFilter.isValidHref(input));
        } else {
            assertFalse("Expected invalid href value for: " + input, xssFilter.isValidHref(input));
        }
    }

}
