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
public class CCMDescriptor 
extends Descriptor<Builder> {

	private static final String DISPLAY_NAME = "Invoke CCM";
	
	@CopyOnWrite
	private volatile CCMInstallation[] installations = new CCMInstallation[0];
	
	public CCMDescriptor()
	{
		super(CCMBuilder.class);
		load();
	}
	
	/**
     * This human readable name is used in the configuration screen.
     */
	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}	
	
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
			throws hudson.model.Descriptor.FormException {
		this.installations = req.bindParametersToList(CCMInstallation.class, "CCM.").
				toArray(new CCMInstallation[0]);
		save();
		return true;		
	}

	public CCMInstallation[] getInstallations() {
		return installations;
	}

}
