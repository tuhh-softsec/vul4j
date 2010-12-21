package hudson.plugins.ccm.model;

import hudson.plugins.ccm.model.CCMXmlHandler;
import junit.framework.TestCase;

/**
 * Tests CCMXmlHandler.
 * 
 * @author César Fernandes de Almeida
 * @since 21/10/2010
 */
public class CCMXmlHandlerTest 
extends TestCase 
{
	CCMXmlHandler ccmXmlHandler;
	
	public void testCCM()
	{
		ccmXmlHandler = new CCMXmlHandler();
	}
}
