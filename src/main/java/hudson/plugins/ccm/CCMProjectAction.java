/**
 * 
 */
package hudson.plugins.ccm;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Actionable;

import org.kohsuke.stapler.StaplerProxy;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class CCMProjectAction 
extends Actionable 
implements Action, StaplerProxy{

	private AbstractProject<?, ?> project;
	
	public CCMProjectAction(AbstractProject<?, ?> project)
	{
		this.project = project;
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kohsuke.stapler.StaplerProxy#getTarget()
	 */
	public Object getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see hudson.search.SearchItem#getSearchUrl()
	 */
	public String getSearchUrl() {
		// TODO Auto-generated method stub
		return null;
	}

}
