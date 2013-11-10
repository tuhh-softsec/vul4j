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

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.esigate.extension.Extension;
import org.esigate.http.BasicCloseableHttpResponse;
import org.esigate.servlet.HttpServletMediator;
import org.esigate.servlet.ResponseCapturingWrapper;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;

public class ServletExtension implements Extension, IEventListener {
    private Driver driver;

    @Override
    public void init(Driver driver, Properties properties) {
        this.driver = driver;
        driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
    }

    @Override
    public boolean event(EventDefinition id, Event event) {
        FetchEvent fetchEvent = (FetchEvent) event;
        if (EventManager.EVENT_FETCH_PRE.equals(id)) {
            HttpServletMediator mediator = (HttpServletMediator) HttpRequestHelper.getMediator(fetchEvent.httpRequest);
            ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(mediator.getResponse(),
                    driver.getContentTypeHelper());
            CloseableHttpResponse result;
            try {
                if (fetchEvent.isProxy()) {
                    mediator.getFilterChain().doFilter(mediator.getRequest(), wrappedResponse);
                } else {
                    mediator.getRequest().getRequestDispatcher(getRelUrl(fetchEvent.httpRequest))
                            .forward(mediator.getRequest(), wrappedResponse);
                }
            } catch (IOException e) {
                result = new BasicCloseableHttpResponse(
                        new HttpErrorPage(HttpStatus.SC_BAD_GATEWAY, e.getMessage(), e).getHttpResponse());
            } catch (ServletException e) {
                result = new BasicCloseableHttpResponse(
                        new HttpErrorPage(HttpStatus.SC_BAD_GATEWAY, e.getMessage(), e).getHttpResponse());
            }
            result = new BasicCloseableHttpResponse(wrappedResponse.getResponse());
            fetchEvent.httpResponse = result;
            // Stop execution
            fetchEvent.exit = true;
        }
        return true;
    }

    private String getRelUrl(HttpRequest request) {
        URI uri = UriUtils.createUri(request.getRequestLine().getUri());
        String relUrl = uri.getPath();
        HttpServletMediator mediator = (HttpServletMediator) HttpRequestHelper.getMediator(request);
        relUrl = relUrl.substring(mediator.getRequest().getContextPath().length());
        if (uri.getRawQuery() != null) {
            relUrl += "?" + uri.getRawQuery();
        }
        return relUrl;
    }

}
