package net.webassembletool.http;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RewriteUtilsTest extends TestCase {
	private void simpleRemoveTest(String sessionId, String in, String expected) {
		String actual = RewriteUtils.removeSessionId(sessionId, in);
		Assert.assertEquals("Removing sessionId failed", expected, actual);
	}

	public void testRemoveSessionId() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here",
				"http://localhost:8080/app/location.do#someInfo.here");
	}

	public void testRemoveSessionId1() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D&somethig=true",
				"http://localhost:8080/app/location.do&somethig=true");
	}

	public void testRemoveSessionId2() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D?somethig=true",
				"http://localhost:8080/app/location.do?somethig=true");
	}

	public void testRemoveSessionId3() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here'>",
				"<a href='location.do#someInfo.here'>");
	}

	public void testRemoveSessionId4() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D'>",
				"<a href='location.do'>");
	}

	public void testRemoveSessionId5() {
		simpleRemoveTest(
				"DD2EDBFA85B2BAF5ED3E8655A5D6A03D",
				"<a href=\"location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D\">",
				"<a href=\"location.do\">");
	}

	public void testRemoveSessionId6() {
		simpleRemoveTest(
				"84FF5970F8A92E41F752F8A15F736727",
				"<a href=\"/test;jsessionid=84FF5970F8A92E41F752F8A15F736727\">/test;jsessionid=84FF5970F8A92E41F752F8A15F736727</a>",
				"<a href=\"/test\">/test</a>");
	}

}
