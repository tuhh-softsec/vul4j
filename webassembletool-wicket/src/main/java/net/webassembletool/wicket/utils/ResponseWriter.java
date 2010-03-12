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
package net.webassembletool.wicket.utils;

import java.io.IOException;
import java.io.Writer;

import org.apache.wicket.Response;

/**
 * @author Nicolas Richeton
 * 
 */
public class ResponseWriter extends Writer implements Appendable {

	private final Response resp;

	/**
	 * Adapts Wicket Response to Writer.
	 * 
	 * @param r
	 */
	public ResponseWriter(Response r) {
		this.resp = r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		resp.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		String content = new String(cbuf);
		resp.write(content.subSequence(off, len));
	}
}
