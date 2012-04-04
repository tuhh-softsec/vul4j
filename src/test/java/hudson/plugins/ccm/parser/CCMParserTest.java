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
	
}
