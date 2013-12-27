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

package org.esigate.events.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Renderer;
import org.esigate.events.Event;
import org.esigate.impl.DriverRequest;

/**
 * Render event : when renderers are applied on the page.
 * 
 * @author Nicolas Richeton
 * 
 */
public class RenderEvent extends Event {

    private final List<Renderer> renderers = new ArrayList<Renderer>(10);
    private final String remoteUrl;
    private final DriverRequest originalRequest;

    /**
     * The response from backend, including headers.
     */
    private final CloseableHttpResponse httpResponse;

    public RenderEvent(String remoteUrl, DriverRequest originalRequest, CloseableHttpResponse httpResponse) {
        this.remoteUrl = remoteUrl;
        this.originalRequest = originalRequest;
        this.httpResponse = httpResponse;
    }

    public List<Renderer> getRenderers() {
        return renderers;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public DriverRequest getOriginalRequest() {
        return originalRequest;
    }

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }
}
