package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;
import net.webassembletool.Driver;
import net.webassembletool.RenderingException;

public class IncludeBlockRegionTest extends TestCase {

    public void testProcess() throws IOException, RenderingException {
        final List<String> calls = new LinkedList<String>();
        final Writer mockWriter = new StringWriter();
        final Driver mockDriver = new Driver("mock", new Properties()) {
            @Override
            public void renderBlock(String page, String name, Writer writer,
                    HttpServletRequest originalRequest,
                    Map<String, String> replaceRules,
                    Map<String, String> parameters, boolean propagateJsessionId) {
                calls.add("renderBlock");
                assertEquals("page", page);
                assertEquals("name", name);
                assertEquals(mockWriter, writer);
                assertNull(originalRequest);
                assertNull(replaceRules);
                assertNull(parameters);
                assertFalse(propagateJsessionId);
            }
        };
        IncludeBlockRegion tested = new IncludeBlockRegion("provider", "page",
                "name", false) {
            @Override
            protected Driver getDriver() {
                calls.add("getDriver");
                return mockDriver;
            }
        };

        tested.process(mockWriter, null);
        assertEquals("two methods should be called", Arrays.asList("getDriver",
                "renderBlock"), calls);
    }

}
