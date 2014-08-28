/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.servlet.impl;

import junit.framework.TestCase;
import org.esigate.impl.UriMapping;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alexis Thaveau.
 */
public class RequestUrlTest extends TestCase {
    public void testGetRelativeUrl() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContextPath()).thenReturn("/context");
        Mockito.when(request.getServletPath()).thenReturn("/servlet");
        Mockito.when(request.getRequestURI()).thenReturn("/context/servlet/resource");
        assertEquals("/servlet/resource", RequestUrl.getRelativeUrl(request, false));
        assertEquals("/resource", RequestUrl.getRelativeUrl(request, true));
    }

    public void testStripMappingPath() throws Exception {
        UriMapping mapping = UriMapping.create("/url/to/resource");
        String relUrl = RequestUrl.stripMappingPath("/mapping/path/test", mapping);
        assertEquals("/mapping/path/test", relUrl);

        mapping = UriMapping.create("/mapping/path/test");
        relUrl = RequestUrl.stripMappingPath("/mapping/path/test/url/to/resource", mapping);
        assertEquals("/url/to/resource", relUrl);
    }
}
