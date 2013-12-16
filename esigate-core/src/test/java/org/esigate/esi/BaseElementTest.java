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

package org.esigate.esi;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.parser.Element;
import org.esigate.parser.ParserContext;

public class BaseElementTest extends TestCase {

    public void testOnTagStart() throws Exception {
        final Tag[] parsed = new Tag[1];
        BaseElement tested = new MockBaseElement() {
            @Override
            protected void parseTag(Tag tag, ParserContext ctx) {
                parsed[0] = tag;
            }
        };
        ParserContext ctx = new MockParserContext();

        tested.onTagStart("<do:something />", ctx);
        assertEquals(true, tested.isClosed());

        assertNotNull(parsed[0]);
        assertEquals(true, parsed[0].isOpenClosed());
        assertEquals(false, parsed[0].isClosing());
        assertEquals("do:something", parsed[0].getName());

        tested.onTagStart("<do:something>", ctx);
        assertEquals(false, tested.isClosed());

        assertNotNull(parsed[0]);
        assertEquals(false, parsed[0].isOpenClosed());
        assertEquals(false, parsed[0].isClosing());
        assertEquals("do:something", parsed[0].getName());

        tested.onTagStart("<do:something name='value'>", ctx);
        assertEquals(false, tested.isClosed());

        assertNotNull(parsed[0]);
        assertEquals(false, parsed[0].isOpenClosed());
        assertEquals(false, parsed[0].isClosing());
        assertEquals("do:something", parsed[0].getName());
        assertEquals("value", parsed[0].getAttribute("name"));
    }

    protected static class MockBaseElement extends BaseElement {
        public MockBaseElement() {
        }

    }

    protected static class MockParserContext implements ParserContext {
        @Override
        public HttpEntityEnclosingRequest getHttpRequest() {
            return null;
        }

        @Override
        public boolean reportError(Exception e) {
            return false;
        }

        @Override
        public Element getCurrent() {
            return null;
        }

        @Override
        public <T> T findAncestor(Class<T> type) {
            return null;
        }

        @Override
        public HttpResponse getHttpResponse() {
            return null;
        }
    }
}
