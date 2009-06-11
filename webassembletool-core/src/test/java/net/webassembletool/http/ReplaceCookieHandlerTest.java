package net.webassembletool.http;

import junit.framework.TestCase;
import net.webassembletool.http.HttpResource.ReplaceCookieHandler;

public class ReplaceCookieHandlerTest extends TestCase {

    public void testParseContent() {
        ReplaceCookieHandler tested = new ReplaceCookieHandler("UTF-8",
                "FFEEDDCCBBAA");

        String data[] = {
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D&somethig=true",
                "http://localhost:8080/app/location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D?somethig=true",
                "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D#someInfo.here'>",
                "<a href='location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D'>",
                "<a href=\"location.do;jsessionid=DD2EDBFA85B2BAF5ED3E8655A5D6A03D\">", };
        String expected[] = {
                "http://localhost:8080/app/location.do;jsessionid=FFEEDDCCBBAA#someInfo.here",
                "http://localhost:8080/app/location.do;jsessionid=FFEEDDCCBBAA&somethig=true",
                "http://localhost:8080/app/location.do;jsessionid=FFEEDDCCBBAA?somethig=true",
                "<a href='location.do;jsessionid=FFEEDDCCBBAA#someInfo.here'>",
                "<a href='location.do;jsessionid=FFEEDDCCBBAA'>",
                "<a href=\"location.do;jsessionid=FFEEDDCCBBAA\">", };
        for (int i = 0; i < data.length; i++) {
            String actual = tested.parseContent(data[i]);
            assertEquals("Replacing failed for string [" + i + "] '" + data[i]
                    + "'", expected[i], actual);
        }
    }

}
