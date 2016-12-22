/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestResult;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;

import static org.junit.Assert.*;

public class TestRunDetailsXMLHandlerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static boolean containsMeasure(TestRun testRun, String packageName, String fullName) {
        for (TestResult testResult : testRun.getTestResults()) {
            if (testResult.getPackageName().equalsIgnoreCase(packageName) && testResult.getName().equalsIgnoreCase(fullName)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testXMLParser() throws SAXException, IOException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        TestRunDetailsXMLHandler handler = new TestRunDetailsXMLHandler();
        xr.setContentHandler(handler);

        File file = new File("src/test/resources/TestRunDetailsXMLHandlerTest1.xml");
        InputStream inputStream = new FileInputStream(file);
        xr.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));
        TestRun testRun = handler.getParsedObjects();

        assertNotNull(testRun);
        assertNotNull(testRun.getTestResults());
        assertFalse(testRun.getTestResults().isEmpty());

        assertEquals(testRun.getTestResults().size(), 277);
        assertEquals(testRun.getVersionBuild(), "10908");
        assertEquals(testRun.getNumPassed() + testRun.getNumFailed() + testRun.getNumDegraded() + testRun.getNumImproved() + testRun.getNumVolatile()
                + testRun.getNumInvalidated(), testRun.getTestResults().size());

        assertTrue(containsMeasure(testRun, "com.dynatrace.easytravel.launcher.plugin", "BasePluginManagerTest.enablePluginsWorks"));
        assertTrue(containsMeasure(testRun, "com.dynatrace.easytravel.util", "WebUtilsTest.testHtmlPageTitleInvalid2"));
    }

    @Test
    public void testXMLParserException() throws SAXException, IOException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        TestRunDetailsXMLHandler handler = new TestRunDetailsXMLHandler();
        xr.setContentHandler(handler);

        File file = new File("src/test/resources/TestRunDetailsXMLHandlerTest2.xml");
        InputStream inputStream = new FileInputStream(file);
        xr.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));

        exception.expect(RESTErrorException.class);
        handler.getParsedObjects();
    }
}
