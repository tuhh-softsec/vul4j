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

package org.esigate.authentication;

import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.esigate.UserContext;
import org.esigate.http.GenericHttpRequest;
import org.esigate.util.HttpRequestHelper;

/**
 * AuthenticationHandler implementation that retrieves the user passed by the servlet container or set manually into the
 * RequestContext and transmits it as a HTTP header X_REMOTE_USER in all requests.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class RemoteUserAuthenticationHandler extends GenericAuthentificationHandler {

    @Override
    public boolean needsNewRequest(HttpResponse response, HttpRequest httpRequest) {
        return false;
    }

    @Override
    public void preRequest(GenericHttpRequest request, HttpRequest httpRequest) {
        UserContext userContext = HttpRequestHelper.getUserContext(httpRequest);
        String remoteUser = null;
        if (userContext != null && userContext.getUser() != null) {
            remoteUser = userContext.getUser();
        } else {
            remoteUser = HttpRequestHelper.getMediator(httpRequest).getRemoteUser();
        }
        if (remoteUser != null) {
            request.addHeader("X_REMOTE_USER", remoteUser);
        }
    }

    @Override
    public void init(Properties properties) {
        // Nothing to do
    }

    @Override
    public boolean beforeProxy(HttpRequest httpRequest) {
        return true;
    }

}
