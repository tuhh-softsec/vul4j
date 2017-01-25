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
import hudson.model.Project;
import hudson.model.Run;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PerfSigTestActionTest {
    private final String TEST_PROJECT_WITH_HISTORY = "projectAction";
    @Rule
    public JenkinsRule j = new JenkinsRule();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @LocalData
    @Test
    public void testTestReportPage() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        Run<?, ?> build = proj.getBuildByNumber(11157);
        assertNotNull("We should have a build with number 11157", build);
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage testReport = wc.getPage(build, "testReport");

        assertEquals(testReport.getByXPath("//*[@id=\"main-panel-content\"]/table[2]/tbody/tr/td[1]/div[1]/a").size(), 11);
        j.assertXPath(testReport, "//*[contains(@id,\"collapseid\")]/div/table/tbody/tr[1]/td[1]/b");
        j.assertXPathValue(testReport, "//*[contains(@id,\"collapseid\")]/div/table/tbody/tr[1]/td[1]/b/text()", "PurePaths - PurePath CPU Duration (ms)");
        j.assertXPath(testReport, "//*[contains(@id,\"collapseid\")]/div/p/text()[2]");
        assertEquals(StringUtils.trim(((DomText) testReport.getByXPath("//*[contains(@id,\"collapseid\")]/div/p/text()[2]").get(0)).getWholeText()),
                "Status: Failed\n" +
                        "         ");
    }

    @LocalData
    @Test
    public void testTestReportPageDetail() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        Run<?, ?> build = proj.getBuildByNumber(11157);
        assertNotNull("We should have a build with number 11157", build);
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage testReport = wc.getPage(build, "testReport/com.dynatrace.easytravel.util/ZipUtilsTest/testZipSrcDirExclude/");

        j.assertXPath(testReport, "//*[@id=\"main-panel-content\"]/table/tbody/tr/td/div/table/tbody/tr[1]/td[2]");
        j.assertXPathValue(testReport, "//*[@id=\"main-panel-content\"]/table/tbody/tr/td/div/table/tbody/tr[1]/td[2]/text()", "0.22");
        j.assertXPath(testReport, "//*[@id=\"main-panel-content\"]/table/tbody/tr/td/div/p/text()[2]");
        assertEquals(StringUtils.trim(((DomText) testReport.getByXPath("//*[@id=\"main-panel-content\"]/table/tbody/tr/td/div/p/text()[2]").get(0))
                .getWholeText()), "Status: Failed\n" +
                "         ");
    }
}
