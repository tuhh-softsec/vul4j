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
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;

/**
 * Represents the results of CCM analysis.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmResult extends BuildResult {

	private static final long serialVersionUID = -5116183927528793013L;
	
	/**
	 * @param build
	 * @param defaultEncoding
	 * @param result
	 */
	public CcmResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final ParserResult result) {
		super(build, defaultEncoding, result, new BuildHistory(build, CcmResultAction.class));
	}
	
	/* (non-Javadoc)
	 * @see hudson.model.ModelObject#getDisplayName()
	 */
	public String getDisplayName() {
		return Messages.CCM_ProjectAction_Name();
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.BuildResult#getSerializationFileName()
	 */
	@Override
	protected String getSerializationFileName() {
		return "ccm-warnings.xml";
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.BuildResult#getResultActionType()
	 */
	@Override
	protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
		return CcmResultAction.class;
	}

	@Override
	public String getSummary() {
		return "CCM: " + createDefaultSummary(CcmDescriptor.RESULT_URL, getNumberOfAnnotations(), getNumberOfModules());
	}
	
}
