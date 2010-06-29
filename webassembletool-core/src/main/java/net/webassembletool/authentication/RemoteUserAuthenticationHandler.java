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
package net.webassembletool.authentication;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.UserContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

/**
 * AuthenticationHandler implementation that retrieves the user passed by the
 * servlet container or set manually into the RequestContext and transmits it as
 * a HTTP header X_REMOTE_USER in all requests
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class RemoteUserAuthenticationHandler implements AuthenticationHandler {

	public boolean needsNewRequest(HttpClientResponse response,
			ResourceContext requestContext) {
		return false;
	}

	public void preRequest(HttpClientRequest request,
			ResourceContext requestContext) {
		UserContext userContext = requestContext.getUserContext(false);
		String remoteUser = null;
		if (userContext != null && userContext.getUser() != null) {
			remoteUser = userContext.getUser();
		} else if (requestContext.getOriginalRequest().getRemoteUser() != null) {
			remoteUser = requestContext.getOriginalRequest().getRemoteUser();
		}
		if (remoteUser != null) {
			request.addHeader("X_REMOTE_USER", remoteUser);
		}
	}

	public void init(Properties properties) {
		// Nothing to do
	}

	public boolean beforeProxy(ResourceContext requestContext) {
		return true;
	}

	public void render(ResourceContext requestContext, String src, Writer out)
			throws IOException, HttpErrorPage {
		// Just copy src to out
		out.write(src);
	}
}
