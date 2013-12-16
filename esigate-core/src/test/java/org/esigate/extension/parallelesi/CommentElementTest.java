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

package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;
import org.esigate.test.TestUtils;

public class CommentElementTest extends TestCase {
    private EsiRenderer tested;
    private HttpEntityEnclosingRequest request;

    @Override
    protected void setUp() throws Exception {
        MockRequestExecutor provider = MockRequestExecutor.createMockDriver();
        request = TestUtils.createRequest();
        tested = new EsiRenderer(Executors.newCachedThreadPool());
        provider.initHttpRequestParams(request, null);
    }

    public void testCommentEmpty() throws IOException, HttpErrorPage {
        String page = "begin <esi:comment text=\"some comment\" /> end";
        StringWriter out = new StringWriter();
        tested.render(request, page, out);
        assertEquals("begin  end", out.toString());
    }

    public void testComment() throws IOException, HttpErrorPage {
        String page = "begin <esi:comment text=\"some comment\" > some text </esi:comment> end";
        StringWriter out = new StringWriter();
        tested.render(request, page, out);
        assertEquals("begin  end", out.toString());
    }
}
