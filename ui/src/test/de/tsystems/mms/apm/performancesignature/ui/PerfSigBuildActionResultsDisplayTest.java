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

package de.tsystems.mms.apm.performancesignature.ui;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DashboardXMLHandler;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PerfSigBuildActionResultsDisplayTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void testBuildActionCharts() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        XMLReader xr = XMLReaderFactory.createXMLReader();
        DashboardXMLHandler handler = new DashboardXMLHandler("buildActionTest");
        xr.setContentHandler(handler);

        File file = new File("src/test/resources/PerfSigBuildActionResultsDisplayTest.xml");
        InputStream inputStream = new FileInputStream(file);
        xr.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));

        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));
        PerfSigBuildAction action = new PerfSigBuildAction(Collections.singletonList(handler.getParsedObjects()));
        build.addAction(action);

        HtmlPage page = j.createWebClient().goTo("job/test0/1/performance-signature/");
        assertEquals(page.getByXPath("//*[@id=\"buildActionTest\"]/div/img").size(), 10);
        List<?> list = page.getByXPath("//*[@id=\"buildActionTest\"]/div/table/tbody/tr/td[1]/b/text()");
        assertTrue(containsMeasure(list, "Total GC Utilization (Average) (%)"));
        assertTrue(containsMeasure(list, "WebService Count (Count) (num)"));
    }

    public void

    private boolean containsMeasure(List<?> list, String search) {
        for(Object text : list) {
            if(((DomText)text).getWholeText().equals(search)) {
                return true;
            }
        }
        return false;
    }
}
