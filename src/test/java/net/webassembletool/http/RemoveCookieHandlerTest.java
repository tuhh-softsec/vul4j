package net.webassembletool.http;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.webassembletool.http.HttpResource.RemoveCookieHandler;

public class RemoveCookieHandlerTest extends TestCase {
    public void testParseContent() {
        RemoveCookieHandler tested = new RemoveCookieHandler("UTF-8", "unused");
        String data[] = { "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D&somethig=true",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D?somethig=true", "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here'>",
                "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D'>", "<a href=\"location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D\">", };
        String expected[] = { "http://localhost:8080/app/location.do#someInfo.here", "http://localhost:8080/app/location.do&somethig=true", "http://localhost:8080/app/location.do?somethig=true",
                "<a href='location.do#someInfo.here'>", "<a href='location.do'>", "<a href=\"location.do\">", };
        for (int i = 0; i < data.length; i++) {
            String actual = tested.parseContent(data[i]);
            Assert.assertEquals("Removing failed for string [" + i + "] '" + data[i] + "'", expected[i], actual);
        }
    }
}
