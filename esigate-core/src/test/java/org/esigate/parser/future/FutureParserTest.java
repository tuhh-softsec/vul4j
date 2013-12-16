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

package org.esigate.parser.future;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;
import org.esigate.test.TestUtils;
import org.esigate.util.HttpRequestHelper;

public class FutureParserTest extends TestCase {
    private FutureParser tested;

    @Override
    protected void setUp() throws HttpErrorPage {
        MockRequestExecutor provider = MockRequestExecutor.createMockDriver();
        tested = new FutureParser(Pattern.compile("(<test:[^>]*>)|(</test:[^>]*>)"), SIMPLE, BODY);
        HttpEntityEnclosingRequest request = TestUtils.createRequest("http://a.b?request=updated");
        provider.initHttpRequestParams(request, null);
        tested.setHttpRequest(request);
    }

    @Override
    protected void tearDown() {
        tested = null;
    }

    public void testParse() throws IOException, HttpErrorPage, InterruptedException, ExecutionException {
        String page = "begin " + "<test:simple name='ignored'> this text will be ignored </test:simple>"
                + "<test:body>this text should be {request} </test:body>" + "<test:unknown name='value' />"
                + "<test:simple name='also ignored'/>" + " end";
        StringBuilderFutureAppendable sbf = new StringBuilderFutureAppendable();

        tested.parse(page, sbf);

        assertEquals("begin this text should be updated <test:unknown name='value' /> end", sbf.get());
    }

    private static final FutureElementType SIMPLE = new MockElementType("<test:simple", "</test:simple") {
        @Override
        public FutureElement newInstance() {
            return new SimpleElement();
        }
    };
    private static final FutureElementType BODY = new MockElementType("<test:body", "</test:body") {
        @Override
        public FutureElement newInstance() {
            return new BodyElement();
        }
    };

    private abstract static class MockElementType implements FutureElementType {
        private final String startTag;
        private final String endTag;

        protected MockElementType(String startTag, String endTag) {
            this.startTag = startTag;
            this.endTag = endTag;
        }

        @Override
        public final boolean isStartTag(String tag) {
            return tag.startsWith(startTag);
        }

        @Override
        public final boolean isEndTag(String tag) {
            return tag.startsWith(endTag);
        }
    }

    private static class BodyElement extends SimpleElement {
        private final StringBuilderFutureAppendable buf;

        public BodyElement() {
            buf = new StringBuilderFutureAppendable();
        }

        @Override
        public void onTagEnd(String tag, FutureParserContext ctx) throws IOException {
            String result;
            try {
                result = buf.get().toString()
                        .replaceAll("\\{request\\}", HttpRequestHelper.getParameter(ctx.getHttpRequest(), "request"));
            } catch (InterruptedException e) {
                throw new IOException(e);
            } catch (ExecutionException e) {
                throw new IOException(e);
            }
            ctx.getCurrent().characters(new CharSequenceFuture(result));
        }

        @Override
        public void characters(Future<CharSequence> csq) throws IOException {
            buf.enqueueAppend(csq);
        }
    }

    private static class SimpleElement implements FutureElement {
        private boolean closed;

        public SimpleElement() {
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void onTagStart(String tag, FutureParserContext ctx) {
            closed = tag.endsWith("/>");
        }

        @Override
        public void onTagEnd(String tag, FutureParserContext ctx) throws IOException {
        }

        @Override
        public void characters(Future<CharSequence> csq) throws IOException {
        }

        @Override
        public boolean onError(Exception e, FutureParserContext ctx) {
            return false;
        }

        @Override
        public FutureElement getParent() {
            return null;
        }

    }
}
