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

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Controls the live cycle of the CCM results. This action persists the
 * results of the CCM analysis of a build and displays the results on the
 * build page. The actual visualization of the results is defined in the
 * matching <code>summary.jelly</code> file.
 * <p>
 * Moreover, this class renders the PMD result trend.
 * </p>
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmResultAction extends AbstractResultAction<CcmResult> {

	/**
	 * @param owner
	 * @param healthDescriptor
	 * @param result
	 */
	public CcmResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor, final CcmResult result) {
		super(owner, new CcmHealthDescriptor(healthDescriptor), result);
	}
	
	/**
	 * @param owner
	 * @param healthDescriptor
	 */
	public CcmResultAction(AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor) {
		super(owner, new CcmHealthDescriptor(healthDescriptor));
	}

	/* (non-Javadoc)
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return Messages.CCM_ProjectAction_Name();
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AbstractResultAction#getDescriptor()
	 */
	@Override
	protected PluginDescriptor getDescriptor() {
		return new CcmDescriptor();
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AbstractResultAction#getMultipleItemsTooltip(int)
	 */
	@Override
	protected String getMultipleItemsTooltip(int numberOfItems) {
		return Messages.CCM_ResultAction_MultipleWarnings(numberOfItems);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AbstractResultAction#getSingleItemTooltip()
	 */
	@Override
	protected String getSingleItemTooltip() {
		return Messages.CCM_ResultAction_OneWarning();
	}

}
