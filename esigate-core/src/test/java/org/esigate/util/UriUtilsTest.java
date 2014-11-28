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

package org.esigate.util;

import java.net.URI;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UriUtilsTest extends TestCase {

    public void testCreateUri() {
        assertEquals("http://foo.com/%E9?q=%E0", UriUtils.createURI("http", "foo.com", 0, "%E9", "q=%E0", null));
    }

    private void assertParses(String uriToParse, String expected) {
        assertEquals(expected, UriUtils.createURI(uriToParse).toASCIIString());
    }

    public void testCreateUriSpecialCharacters() {
        assertParses("a b", "a%20b");
        assertParses("a{b", "a%7Bb");
        assertParses("aéb", "a%C3%A9b");
    }

    private void simpleRemoveTest(String sessionId, String in, String expected) {
        String actual = UriUtils.removeSessionId(sessionId, in);
        Assert.assertEquals("Removing sessionId failed", expected, actual);
    }

    public void testRemoveSessionId() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here",
                "http://localhost:8080/app/location.do#someInfo.here");
    }

    public void testRemoveSessionId1() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D&somethig=true",
                "http://localhost:8080/app/location.do&somethig=true");
    }

    public void testRemoveSessionId2() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D?somethig=true",
                "http://localhost:8080/app/location.do?somethig=true");
    }

    public void testRemoveSessionId3() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here'>",
                "<a href='location.do#someInfo.here'>");
    }

    public void testRemoveSessionId4() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D'>", "<a href='location.do'>");
    }

    public void testRemoveSessionId5() {
        simpleRemoveTest("DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
                "<a href=\"location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D\">", "<a href=\"location.do\">");
    }

    public void testRemoveSessionId6() {
        simpleRemoveTest("84FF5970F8A92E41F752F8A15F736727",
                "<a href=\"/test;jsessionid=84FF5970F8A92E41F752F8A15F736727\">"
                        + "/test;jsessionid=84FF5970F8A92E41F752F8A15F736727" + "</a>", "<a href=\"/test\">/test</a>");
    }

    private void assertEncodes(String character, String expectedResult) {
        assertEquals(expectedResult, UriUtils.encodeIllegalCharacters(character));
    }

    public void testEncodeIllegalCharacters() {
        assertEncodes("a", "a");
        assertEncodes("/", "/");
        assertEncodes(" ", "%20");
    }

    private void assertNormalize(String path, String expectedNormalizedPath) {
        URI uri = UriUtils.createURI(path);
        uri = uri.normalize();
        assertEquals(expectedNormalizedPath, uri.toString());
    }

    public void testNormalizePath() {
        assertNormalize("test", "test");
        assertNormalize("/test/", "/test/");
        assertNormalize("/test/../", "/");
        assertNormalize("/test/../../", "/../");
        assertNormalize("/test/../../aaa/", "/../aaa/");

        assertNormalize("path/to/page", "path/to/page");
        assertNormalize("path/to/../page", "path/page");
        assertNormalize("path/to/../../page", "page");

        assertNormalize("http://host/path/to/../../page", "http://host/page");
        assertNormalize("//host/path/to/../../page", "//host/page");
        assertNormalize("http://host/path/to/../../page/../", "http://host/");
        assertNormalize("http://host/path/to/../../../page/../", "http://host/../");

        // Test bad url
        assertNormalize("http://host/path/to/../../../../page/../", "http://host/../../");

        assertNormalize("path/../to/../page", "page");

        // test empty url
        assertNormalize("", "");

        // Test url that can't be totally cleaned
        assertNormalize("path/../../page", "../page");

        // Test url that can't be cleaned
        assertNormalize("../../path/../to/../page", "../../page");
        assertNormalize("../page", "../page");
        assertNormalize("../", "../");

        // Test with parameters
        assertNormalize("path/to/page?param1=value1", "path/to/page?param1=value1");
        assertNormalize("path/to/page?param1=value1#test", "path/to/page?param1=value1#test");
        assertNormalize("path/to/page#test", "path/to/page#test");

        assertNormalize("path/to/../page?param1=../test", "path/page?param1=../test");
        assertNormalize("path/to/page?param1=../test#test", "path/to/page?param1=../test#test");
        assertNormalize("path/to/page#test", "path/to/page#test");
    }

    public void testSetPath() {
        URI uri = UriUtils.createURI("/");
        uri = UriUtils.concatPath(uri, "/");
        assertEquals("/", uri.getPath());

        uri = UriUtils.createURI("/");
        uri = UriUtils.concatPath(uri, "/test");
        assertEquals("/test", uri.getPath());

        uri = UriUtils.createURI("/");
        uri = UriUtils.concatPath(uri, "//test/");
        assertEquals("/test/", uri.getPath());

        uri = UriUtils.createURI("");
        uri = UriUtils.concatPath(uri, "/test");
        assertEquals("test", uri.getPath());
    }

}
