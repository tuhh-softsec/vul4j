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
package org.esigate;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrapper for servlet OutputStream that wraps underlying IOExceptions to be
 * able to distinguish them later from other kinds of IOExceptions
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
class ResponseOutputStream extends OutputStream {
	private final OutputStream parent;

	public ResponseOutputStream(OutputStream parent) {
		this.parent = parent;
	}

	@Override
	public void write(int b) {
		try {
			parent.write(b);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void close() {
		try {
			parent.close();
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void flush() {
		try {
			parent.flush();
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) {
		try {
			parent.write(b, off, len);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void write(byte[] b) {
		try {
			parent.write(b);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

}
