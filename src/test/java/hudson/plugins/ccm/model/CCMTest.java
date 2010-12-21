package hudson.plugins.ccm.model;

import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.model.Metric;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests CCM.
 * 
 * @author César Fernandes de Almeida
 * @since 21/10/2010
 */
public class CCMTest 
extends TestCase 
{
	CCM ccm;
	Metric metric;
	
	public void testCCM()
	{
		ccm = new CCM();
		
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
		
		ccm.setMetrics(metricList);
		
		// Test getter and setter
		assertNotNull( ccm.getMetrics() );
		
	}
}
