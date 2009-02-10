package net.webassembletool.parse;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

public class UnmodifiableRegionTest extends TestCase {

    public void testProcess() throws IOException {
	String data = "content";
	UnmodifiableRegion tested = new UnmodifiableRegion(data, 1, data
		.length() - 1);
	StringWriter out = new StringWriter();

	tested.process(out, null);
	assertEquals("onten", out.toString());
    }

}
