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

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.model.JSONDashlet;
import de.tsystems.mms.apm.performancesignature.ui.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.model.Project;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import java.util.List;

import static org.junit.Assert.*;

public class PerfSigProjectActionTest {

    private final String TEST_PROJECT_WITH_HISTORY = "projectAction";
    @Rule
    public JenkinsRule j = new JenkinsRule();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @LocalData
    @Test
    public void testProjectActionChartsFloatingBox() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        JenkinsRule.WebClient wc = j.createWebClient();

        HtmlPage projectPage = wc.getPage(proj);
        j.assertAllImageLoadSuccessfully(projectPage);
        assertEquals(projectPage.getByXPath("//*[@id=\"tabList\"]/li/a").size(), 2); //no JS available :(

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        List<JSONDashlet> configuration = new Gson().fromJson(projectAction.getDashboardConfiguration("PerfTest"), new TypeToken<List<JSONDashlet>>() {
        }.getType());
        assertEquals(11, configuration.size());
        assertTrue(containsDashlet(configuration, "Database - DB Count (Count)"));
        assertTrue(containsDashlet(configuration, "WebServiceTime - Time (Average)"));
    }

    @LocalData
    @Test
    public void testProjectActionCharts() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage projectPage = wc.getPage(proj, "performance-signature");

        j.assertAllImageLoadSuccessfully(projectPage);
        assertEquals(projectPage.getByXPath("//*[@id=\"gridster-UnitTest\"]/ul/li/a/img").size(), 10);
        assertEquals(projectPage.getByXPath("//*[@id=\"gridster-PerfTest\"]/ul/li/a/img").size(), 10);
        List<?> list = projectPage.getByXPath("//*[@id=\"DataTables_Table_1\"]/thead/tr/th/text()");
        assertTrue(TestUtils.containsMeasure(list, "Total GC Utilization (Average) (%)"));
        assertTrue(TestUtils.containsMeasure(list, "WebService Count (Count) (num)"));
    }

    @LocalData
    @Test
    public void testSummerizerGraph() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        JenkinsRule.WebClient wc = j.createWebClient();

        Page trendGraphPage = wc.goTo(proj.getUrl() + "/performance-signature/summarizerGraph?id=19571aabda401cc01546d7ebd62e0e58", "image/png");
        j.assertGoodStatus(trendGraphPage);

        exception.expect(FailingHttpStatusCodeException.class);
        wc.goTo(proj.getUrl() + "/performance-signature/summarizerGraph?id=20571aabda401cc01546d7ebd62e0e58", "image/png");
    }

    @LocalData
    @Test
    public void testWebMethods() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        JenkinsRule.WebClient wc = j.createWebClient();

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        assertEquals(PerfSigUIUtils.class, projectAction.getPerfSigUIUtils());
        /*projectAction.getFilteredChartDashlets();
        projectAction.getAggregationFromMeasure();
        projectAction.getAvailableMeasures();
        projectAction.getDashboardReports();
        projectAction.getLastDashboardReports();*/
    }

    public void testGridConfiguration() {

    }

    private boolean containsDashlet(List<JSONDashlet> list, String search) {
        for (JSONDashlet jsonDashlet : list) {
            if (jsonDashlet.generateDashletName().equals(search)) {
                return true;
            }
        }
        return false;
    }
}
