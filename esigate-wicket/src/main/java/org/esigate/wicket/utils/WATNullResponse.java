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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebResponse;
import org.esigate.wicket.container.WATTemplate;

/**
 * This response drops all output and can store content from param blocks.
 * 
 * @author Nicolas Richeton <nicolas.richeton@gmail.com>
 * @see WATTemplate
 * @see Response
 */
public class WATNullResponse extends WebResponse {
	private final Map<String, String> blocks = new HashMap<String, String>();

	/**
	 * Get WAT blocks stored in this response. Map#put() can be used to add a
	 * new block.
	 * 
	 * @return Map of blocks <id, content>.
	 */
	public Map<String, String> getBlocks() {
		return blocks;
	}

	/**
	 * @see org.apache.wicket.Response#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// No output between parameter tags. Drop content.
			}
		};
	}

	/**
	 * @see org.apache.wicket.Response#write(java.lang.CharSequence)
	 */
	@Override
	public void write(CharSequence string) {
		// No output between parameter tags. Drop content.
	}

}
