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
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.commons.io.output.StringBuilderWriter;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.test.TestUtils;

public abstract class AbstractElementTest extends TestCase {

    private IncomingRequest.Builder requestBuilder;
    private EsiRenderer tested;
    private Driver provider;

    @Override
    protected void setUp() {
        provider = MockRequestExecutor.createDriver();
        tested = new EsiRenderer(Executors.newCachedThreadPool());
        requestBuilder = TestUtils.createIncomingRequest();
    }

    protected String render(String page) throws HttpErrorPage, IOException {
        IncomingRequest incomingRequest = requestBuilder.build();
        DriverRequest request = new DriverRequest(incomingRequest, provider, page);
        StringBuilderWriter out = new StringBuilderWriter();
        tested.render(request, page, out);
        return out.toString();
    }

    protected void incomingRequest(String uri) {
        requestBuilder = TestUtils.createIncomingRequest(uri);
    }

    protected void addResource(String relUrl, String content) {
        ((MockRequestExecutor) provider.getRequestExecutor()).addResource(relUrl, content);
    }

    protected void setProvider(Driver provider) {
        this.provider = provider;
    }

    protected IncomingRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }

    protected void setTested(EsiRenderer tested) {
        this.tested = tested;
    }

}
