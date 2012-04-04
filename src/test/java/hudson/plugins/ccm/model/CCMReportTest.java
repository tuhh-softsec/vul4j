package hudson.plugins.ccm.model;

import hudson.plugins.ccm.parser.CCMReport;
import hudson.plugins.ccm.parser.Metric;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests CCM Report.
 * 
 * @author Cesar Fernandes de Almeida
 * @since 21/10/2010
 */
public class CCMReportTest 
extends TestCase 
{
	CCMReport ccmReport;
	Metric metric;
	
	public void testCCMReport()
	{
		ccmReport = new CCMReport();
		
		List<Metric> metricList = new ArrayList<Metric>();
		
		metric = new Metric();
		metric.setFile("Test File 1");
		metric.setUnit("X");
		metric.setClassification("Good");
		metric.setComplexity(5);
		metricList.add(metric);
		
		metric = new Metric();
		metric.setFile("Test File 2");
		metric.setUnit("Y");
		metric.setClassification("OK");
		metric.setComplexity(3);
		metricList.add(metric);
		
		ccmReport.setMetrics(metricList);
		
		assertNotNull( ccmReport.getMetrics() );
		
		assertEquals(ccmReport.getNumberOfMethods(), 0);
		
		// Test Metrics Update
		ccmReport.updateNumbers();
		
		assertEquals(ccmReport.getNumberOfMethods(), 2);
		assertNotNull(ccmReport.getAverageComplexityPerMethod());
		assertNotNull(ccmReport.getTotalComplexity());
		
	}
}
