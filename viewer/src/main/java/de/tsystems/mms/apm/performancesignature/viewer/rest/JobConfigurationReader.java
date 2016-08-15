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

public class JobConfigurationReader {
    private final List<ConfigurationTestCase> configurationTestCases;

    public JobConfigurationReader() {
        configurationTestCases = new ArrayList<ConfigurationTestCase>();
    }

    //Get JDOM document from SAX Parser
    private static Document useSAXParser(final String xml) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new StringReader(xml));
    }

    public void parseXML(final String xml) throws IOException, JDOMException {
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

    public List<ConfigurationTestCase> getParsedObjects() {
        return this.configurationTestCases;
    }
}
