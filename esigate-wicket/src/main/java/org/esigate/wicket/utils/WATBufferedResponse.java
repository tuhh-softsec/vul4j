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
package org.esigate.wicket.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebResponse;
import org.esigate.wicket.container.WATParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Wicket Response implementation used by the WATParam container. Any content
 * written to this reponse is buffered and will not be sent to the servlet
 * response. Content can be retrieved using
 * {@link WATBufferedResponse#getContent()}.
 * 
 * @author Nicolas Richeton
 * @see WATParam
 * @see Response
 */
public class WATBufferedResponse extends WebResponse {
	private static final Logger LOG = LoggerFactory
			.getLogger(WATBufferedResponse.class);

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

	/**
	 * Get buffered content.
	 * 
	 * @return the content
	 */
	public String getContent() {
		return new String(baos.toByteArray());
	}

	/**
	 * @see org.apache.wicket.Response#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return baos;
	}

	/**
	 * @see org.apache.wicket.Response#write(java.lang.CharSequence)
	 */
	@Override
	public void write(CharSequence string) {
		try {
			baos.write(string.toString().getBytes());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
