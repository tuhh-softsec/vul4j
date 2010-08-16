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

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.ccm.model.CCMParser;
import hudson.plugins.ccm.model.CCMReport;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * <p>An extension point to execute a post build report generation for CCM.</p>
 * 
 * <p>It defines {@link CCMProjectAction} as Project Action and 
 * {@link CCMBuildAction} as an action for each build.</p>
 * 
 * <p>This publisher is not executed when the build status is ABORTED or 
 * FAILURE.</p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
@SuppressWarnings("unchecked")
public class CCMPublisher 
extends Recorder implements Serializable {

	@Extension
	public static final CCMPublisherDescriptor DESCRIPTOR = new CCMPublisherDescriptor();
	
	@DataBoundConstructor
	public CCMPublisher()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see hudson.tasks.BuildStep#getRequiredMonitorService()
	 */
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
	
	/**
	 * Defines the project action.
	 */
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) 
	{
		return new CCMProjectAction(project);
	}
	
	/**
	 * CCM doesn't need to continue if the build's status is ABORTED or FAILURE.
	 * 
	 * @param result
	 * @return true if build status is not ABORTED or FAILURE.
	 */
	protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }
	
	/**
	 * Performs the publishing of CCM results.
	 */
	@Override
	public boolean perform(
			AbstractBuild<?, ?> build, 
			Launcher launcher,
			BuildListener listener) 
	throws InterruptedException, IOException 
	{
		listener.getLogger().println("Performing CCM publisher...");
		// if build's status is not ABORTED or FAILURE
		if ( this.canContinue(build.getResult()) )
		{
			FilePath workspace = build.getWorkspace();
			PrintStream logger = listener.getLogger();
			
			CCMParser parser = new CCMParser(logger);
			CCMReport report;
			
			listener.getLogger().println("Generating report...");
			try{
                report = workspace.act(parser);
            
            }catch(IOException ioe){
                ioe.printStackTrace(logger);
                return false;
            
            }catch(InterruptedException ie){
                ie.printStackTrace(logger);
                return false;
            }
            
            CCMResult result = new CCMResult(report, build);
            CCMBuildAction buildAction = new CCMBuildAction(build, result);
            build.addAction( buildAction );
            
		} else {
			listener.getLogger().println("Canceling CCM publisher. Wrong project status.");
		}
		
		return true;
	}

}
