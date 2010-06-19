package hudson.plugins.ccm.publisher;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * A Class for the plugin freestyle (and other?) configuration 
 * screen in Hudson.
 * @author Bruno P. Kinoshita
 *
 */
public class CCMPublisherDescriptor 
extends BuildStepDescriptor<Publisher>
{

	/**
	 * a constructor
	 */
	public CCMPublisherDescriptor()
	{
		super(CCMPublisher.class);
		load();
	}
	
	/**
	 * Get the name to display in the configuration screen for projects.
	 * @return the name.
	 */
	public String getDisplayName()
	{
		return "Report CCM";
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#getHelpFile()
	 */
	@Override
	public String getHelpFile() {
		return "/plugin/ccm/help.html";
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#newInstance(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public Publisher newInstance(StaplerRequest req, JSONObject formData)
			throws hudson.model.Descriptor.FormException {
		CCMPublisher pub = new CCMPublisher();
		req.bindParameters(pub, "ccm.");
		req.bindParameters(pub.getConfig(), "config.");
		pub.getConfig().fix();
		return pub;
	}
	
	
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		return !AbstractMavenProject.class.isAssignableFrom(jobType);
	}
	
}
