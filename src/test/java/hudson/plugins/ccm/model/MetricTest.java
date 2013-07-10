package hudson.plugins.ccm.model;

import hudson.plugins.ccm.parser.Metric;
import junit.framework.TestCase;

/**
 * Tests Metric.
 * 
 * @author Cesar Fernandes de Almeida
 * @since 21/10/2010
 */
public class MetricTest 
extends TestCase 
{
	Metric metric;
	
	public void testMetric()
	{
		metric = new Metric();
		metric.setFile("Test File 1");
		metric.setUnit("X");
		metric.setClassification("Good");
		metric.setComplexity(5);
		metric.setStartLineNumber(1);
		metric.setEndLineNumber(2);
		
		assertNotNull( metric.getFile() );
		assertNotNull( metric.getUnit() );
		assertNotNull( metric.getClassification() );
		assertNotNull( metric.getComplexity() );
		assertNotNull( metric.getStartLineNumber() );
		assertNotNull( metric.getEndLineNumber() );
	}
}
