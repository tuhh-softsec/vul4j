package br.eti.kinoshita.ccm.model;

import hudson.plugins.ccm.CCMBuildAction;
import hudson.plugins.ccm.CCMResult;
import hudson.plugins.ccm.model.CCMParser;
import hudson.plugins.ccm.model.CCMReport;
import hudson.plugins.ccm.model.Metric;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		CCMParser parser = new CCMParser(System.out);
		
		CCMReport report = null;
		
		try 
		{
			String sFile = CCMParserTest.class.getResource("ccm.result.xml").getFile();
			report = parser.invoke(new File(sFile).getParentFile(), null);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertNotNull(report);
		
		List<Metric> metrics = report.getMetrics();
		
		assertNotNull(metrics);
		
		report.updateNumbers();
		
		CCMResult result = new CCMResult(report, null);
		CCMBuildAction buildAction = new CCMBuildAction(null, result);
		
		assertTrue(buildAction.getResult().getReport().getAverageComplexityPerMethod() > 0 );
		
	}
	
}
