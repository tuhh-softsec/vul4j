package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;
import net.webassembletool.AggregationSyntaxException;
import net.webassembletool.Driver;
import net.webassembletool.RenderingException;

public class IncludeTemplateRegionTest extends TestCase {

    public void testProcess() throws IOException, RenderingException {
        final List<String> calls = new LinkedList<String>();
        final Writer mockWriter = new StringWriter();
        final Driver mockDriver = new Driver("mock", new Properties()) {
            @Override
            public void renderTemplate(String page, String name, Writer writer,
                    HttpServletRequest originalRequest,
                    Map<String, String> params,
                    Map<String, String> replaceRules,
                    Map<String, String> parameters, boolean propagateJsessionId) {
                calls.add("renderTemplate");
                assertEquals("page", page);
                assertEquals("name", name);
                assertEquals(mockWriter, writer);
                assertNull(originalRequest);
                assertNotNull(params);
                assertEquals(Collections.emptyMap(), params);
                assertNull(replaceRules);
                assertNull(parameters);
                assertFalse(propagateJsessionId);
            }
        };
        IncludeTemplateRegion tested = new IncludeTemplateRegion("provider",
                "page", "name", false, "templateBody") {
            @Override
            protected Driver getDriver() {
                calls.add("getDriver");
                return mockDriver;
            }

            @Override
            protected Map<String, String> parseParameters(String content) {
                calls.add("parseParameters");
                assertEquals("templateBody", content);
                return Collections.emptyMap();
            }
        };

        tested.process(mockWriter, null);
        assertEquals("three methods should be called", Arrays.asList(
                "parseParameters", "getDriver", "renderTemplate"), calls);
    }

    public void testParseParameters() throws AggregationSyntaxException {
        IncludeTemplateRegion tested = new IncludeTemplateRegion(null, null,
                null, false, null);
        Map<String, String> actual = tested.parseParameters("");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());

        actual = tested
                .parseParameters("<!--$beginput$param-->value<!--$endput-->");
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertTrue(actual.containsKey("param"));
        assertEquals("value", actual.get("param"));

        try {
            tested.parseParameters("<!--$beginput$param-->value");
            fail("should throw AggregationSyntaxException");
        } catch (AggregationSyntaxException e) {
            assertEquals("Tag not closed: <beginput,param>", e.getMessage());
        }

        try {
            tested.parseParameters("<!--$beginput-->");
            fail("should throw AggregationSyntaxException");
        } catch (AggregationSyntaxException e) {
            assertEquals("Invalid syntax: <beginput>", e.getMessage());
        }

        actual = tested
                .parseParameters("<!--$beginput$param1-->value1<!--$endput-->"
                        + "<!--$beginput$param2-->value2<!--$endput-->");
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.containsKey("param1"));
        assertTrue(actual.containsKey("param2"));
        assertEquals("value1", actual.get("param1"));
        assertEquals("value2", actual.get("param2"));
    }

}
