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
package org.esigate.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.esigate.test.MockOutput;

public class OutputTest extends TestCase {

	public void testCopyHeaders() {
		Output tested = new MockOutput();
		String name = "hEaDeR";
		tested.addHeader(name.toLowerCase(), "old");
		tested.addHeader(name.toUpperCase(), "old");
		tested.addHeader(name, "old");

		Output actual = new MockOutput();
		tested.copyHeaders(actual);
		assertEquals("headers are different", 1, actual.getHeaders().size());
		assertEquals("headers are different", tested.getHeaders(), actual.getHeaders());
	}

	public void testWrite() throws UnsupportedEncodingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Output tested = new MockOutput(out);
		tested.setCharsetName("UTF-8");

		tested.write("expected");
		assertEquals("expected", new String(out.toByteArray(), "UTF-8"));

		tested.setCharsetName("UNSUPPORTED");
		try {
			tested.write("unsupported");
			fail("should throw OutputException");
		} catch (OutputException e) {
			assertNotNull(e.getCause());
			assertTrue("cause should be instance of UnsupportedEncodingException",
					e.getCause() instanceof UnsupportedEncodingException);
		}

		tested = new MockOutput(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException();
			}
		});
		tested.setCharsetName("UTF-8");
		try {
			tested.write("unexpected");
			fail("should throw OutputException");
		} catch (OutputException e) {
			assertNotNull(e.getCause());
			assertTrue("cause should be instance of IOException", e.getCause() instanceof IOException);
		}
	}

}
