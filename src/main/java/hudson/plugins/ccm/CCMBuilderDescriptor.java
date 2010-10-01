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

import hudson.CopyOnWrite;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for {@link CCMBuilder}. Used as a singleton.
 * The class is marked as public so that it can be accessed from views.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 7 april, 2010
 */
public class CCMBuilderDescriptor 
extends Descriptor<Builder>{

	//private static final String DISPLAY_NAME = "Invoke CCM";
	
	@CopyOnWrite
	private volatile CCMBuilderInstallation[] installations = new CCMBuilderInstallation[0];
	
	public CCMBuilderDescriptor()
	{
		super(CCMBuilder.class);
		load();
	}
	
	/**
     * This human readable name is used in the configuration screen.
     */
	@Override
	public String getDisplayName() {
		return "Invoke CCM";
	}
	
	public CCMBuilderInstallation[] getInstallations()
	{
		return this.installations;
	}
	
	/**
	 * <p>Called when the user hits save button.</p> 
	 * 
	 * <p>Saves the user input data and creates a {@link CCMBuilderInstallation}
	 * .</p>
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
	throws hudson.model.Descriptor.FormException 
	{
		this.installations = 
			req.bindParametersToList(CCMBuilderInstallation.class, "CCM.")
			.toArray(new CCMBuilderInstallation[0]);
		save();
		return true;
	}

}
