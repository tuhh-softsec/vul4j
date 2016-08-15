package de.tsystems.mms.apm.performancesignature.viewer;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.Job;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.util.ViewerUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class ViewerWaitForJob extends Builder implements SimpleBuildStep {
    static final int waitForPollingInterval = 5000;
    private final String jenkinsJob;

    @DataBoundConstructor
    public ViewerWaitForJob(final String jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        JenkinsServerConfiguration serverConfiguration = ViewerUtils.getServerConfiguration(jenkinsJob);
        if (serverConfiguration == null)
            throw new AbortException("failed to lookup Jenkins server configuration");

        CredJobPair pair = serverConfiguration.getCredJobPair(jenkinsJob);
        if (pair == null)
            throw new AbortException("failed to lookup Jenkins job");

        ServerConnection serverConnection = new ServerConnection(serverConfiguration, pair);
        if (!serverConnection.validateConnection()) {
            throw new RESTErrorException(Messages.PerfSigRecorder_DTConnectionError());
        }

        Job perfSigJob = serverConnection.getJenkinsJob();
        boolean buildInQueue = perfSigJob.details().isInQueue();
        while (buildInQueue) {
            Thread.sleep(waitForPollingInterval);
            buildInQueue = perfSigJob.details().isInQueue();
        }

        ViewerEnvInvisAction envInvisAction = run.getAction(ViewerEnvInvisAction.class);
        int buildNumber;
        if (envInvisAction != null) {
            buildNumber = envInvisAction.getCurrentBuild();
        } else {
            buildNumber = perfSigJob.details().getLastBuild().getNumber();
        }

        Build build = perfSigJob.details().getBuildByNumber(buildNumber);
        logger.println("waiting for job " + perfSigJob.getName() + " #" + build.getNumber() + " to finish ...");

        if (build.details().isBuilding()) {
            boolean buildFinished = build.details().isBuilding();
            while (buildFinished) {
                Thread.sleep(waitForPollingInterval);
                buildFinished = build.details().isBuilding();
            }
        }
        logger.println("Jenkins job finished ...");
        BuildResult buildResult = build.details().getResult();

        logger.println("Jenkins job status: " + buildResult);
        if (!buildResult.equals(BuildResult.SUCCESS) && !buildResult.equals(BuildResult.UNSTABLE)) {
            String output = build.details().getConsoleOutputText();
            logger.println(output.substring(StringUtils.lastOrdinalIndexOf(output, "\n", 5) + 1)); //get the last 5 lines of console output
            throw new AbortException("jenkins job failed");
        }
    }

    public String getJenkinsJob() {
        return jenkinsJob;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public ListBoxModel doFillJenkinsJobItems() {
            return ViewerUtils.listToListBoxModel(ViewerUtils.getJenkinsConfigurations());
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Wait for Jenkins Job till finished";
        }
    }
}
