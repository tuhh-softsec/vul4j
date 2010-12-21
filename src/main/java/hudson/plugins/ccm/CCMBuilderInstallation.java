/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.ccm;

import hudson.Launcher;
import hudson.Launcher.LocalLauncher;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;


/**
 * <p>CCM Builder Installation.</p>
 * 
 * <p>Holds data provided by the user in the global screen of configuration.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class CCMBuilderInstallation 
implements Serializable
{

	private String name;
	private String pathToCCM;
	
	@DataBoundConstructor
	public CCMBuilderInstallation(String name, String pathToCCM)
	{
		this.name = name;
		this.pathToCCM = pathToCCM;
	}

	/**
     * Human readable display name.
     */
	public String getName() {
		return name;
	}

	public String getPathToCCM() {
		return pathToCCM;
	}
	
	/**
	 * Gets the path to the CCM executable.
	 * 
	 * @param launcher Hudson launcher
	 * @return Path to the CCM executable
	 * @throws InterruptedException
	 * @throws IOException If the executable does not exist
	 */
	public String getExecutable(Launcher launcher) 
	throws InterruptedException, IOException
	{
		VirtualChannel channel = launcher.getChannel();
		String executable = channel.call(
			new Callable<String, IOException>()
			{
				/* (non-Javadoc)
				 * @see hudson.remoting.Callable#call()
				 */
				public String call() 
				throws IOException
				{
					File exe = getExecutableFile();
					if ( exe.exists() )
					{
						return exe.getPath();
					}
					
					throw new IOException( Messages.CCM_Builder_Installation_NoExe( exe ) );
				}
			}
		);
		
		return executable;
	}
	
	/**
	 * Gets the executable file of CCM.
	 * 
	 * @return CCM executable file
	 */
	public File getExecutableFile()
	{
		return new File(pathToCCM);
	}
	
	/**
     * Returns true if the executable exists.
     */
	public boolean getExists() 
	throws InterruptedException, IOException
	{
		LocalLauncher launcher = new LocalLauncher(TaskListener.NULL);
		VirtualChannel channel = launcher.getChannel();
		
		Boolean result = channel.call(
			new Callable<Boolean, IOException>()
			{
				
				public Boolean call() 
				throws IOException 
				{
					return getExecutableFile().exists();
				};
				
			}
		);
		
		return result;
	}
	
}
