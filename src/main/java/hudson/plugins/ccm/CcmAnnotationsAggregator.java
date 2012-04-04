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

import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;

/**
 * Aggregates {@link CcmResultAction}s of {@link MatrixRun}s into
 * {@link MatrixBuild}.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmAnnotationsAggregator extends AnnotationsAggregator {

	/**
	 * @param build
	 * @param launcher
	 * @param listener
	 * @param healthDescriptor
	 * @param defaultEncoding
	 */
	public CcmAnnotationsAggregator(MatrixBuild build, Launcher launcher, BuildListener listener, HealthDescriptor healthDescriptor, String defaultEncoding) {
		super(build, launcher, listener, healthDescriptor, defaultEncoding);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AnnotationsAggregator#createAction(hudson.plugins.analysis.core.HealthDescriptor, java.lang.String, hudson.plugins.analysis.core.ParserResult)
	 */
	@Override
	protected Action createAction(HealthDescriptor healthDescriptor, String defaultEncoding, ParserResult aggregatedResult) {
		return new CcmResultAction(build, healthDescriptor, new CcmResult(build, defaultEncoding, aggregatedResult));
	}
	
	@Override
    protected boolean hasResult(final MatrixRun run) {
        return getAction(run) != null;
    }
	
	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.AnnotationsAggregator#getResult(hudson.matrix.MatrixRun)
	 */
	@Override
	protected BuildResult getResult(MatrixRun run) {
		return getAction(run).getResult();
	}

	private CcmResultAction getAction(final MatrixRun run) {
        return run.getAction(CcmResultAction.class);
    }

}
