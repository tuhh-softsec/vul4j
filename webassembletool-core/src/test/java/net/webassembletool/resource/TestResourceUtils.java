package net.webassembletool.resource;

import junit.framework.TestCase;

public class TestResourceUtils extends TestCase {

    public void testRemoveJsessionId() {
        String expected[] = { "/base/path/?param=pvalue", "/base/path/",
                "/base/path/?param=pvalue", "/base/path/&param=pvalue",
                "/base/path/?param=pvalue&",
                "/base/path/?param=pvalue&&param=pvalue", };
        String tested[] = { "/base/path/?param=pvalue",
                "/base/path/;jsessionid=value",
                "/base/path/;jsessionid=value?param=pvalue",
                "/base/path/;jsessionid=value&param=pvalue",
                "/base/path/?param=pvalue&;jsessionid=value",
                "/base/path/?param=pvalue&;jsessionid=value&param=pvalue", };
        for (int i = 0; i < tested.length; i++) {
            StringBuilder actual = new StringBuilder(tested[i]);
            ResourceUtils.removeJsessionId(actual);
            assertEquals("failed for string[" + i + "] '" + tested[i] + "'",
                    expected[i], actual.toString());
        }
    }
}
