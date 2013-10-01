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

package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.esi.EsiSyntaxError;
import org.esigate.test.TestUtils;

public class ReplaceElementTest extends TestCase {

	private EsiRenderer tested;
	private HttpEntityEnclosingRequest request;

	@Override
	protected void setUp() throws IOException, HttpErrorPage {
		MockDriver provider = new MockDriver("mock");
		request = TestUtils.createRequest();
		tested = new EsiRenderer();
		provider.initHttpRequestParams(request, null);
	}

	public void testErrorIfNotInsideIncludeTag() throws IOException, HttpErrorPage {
		String page = "begin <esi:replace fragment=\"test\">test</esi:replace> end";
		StringWriter out = new StringWriter();
		try {
			tested.render(request, page, out);
		} catch (EsiSyntaxError e) {
			return;
		}
		fail("We should have had a EsiSyntaxError");
	}
}
