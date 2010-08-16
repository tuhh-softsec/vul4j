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

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.ccm.model.CCMReport;

import java.io.Serializable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class CCMBuildAction 
implements Action, Serializable, StaplerProxy{

	public static final String DISPLAY_NAME = "CCM";
	public static final String ICON_FILE_NAME = "/plugin/ccm/icons/ccm-24.png";
	public static final String URL_NAME = "ccmResult";

	private AbstractBuild<?, ?> build;
	private CCMResult result;
	
	public CCMBuildAction(AbstractBuild<?, ?> build, CCMResult result) {
		this.build = build;
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName() {
		return ICON_FILE_NAME;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		return URL_NAME;
	}

	/* (non-Javadoc)
	 * @see org.kohsuke.stapler.StaplerProxy#getTarget()
	 */
	public Object getTarget() {
		return this.result;
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}
	
	public CCMResult getResult() {
		return result;
	}
	
	private CCMReport getPreviousReport()
	{
		CCMResult previousResult = this.getPreviousResult();
		CCMReport previousReport = null;
		if ( previousResult != null )
		{
			previousReport = previousResult.getReport();
		}
		return previousReport;
	}
	
	public CCMResult getPreviousResult()
	{
		CCMBuildAction previousAction = this.getPreviousAction();
		CCMResult previousResult = null;
		if ( previousAction != null )
		{
			previousResult = previousAction.getResult();
		}
		return previousResult;
	}
	
	public CCMBuildAction getPreviousAction()
	{
		if ( this.build != null )
		{
			AbstractBuild<?, ?> previousBuild = this.build.getPreviousBuild();
			if ( previousBuild != null )
			{
				return previousBuild.getAction(CCMBuildAction.class);
			}
		}
		return null;
	}
	
	public String getSummary(){
        return ReportSummary.createReportSummary(result.getReport(), this.getPreviousReport());
    }

    public String getDetails(){
        return ReportSummary.createReportSummaryDetails(result.getReport(), this.getPreviousReport());
    }

}
