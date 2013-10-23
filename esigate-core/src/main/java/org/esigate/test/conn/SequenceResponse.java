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
package org.esigate.test.conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * A response handler, which returns HTTP reponses one after another.
 * <p>
 * Sends IllegalStateException if no reponse has been added, or if execute is
 * called too many times.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SequenceResponse implements IResponseHandler {
	private int count = 0;
	private List<HttpResponse> responses = new ArrayList<HttpResponse>();

	@Override
	public HttpResponse execute(HttpRequest request) throws IOException {

		if (this.responses.size() <= this.count) {
			throw new IllegalStateException("Unexpected request");
		}

		HttpResponse result = this.responses.get(this.count);
		this.count++;
		return result;
	}

	/**
	 * Add a Http response.
	 * 
	 * @param response
	 * @return this object
	 */
	public SequenceResponse response(HttpResponse response) {
		this.responses.add(response);
		return this;
	}

}
