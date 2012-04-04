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

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Project action of CCM plug-in. It displays the CCM graphs. The graph 
 * generation is done by {@link ResultAction}.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmProjectAction extends AbstractProjectAction<ResultAction<CcmResult>>  {
	/**
     * Instantiates a new {@link PmdProjectAction}.
     *
     * @param project
     *            the project that owns this action
     */
    public CcmProjectAction(final AbstractProject<?, ?> project, PluginDescriptor plugin) {
        this(project, CcmResultAction.class, plugin);
    }
    
    /**
     * Instantiates a new {@link PmdProjectAction}.
     *
     * @param project
     *            the project that owns this action
     * @param type
     *            the result action type
     */
    public CcmProjectAction(final AbstractProject<?, ?> project, final Class<? extends ResultAction<CcmResult>> type, PluginDescriptor plugin) {
        super(project, type, plugin);
    }

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return Messages._CCM_ProjectAction_Name().toString();
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AbstractProjectAction#getTrendName()
	 */
	@Override
	public String getTrendName() {
		return Messages._CCM_Trend_Name().toString();
	}
	
}