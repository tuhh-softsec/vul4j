package hudson.plugins.ccm.parser;

import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.model.Metric;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


public class CCMParserTest  {

	@Test
	public void testParserResult()
	{
		CCMResultParser parser = new CCMResultParser();
		try {
			CCM ccm = parser.parse(new File("src/main/test/hudson/plugins/ccm/parser/ccm.result.xml"));
			
			List<Metric> metrics = ccm.getMetrics();
			
			for (Iterator<Metric> iterator = metrics.iterator(); iterator.hasNext();) {
				Metric metric = iterator.next();
				
				System.out.println(metric);
				
			}
		} catch (IOException e) {
			Assert.fail("Error parsing ccm result: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
