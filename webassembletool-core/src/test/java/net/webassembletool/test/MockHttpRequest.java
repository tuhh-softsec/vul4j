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
package net.webassembletool.test;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpParams;

public class MockHttpRequest implements HttpRequest {
	String uri;

	public MockHttpRequest(String uri) {
		this.uri = uri;
	}

	public RequestLine getRequestLine() {
		return new BasicRequestLine("GET", uri, HttpVersion.HTTP_1_1);
	}

	public void addHeader(Header header) {
	}

	public void addHeader(String name, String value) {
	}

	public boolean containsHeader(String name) {
		return false;
	}

	public Header[] getAllHeaders() {
		return null;
	}

	public Header getFirstHeader(String name) {
		return null;
	}

	public Header[] getHeaders(String name) {
		return null;
	}

	public Header getLastHeader(String name) {
		return null;
	}

	public HttpParams getParams() {
		return null;
	}

	public ProtocolVersion getProtocolVersion() {
		return null;
	}

	public HeaderIterator headerIterator() {
		return null;
	}

	public HeaderIterator headerIterator(String name) {
		return null;
	}

	public void removeHeader(Header header) {
	}

	public void removeHeaders(String name) {
	}

	public void setHeader(Header header) {
	}

	public void setHeader(String name, String value) {
	}

	public void setHeaders(Header[] headers) {
	}

	public void setParams(HttpParams params) {
	}

}
