package hudson.plugins.ccm.model;

import hudson.plugins.ccm.model.Metric;
import junit.framework.TestCase;

/**
 * Tests DefaultHandler.
 * 
 * @author César Fernandes de Almeida
 * @since 21/10/2010
 */
public class DefaultHandlerTest 
extends TestCase 
{
	Metric metric;
	
	public void testDefaultHandler()
	{
		metric = new Metric();
		metric.setFile("Test File 1");
		metric.setUnit("X");
		metric.setClassification("Good");
		metric.setComplexity(5);
		
		assertNotNull( metric.getFile() );
		assertNotNull( metric.getUnit() );
		assertNotNull( metric.getClassification() );
		assertNotNull( metric.getComplexity() );
	}
}
