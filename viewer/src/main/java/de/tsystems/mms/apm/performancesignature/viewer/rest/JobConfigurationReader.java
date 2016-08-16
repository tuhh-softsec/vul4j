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

package de.tsystems.mms.apm.performancesignature.viewer.rest;

import de.tsystems.mms.apm.performancesignature.viewer.rest.model.ConfigurationTestCase;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class JobConfigurationReader {
    private final List<ConfigurationTestCase> configurationTestCases;

    public JobConfigurationReader() {
        configurationTestCases = new ArrayList<ConfigurationTestCase>();
    }

    //Get JDOM document from SAX Parser
    private static Document useSAXParser(final String xml) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new StringReader(xml));
    }

    void parseXML(final String xml) throws IOException, JDOMException {
        Document jdomDoc = useSAXParser(xml);
        Element root = jdomDoc.getRootElement();
        Element testCases = root.getChild("publishers").getChild("de.tsystems.mms.apm.performancesignature.dynatrace.PerfSigRecorder")
                .getChild("configurationTestCases");
        for(Element testCase : testCases.getChildren()) {
            ConfigurationTestCase configurationTestCase = new ConfigurationTestCase(testCase.getChildText("name"));
            for(Element dashboard : testCase.getChildren("singleDashboards")) {
                configurationTestCase.addSingleDashboard(dashboard
                        .getChild("de.tsystems.mms.apm.performancesignature.dynatrace.configuration.Dashboard").getChildText("name"));
            }
            for(Element dashboard : testCase.getChildren("comparisonDashboards")) {
                configurationTestCase.addComparisonDashboard(dashboard
                        .getChild("de.tsystems.mms.apm.performancesignature.dynatrace.configuration.Dashboard").getChildText("name"));
            }
            configurationTestCases.add(configurationTestCase);
        }
    }

    List<ConfigurationTestCase> getParsedObjects() {
        return this.configurationTestCases;
    }
}
