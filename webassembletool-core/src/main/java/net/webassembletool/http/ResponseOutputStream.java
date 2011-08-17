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
package net.webassembletool.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wrapper for servlet OutputStream that wraps underlying IOExceptions to be
 * able to distinguish them later from other kinds of IOExceptions
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class ResponseOutputStream extends OutputStream {
	private final OutputStream outputStream;

	public ResponseOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void write(int b) throws IOException {
		try {
			outputStream.write(b);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void flush() throws IOException {
		try {
			outputStream.flush();
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			outputStream.write(b, off, len);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		try {
			outputStream.write(b);
		} catch (IOException e) {
			throw new ResponseOutputStreamException(e);
		}
	}

}
