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

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.esigate.Renderer;
import org.esigate.events.Event;

/**
 * Render event : when renderers are applied on the page.
 * 
 * @author Nicolas Richeton
 * 
 */
public class RenderEvent extends Event {

    public List<Renderer> renderers;
    public String remoteUrl;
    public HttpRequest originalRequest;

    /**
     * The response from backend, including headers.
     */
    public HttpResponse httpResponse;
}
