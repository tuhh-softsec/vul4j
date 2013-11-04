package org.esigate.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UriUtilsTest extends TestCase {

    public void testCreateUri() {
        assertEquals("http://foo.com/%E9?q=%E0", UriUtils.createURI("http", "foo.com", 0, "%E9", "q=%E0", null)
                .toString());
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
        simpleRemoveTest(
                "84FF5970F8A92E41F752F8A15F736727",
                "<a href=\"/test;jsessionid=84FF5970F8A92E41F752F8A15F736727\">/test;jsessionid=84FF5970F8A92E41F752F8A15F736727</a>",
                "<a href=\"/test\">/test</a>");
    }

    public void testTranslate() throws Exception {
        String sourceUrl = "http://www.test.com/aaa/bb";
        String sourceExample = "http://www.test.com/aaa/cccc/d/";
        String targetExample = "https://localhost:8080/eee/cccc/d/";
        String expected = "https://localhost:8080/eee/bb";
        assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
    }

    public void testTranslateSlash1() throws Exception {
        String sourceUrl = "http://www.test.com";
        String sourceExample = "https://localhost:8080/";
        String targetExample = "http://www.test.com/";
        String expected = "http://www.test.com";
        assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
    }

    public void testTranslateSlash2() throws Exception {
        String sourceUrl = "http://www.test.com/";
        String sourceExample = "https://localhost:8080/";
        String targetExample = "http://www.test.com";
        String expected = "http://www.test.com/";
        assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
    }

    public void testTranslateUnmodified() throws Exception {
        String sourceUrl = "http://www.test.com/zz/bb";
        String sourceExample = "http://www.test.com/aaa/cccc/d/";
        String targetExample = "https://localhost:8080/eee/cccc/d/";
        String expected = sourceUrl;
        assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
    }

    public void testTranslateIssue132() throws Exception {
        String sourceUrl = "http://mbl-ez-dua.sirissie.caisse-epargne.fr/rhone-alpes";
        String sourceExample = "http://esigate:8080/switchlanguage/to/mbl_rhone-alpes/";
        String targetExample = "http://esigate:8080/switchlanguage/to/mbl_rhone-alpes/";
        String expected = sourceUrl;
        assertEquals(expected, UriUtils.translateUrl(sourceUrl, sourceExample, targetExample));
    }

}
