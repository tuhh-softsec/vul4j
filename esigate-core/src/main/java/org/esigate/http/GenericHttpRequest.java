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

package org.esigate.http;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpProtocolParams;

public class GenericHttpRequest extends BasicHttpEntityEnclosingRequest {

	private RequestLine requestLine;

	public GenericHttpRequest(String method, String uri) {
		super(method, uri);
		ProtocolVersion version = HttpProtocolParams.getVersion(getParams());
		requestLine = new BasicRequestLine(method, uri, version);
	}

	public void setUri(String uri) {
		requestLine = new BasicRequestLine(requestLine.getMethod(), uri, requestLine.getProtocolVersion());
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return requestLine.getProtocolVersion();
	}

	@Override
	public RequestLine getRequestLine() {
		return requestLine;
	}

	@Override
	public String toString() {
		return requestLine.toString();
	}

}
