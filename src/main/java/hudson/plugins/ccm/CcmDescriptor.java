/*
 * The MIT License
 *
 * Copyright (c) <2012> <Bruno P. Kinoshita>
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

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Descriptor for {@link CcmPublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
@Extension(ordinal = 100)
public class CcmDescriptor extends PluginDescriptor {
	/** Plug-in name. */
    static final String PLUGIN_NAME = "ccm";
    /** Icon to use for the result and project action. */
    static final String ICON_URL = "/plugin/ccm/icons/ccm-24x24.png";
    /** The URL of the result action. */
    static final String RESULT_URL = PluginDescriptor.createResultUrlName(PLUGIN_NAME);
	
	/**
	 * @param clazz
	 */
	public CcmDescriptor() {
		super(CcmPublisher.class);
	}

	@Override
    public String getDisplayName() {
        return Messages.CCM_Publisher_Name();
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public String getIconUrl() {
        return ICON_URL;
    }
}
