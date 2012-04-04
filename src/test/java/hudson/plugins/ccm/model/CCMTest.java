package hudson.plugins.ccm.model;

import hudson.plugins.ccm.parser.Ccm;
import hudson.plugins.ccm.parser.Metric;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests CCM.
 * 
 * @author Cesar Fernandes de Almeida
 * @since 21/10/2010
 */
public class CCMTest 
extends TestCase 
{
	Ccm ccm;
	Metric metric;
	
	public void testCCM()
	{
		ccm = new Ccm();
		
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
