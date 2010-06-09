package hudson.plugins.ccm;

import java.io.File;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * CCM Installation
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 7 april, 2010
 */
public class CCMInstallation {

	private String name;
	private String pathToCCM;
	
	@DataBoundConstructor
	public CCMInstallation( String name, String pathToCCM )
	{
		this.name = name;
		this.pathToCCM = pathToCCM;
	}

	/**
     * Human readable display name.
     */
	public String getName() {
		return name;
	}

	public String getPathToCCM() {
		return pathToCCM;
	}
	
	public File getExecutable()
	{
		return new File(this.pathToCCM);
	}
	
	/**
     * Returns true if the executable exists.
     */
	public boolean getExists()
	{
		return getExecutable().exists();
	}
	
}
