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
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.util.Graph;

import java.io.IOException;
import java.io.Serializable;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class CCMProjectAction 
extends Actionable 
implements ProminentProjectAction, Serializable{

	
	//public static final String DISPLAY_NAME = "CCM Results";
	public static final String ICON_FILE_NAME = "/plugin/ccm/icons/ccm-24.png";
	public static final String URL_NAME = "ccmResult";
	
	public static final int CHART_WIDTH = 500;
    public static final int CHART_HEIGHT = 200;
	
	private AbstractProject<?, ?> project;
	
	public CCMProjectAction(AbstractProject<?, ?> project)
	{
		this.project = project;
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return Messages.CCM_Project_Action_CCMResults();
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
	
	/**
    *
    * Redirects the index page to the last result.
    *
    * @param request
    *            Stapler request
    * @param response
    *            Stapler response
    * @throws IOException
    *             in case of an error
    */
   public void doIndex(final StaplerRequest request, final StaplerResponse response) throws IOException {
       AbstractBuild<?, ?> build = getLastFinishedBuild();
       if (build != null) {
           response.sendRedirect2(String.format("../%d/%s", build.getNumber(), CCMBuildAction.URL_NAME));
       }
   }

   /**
    * Returns the last finished build.
    *
    * @return the last finished build or <code>null</code> if there is no
    *         such build
    */
   public AbstractBuild<?, ?> getLastFinishedBuild() {
       AbstractBuild<?, ?> lastBuild = project.getLastBuild();
       while (lastBuild != null && (lastBuild.isBuilding() || lastBuild.getAction(CCMBuildAction.class) == null)) {
           lastBuild = lastBuild.getPreviousBuild();
       }
       return lastBuild;
   }
   
   public final boolean hasValidResults() {
       AbstractBuild<?, ?> build = getLastFinishedBuild();
       if (build != null) {
           CCMBuildAction resultAction = build.getAction(CCMBuildAction.class);
           if (resultAction != null) {
               return resultAction.getPreviousResult() != null;
           }
       }
       return false;
   }

   /**
    * Display the trend map. Delegates to the the associated
    * {@link ResultAction}.
    *
    * @param request
    *            Stapler request
    * @param response
    *            Stapler response
    * @throws IOException
    *             in case of an error
    */
   public void doTrendMap(
		   final StaplerRequest request, 
		   final StaplerResponse response) 
   throws IOException 
   {
	   AbstractBuild<?,?> lastBuild = this.getLastFinishedBuild();
	   CCMBuildAction lastAction = lastBuild.getAction(CCMBuildAction.class);
	   XYDataset dataset = ChartUtil.createXYDataset(lastAction);
	   final JFreeChart chart = ChartUtil.buildXYChart(dataset);
	   
	   new Graph(-1,CHART_WIDTH,CHART_HEIGHT) {
           protected JFreeChart createGraph() {
               return chart;
           }
       }.doMap(request,response);
       
   }

	/**
    * Display the trend graph. Delegates to the the associated
    * {@link ResultAction}.
    *
    * @param request
    *            Stapler request
    * @param response
    *            Stapler response
    * @throws IOException
    *             in case of an error in
    *             {@link ResultAction#doGraph(StaplerRequest, StaplerResponse, int)}
    */
   public void doTrend(final StaplerRequest request, final StaplerResponse response) throws IOException {
	   AbstractBuild<?,?> lastBuild = this.getLastFinishedBuild();
	   CCMBuildAction lastAction = lastBuild.getAction(CCMBuildAction.class);
	   XYDataset dataset = ChartUtil.createXYDataset(lastAction);
	   final JFreeChart chart = ChartUtil.buildXYChart(dataset);
	   
	   new Graph(-1,CHART_WIDTH,CHART_HEIGHT) {
           protected JFreeChart createGraph() {
               return chart;
           }
       }.doPng(request,response);
	   
   }

   /* (non-Javadoc)
	 * @see hudson.search.SearchItem#getSearchUrl()
	 */
	public String getSearchUrl() {
		return "ccmResult";
	}

}
