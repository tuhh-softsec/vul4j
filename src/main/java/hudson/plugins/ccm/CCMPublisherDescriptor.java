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
 * @since 16/08/2010
 */
package hudson.plugins.ccm;

import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * <p>Descriptor of {@link CCMPublisher}. </p>
 * 
 * <p>As we do not need any input from the user it does not override the 
 * {@link #configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)}  
 * method.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class CCMPublisherDescriptor 
extends BuildStepDescriptor<Publisher> 
{
	
	/*
	 * <p>Name that is shown in the build configuration screen. It appears 
	 * in the section "Post build actions".</p> 
	 */
	//private static final String DISPLAY_NAME = "Publish CCM Report";
	
	public CCMPublisherDescriptor()
	{
		super(CCMPublisher.class);
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Publish CCM Report";
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return Project.class.isAssignableFrom(jobType);
    }	
	
}
