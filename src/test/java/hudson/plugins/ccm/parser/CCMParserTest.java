package hudson.plugins.ccm.parser;

import hudson.plugins.analysis.util.model.FileAnnotation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * Tests CCMParser.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class CCMParserTest 
extends TestCase 
{

	/**
	 * Tests CCMParser parse method. It uses a xml present in the same package 
	 * of this class file.
	 */
	public void testParser()
	{
		CcmParser parser = new CcmParser();
		
		Collection<FileAnnotation> annotations = null;
		
		try 
		{
			String sFile = this.getClass().getResource("ccm.result.xml").getFile();
			annotations = parser.parse(new File(sFile), "ccm");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertNotNull(annotations);
		
	}

	public void testParseAndCalculateMetricPriorities()
	{
		CcmParser parser = new CcmParser();
		Collection<FileAnnotation> annotations = null;

		try
		{
			String sFile = this.getClass().getResource("pynamodb_ccm_results_sample.xml").getFile();
			annotations = parser.parse(new File(sFile), "ccm");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		int expectedNumberOfLowPriority = 1;
		int expectedNumberOfNormalPriority = 1;
		int expectedNumberOfHighPriority = 4;
		int numberOfLowPriorityFound = 0;
		int numberOfNormalPriorityFound = 0;
		int numberOfHighPriorityFound = 0;
		for (FileAnnotation annotation : annotations)
		{
			String annotationPriority = annotation.getPriority().toString().toLowerCase();
			if (annotationPriority.equals("low"))
			{
				numberOfLowPriorityFound++;
			}
			else if (annotationPriority.equals("normal"))
			{
				numberOfNormalPriorityFound++;
			}
			else if (annotationPriority.equals("high"))
			{
				numberOfHighPriorityFound++;
			}
		}

		assert expectedNumberOfLowPriority == numberOfLowPriorityFound;
		assert expectedNumberOfNormalPriority == numberOfNormalPriorityFound;
		assert expectedNumberOfHighPriority == numberOfHighPriorityFound;
	}

	public void testCalculateMetricPriorities()
	{
//		Metric metric1 = Metric()
	}
}
