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

import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.plugins.ccm.parser.CCMReport;

import java.io.Serializable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
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
