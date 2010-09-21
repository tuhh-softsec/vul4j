/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 7 april, 2010
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
 * @since 7 april, 2010
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
					
					throw new IOException(exe.getPath() + " doesn't exist");
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
