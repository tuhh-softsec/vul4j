/**
 * 
 */
package hudson.plugins.ccm.config;

import java.io.File;
import java.io.IOException;

import hudson.FilePath.FileCallable;
import hudson.plugins.ccm.CCMBuilder;
import hudson.remoting.VirtualChannel;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class CCMResultCallable 
implements FileCallable<String>
{
	
	/* (non-Javadoc)
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File, hudson.remoting.VirtualChannel)
	 */
	public String invoke(File workspace, VirtualChannel channel) 
	throws IOException,
	InterruptedException 
	{
		return new File( workspace, CCMBuilder.CCM_RESULT_FILE ).getAbsolutePath();
	}

}
