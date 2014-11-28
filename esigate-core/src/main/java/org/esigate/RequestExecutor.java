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

package org.esigate;

import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.events.EventManager;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.OutgoingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UrlRewriter;

public interface RequestExecutor {

    CloseableHttpResponse createAndExecuteRequest(DriverRequest request, String url, boolean b) throws HttpErrorPage;

    CloseableHttpResponse execute(OutgoingRequest httpRequest);

    public interface RequestExecutorBuilder {

        RequestExecutorBuilder setEventManager(EventManager eventManager);

        RequestExecutorBuilder setDriver(Driver driver);

        RequestExecutorBuilder setProperties(Properties properties);

        RequestExecutorBuilder setContentTypeHelper(ContentTypeHelper contentTypeHelper);

        RequestExecutorBuilder setUrlRewriter(UrlRewriter urlRewriter);

        RequestExecutor build();

    }

}
