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

package org.esigate.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.WriterOutputStream;

public class ResponseCapturingWrapper extends HttpServletResponseWrapper {
	private boolean capturing = true;
	private ServletOutputStream outputStream;
	private PrintWriter printWriter;
	private StringWriter writer = new StringWriter();

	public ResponseCapturingWrapper(HttpServletResponse response) {
		super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (!capturing)
			return super.getOutputStream();
		if (printWriter != null)
			throw new IllegalStateException("Writer already obtained");
		if (outputStream == null) {
			final WriterOutputStream writerOutputStream = new WriterOutputStream(writer, getCharacterEncoding());
			outputStream = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					writerOutputStream.write(b);
				}

				@Override
				public void close() throws IOException {
					writerOutputStream.close();
					super.close();
				}
			};
		}
		return outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (!capturing)
			return super.getWriter();
		if (outputStream != null)
			throw new IllegalStateException("OutputStream already obtained");
		if (printWriter == null) {
			printWriter = new PrintWriter(writer);
		}
		return printWriter;
	}

	@Override
	public void setContentLength(int len) {
		// Don't forward it if we are about to transform the contents.
		if (!capturing)
			super.setContentLength(len);
	}

	@Override
	public void setContentType(String type) {
		// TODO create configuration parameter
		if (!type.startsWith("text") && outputStream == null && printWriter == null)
			capturing = false;
		super.setContentType(type);
	}

	@Override
	public void setHeader(String name, String value) {
		if (!capturing || !name.equalsIgnoreCase("Content-length"))
			super.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		if (!capturing || !name.equalsIgnoreCase("Content-length"))
			super.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		if (!capturing || !name.equalsIgnoreCase("Content-length"))
			super.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		if (!capturing || !name.equalsIgnoreCase("Content-length"))
			super.addIntHeader(name, value);
	}

	public String getResult() {
		return writer.toString();
	}
}
