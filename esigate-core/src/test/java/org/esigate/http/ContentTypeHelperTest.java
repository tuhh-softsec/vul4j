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

package org.esigate.http;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.Parameters;

public class ContentTypeHelperTest extends TestCase {

    /** Test default configuration. */
    public void testDefaultConfig() {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        ContentTypeHelper contentTypeHelper = new ContentTypeHelper(properties);
        // Parsable contentTypes
        assertParsableContentType(contentTypeHelper, "text/html; charset=utf-8");
        assertParsableContentType(contentTypeHelper, "application/xhtml+xml; charset=iso-8859-1");
        assertNotParsableContentType(contentTypeHelper, "application/octet-stream");
    }

    private void assertParsableContentType(ContentTypeHelper contentTypeHelper, String contentType) {
        assertTrue("Content-type should be considered as text", contentTypeHelper.isTextContentType(contentType));
    }

    private void assertNotParsableContentType(ContentTypeHelper contentTypeHelper, String contentType) {
        assertFalse("Content-type should be considered as binary", contentTypeHelper.isTextContentType(contentType));
    }

    /**
     * Test property parsableContentTypes.
     */
    public void testParsableContentTypes() {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        properties.put(Parameters.PARSABLE_CONTENT_TYPES.getName(), "text/plain");
        ContentTypeHelper contentTypeHelper = new ContentTypeHelper(properties);
        assertParsableContentType(contentTypeHelper, "text/plain");

        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        properties.put(Parameters.PARSABLE_CONTENT_TYPES.getName(), "text/plain, text/html");
        contentTypeHelper = new ContentTypeHelper(properties);
        assertParsableContentType(contentTypeHelper, "text/plain");
        assertParsableContentType(contentTypeHelper, "text/html");

        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        properties.put(Parameters.PARSABLE_CONTENT_TYPES.getName(), "text/plain, text/html,application/x");
        contentTypeHelper = new ContentTypeHelper(properties);
        assertParsableContentType(contentTypeHelper, "text/plain");
        assertParsableContentType(contentTypeHelper, "text/html");
        assertParsableContentType(contentTypeHelper, "application/x");
    }
}
