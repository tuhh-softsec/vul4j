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

package org.esigate.esi;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;

public class ReplaceElementTest extends TestCase {

	private ResourceContext ctx;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws IOException {
		MockDriver provider = new MockDriver("mock");
		MockHttpRequest request = new MockHttpRequest();
		ctx = new ResourceContext(provider, null, null, request, null);
		tested = new EsiRenderer();
	}

	public void testErrorIfNotInsideIncludeTag() throws IOException, HttpErrorPage {
		String page = "begin <esi:replace fragment=\"test\">test</esi:replace> end";
		StringWriter out = new StringWriter();
		try {
			tested.render(ctx, page, out);
		} catch (EsiSyntaxError e) {
			return;
		}
		fail("We should have had a EsiSyntaxError");
	}
}
