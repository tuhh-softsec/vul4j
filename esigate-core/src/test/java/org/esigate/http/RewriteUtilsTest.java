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

package org.esigate.http;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.esigate.util.UriUtils;

public class RewriteUtilsTest extends TestCase {
	private void simpleRemoveTest(String sessionId, String in, String expected) {
		String actual = UriUtils.removeSessionId(sessionId, in);
		Assert.assertEquals("Removing sessionId failed", expected, actual);
	}

	public void testRemoveSessionId() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here",
				"http://localhost:8080/app/location.do#someInfo.here");
	}

	public void testRemoveSessionId1() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D&somethig=true",
				"http://localhost:8080/app/location.do&somethig=true");
	}

	public void testRemoveSessionId2() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D?somethig=true",
				"http://localhost:8080/app/location.do?somethig=true");
	}

	public void testRemoveSessionId3() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here'>", "<a href='location.do#someInfo.here'>");
	}

	public void testRemoveSessionId4() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D'>", "<a href='location.do'>");
	}

	public void testRemoveSessionId5() {
		simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D", "<a href=\"location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D\">", "<a href=\"location.do\">");
	}

	public void testRemoveSessionId6() {
		simpleRemoveTest("84FF5970F8A92E41F752F8A15F736727", "<a href=\"/test;jsessionid=84FF5970F8A92E41F752F8A15F736727\">/test;jsessionid=84FF5970F8A92E41F752F8A15F736727</a>",
				"<a href=\"/test\">/test</a>");
	}

	public void testTranslate() throws Exception {
		String sourceUrl = "http://www.test.com/aaa/bb";
		String sourceExample = "http://www.test.com/aaa/cccc/d/";
		String targetExample = "https://localhost:8080/eee/cccc/d/";
		String expected = "https://localhost:8080/eee/bb";
		assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
	}

	public void testTranslateUnmodified() throws Exception {
		String sourceUrl = "http://www.test.com/zz/bb";
		String sourceExample = "http://www.test.com/aaa/cccc/d/";
		String targetExample = "https://localhost:8080/eee/cccc/d/";
		String expected = sourceUrl;
		assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
	}

}
