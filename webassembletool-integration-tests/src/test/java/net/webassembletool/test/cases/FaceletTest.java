package net.webassembletool.test.cases;

import net.sourceforge.jwebunit.junit.WebTestCase;

public class FaceletTest extends WebTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setBaseUrl("http://localhost:8080/webassembletool-app-facelet");
	}

	public void testTemplate() {
		beginAt("/");
		assertTitleMatch("Webassembletool facelet test");
		assertElementPresent("replacement");
		assertMatch("Lorem ipsum should have been replaced in the template",
				"New value", getElementById("replacement").getTextContent());
		assertElementPresent("param1");
		assertMatch("param1 should have been replaced in the template",
				"Test body", getElementById("param1").getTextContent());
		assertElementPresent("param2");
		assertMatch("param2 should have been replaced in the template",
				"Test body", getElementById("param2").getTextContent());
	}
}
