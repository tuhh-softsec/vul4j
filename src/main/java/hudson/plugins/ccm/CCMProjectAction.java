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

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;
import hudson.util.Graph;

import java.io.IOException;
import java.util.Calendar;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class CCMProjectAction 
extends AbstractCCMAction
{

	private AbstractProject<?, ?> project;

	/**
	 * Creates new CCM project action.
	 * 
	 * @param project Project which this action will be applied to.
	 */
	public CCMProjectAction(AbstractProject<?, ?> project)
	{
		this.project = project;
	}

	/**
	 * Checks if it should display graph.
	 * 
	 * @return <code>true</code> if it should display graph and 
	 * 		   <code>false</code> otherwise.
	 */
	public final boolean isDisplayGraph()
	{
		Boolean displayGraph = Boolean.FALSE;
		
		if (project.getBuilds().size() > 0)
		{
			displayGraph = Boolean.TRUE;
		}

		return displayGraph;
	}
	
	/**
	 * Returns the last build action.
	 * 
	 * @return the last build action or <code>null</code> if there is no such
	 *         build action.
	 */
	public CCMBuildAction getLastBuildAction()
	{
		AbstractBuild<?, ?> lastBuild = getLastBuildWithCCM();
		CCMBuildAction action = null;
		if ( lastBuild != null )
		{
			action = lastBuild.getAction( CCMBuildAction.class );
		}
		return action;
	}

	/**
	 * Retrieves the last build with CCM in the project.
	 * 
	 * @return Last build with CCM in the project or <code>null</code>, 
	 * 		   if there is no build with CCM in the project.
	 */
	private AbstractBuild<?, ?> getLastBuildWithCCM()
	{
		AbstractBuild<?, ?> lastBuild = (AbstractBuild<?, ?>) project.getLastBuild();
		while (lastBuild != null
				&& lastBuild.getAction(CCMBuildAction.class) == null)
		{
			lastBuild = lastBuild.getPreviousBuild();
		}
		return lastBuild;
	}

	/**
	 * 
	 * Show CCM html report f the latest build. If no builds are associated 
	 * with CCM , returns info page.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doIndex( 
			final StaplerRequest request, 
			final StaplerResponse response ) 
	throws IOException
	{
		AbstractBuild<?, ?> lastBuild = getLastBuildWithCCM();
		if (lastBuild == null)
		{
			response.sendRedirect2("nodata");
		}
		else 
		{
			int buildNumber = lastBuild.getNumber();
			response.sendRedirect2( String.format("../%d/%s", buildNumber,
					CCMBuildAction.URL_NAME) );
		}
	}
	
	/**
	 * Sets CCM trend graph in the response.
	 * 
	 * @param request Request
	 * @param response Response
	 * @throws IOException
	 */
	public void doGraph( StaplerRequest request, StaplerResponse response )
			throws IOException
	{
		if (ChartUtil.awtProblemCause != null) 
		{
			response.sendRedirect2(request.getContextPath() + "/images/headless.png");
			return;
		}
		
		Calendar t = project.getLastCompletedBuild().getTimestamp();

		if ( request.checkIfModified(t, response) )
		{
			return;
		}
		
		Graph g = new CCMGraph(
				project.getLastBuild(), 
				CCMGraphHelper.createDataSetForProject(this.project), 
				"Number of metrics", 
				"Build number");
		g.doPng( request, response );
	}

}
