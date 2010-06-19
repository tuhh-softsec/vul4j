package hudson.plugins.ccm.publisher;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.parser.CCMResultParser;
import hudson.plugins.ccm.report.CCMReport;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;

public class CCMPublisher extends Recorder{

	private CCMPublisherConfig config = new CCMPublisherConfig();
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
	
	@Override
	public boolean perform(
			AbstractBuild<?, ?> build, 
			Launcher launcher,
			BuildListener listener) 
	throws InterruptedException, IOException {
		
		CCMResultParser parser = new CCMResultParser();
		
		/*
		 * root xml node <ccm> 
		 */
		CCM ccmXmlNode = parser.parse(null);
		
		CCMReport report = new CCMReport(ccmXmlNode);
		
		return true;
	}

	/**
	 * Get publisher configuration.
	 * @return
	 */
	public CCMPublisherConfig getConfig() {
		return this.config;
	}

}
