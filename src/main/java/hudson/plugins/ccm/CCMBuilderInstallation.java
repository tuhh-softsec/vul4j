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

import java.io.File;

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
	
	public File getExecutable()
	{
		return new File(this.pathToCCM);
	}
	
	/**
     * Returns true if the executable exists.
     */
	public boolean getExists()
	{
		return getExecutable().exists();
	}
	
}
