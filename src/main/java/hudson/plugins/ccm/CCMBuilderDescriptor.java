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
 * @since 1.0
 */
public class CCMBuilderDescriptor 
extends Descriptor<Builder>{

	//private static final String DISPLAY_NAME = "Invoke CCM";
	
	@CopyOnWrite
	private volatile CCMBuilderInstallation[] installations = new CCMBuilderInstallation[0];
	
	public CCMBuilderDescriptor()
	{
		super(CCMBuilder.class);
		load();
	}
	
	/**
     * This human readable name is used in the configuration screen.
     */
	@Override
	public String getDisplayName() {
		return "Invoke CCM";
	}
	
	public CCMBuilderInstallation[] getInstallations()
	{
		return this.installations;
	}
	
	/**
	 * <p>Called when the user hits save button.</p> 
	 * 
	 * <p>Saves the user input data and creates a {@link CCMBuilderInstallation}
	 * .</p>
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
	throws hudson.model.Descriptor.FormException 
	{
		this.installations = 
			req.bindParametersToList(CCMBuilderInstallation.class, "CCM.")
			.toArray(new CCMBuilderInstallation[0]);
		save();
		return true;
	}

}
