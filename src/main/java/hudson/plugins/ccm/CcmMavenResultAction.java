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

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;

import java.util.List;
import java.util.Map;

/**
 * A {@link CcmResultAction} for native Maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 3.0
 */
public class CcmMavenResultAction extends MavenResultAction<CcmResult> {

	public CcmMavenResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor, final String defaultEncoding) {
		super(new CcmResultAction(owner, healthDescriptor), defaultEncoding, "CCM");
	}
	
	public CcmMavenResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final CcmResult result) {
        super(new CcmResultAction(owner, healthDescriptor, result), defaultEncoding, "CCM");
    }

	/* (non-Javadoc)
	 * @see hudson.maven.AggregatableAction#createAggregatedAction(hudson.maven.MavenModuleSetBuild, java.util.Map)
	 */
	public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
		return new CcmMavenResultAction(build, getHealthDescriptor(), getDisplayName());
	}

	/* (non-Javadoc)
	 * @see hudson.maven.MavenAggregatedReport#getProjectAction(hudson.maven.MavenModuleSet)
	 */
	public Action getProjectAction(MavenModuleSet moduleSet) {
		return new CcmProjectAction(moduleSet, CcmMavenResultAction.class);
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.MavenResultAction#getIndividualActionType()
	 */
	@Override
	public Class<? extends MavenResultAction<CcmResult>> getIndividualActionType() {
		return CcmMavenResultAction.class;
	}

	/* (non-Javadoc)
	 * @see hudson.plugins.analysis.core.MavenResultAction#createResult(hudson.plugins.analysis.core.BuildResult, hudson.plugins.analysis.core.BuildResult)
	 */
	@Override
	protected CcmResult createResult(CcmResult existingResult, CcmResult additionalResult) {
		return new CcmReporterResult(getOwner(), additionalResult.getDefaultEncoding(), aggregate(existingResult, additionalResult));
	}

}
