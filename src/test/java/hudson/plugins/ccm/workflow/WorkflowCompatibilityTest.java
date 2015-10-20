package hudson.plugins.ccm.workflow;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.FilePath;

import hudson.model.Result;
import hudson.plugins.ccm.CcmResultAction;

import static org.junit.Assert.*;

/**
 * Test workflow compatibility.
 */
public class WorkflowCompatibilityTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    /**
     * Run a workflow job using {@link CcmPublisher} and check for success.
     */
    @Test
    public void ccmPublisherWorkflowStep() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "wf");
        FilePath workspace = j.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("ccm.cs");
        report.copyFrom(WorkflowCompatibilityTest.class.getResourceAsStream("/hudson/plugins/ccm/parser/ccm.result.xml"));
        job.setDefinition(new CpsFlowDefinition(
        "node {" +
        "  step([$class: 'CcmPublisher'])" +
        "}"));
        j.assertBuildStatusSuccess(job.scheduleBuild2(0));
        CcmResultAction result = job.getLastBuild().getAction(CcmResultAction.class);
        assertEquals(22, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link CcmPublisher} with a failing threshols of 0, so the given example file
     * "/hudson/plugins/ccm/parser/ccm.result.xml" will make the build to fail.
     */
    @Test
    public void ccmPublisherWorkflowStepSetLimits() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "wf2");
        FilePath workspace = j.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("ccm.test.xml");
        report.copyFrom(WorkflowCompatibilityTest.class.getResourceAsStream("/hudson/plugins/ccm/parser/ccm.result.xml"));
        job.setDefinition(new CpsFlowDefinition(
        "node {" +
        "  step([$class: 'CcmPublisher', pattern: '**/ccm.test.xml', failedTotalAll: '0', usePreviousBuildAsReference: false])" +
        "}"));
        j.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        CcmResultAction result = job.getLastBuild().getAction(CcmResultAction.class);
        assertEquals(22, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link CcmPublisher} with a unstable threshols of 0, so the given example file
     * "/hudson/plugins/ccm/parser/ccm.result.xml" will make the build to fail.
     */
    @Test
    public void ccmPublisherWorkflowStepFailure() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "wf3");
        FilePath workspace = j.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("ccm.xml");
        report.copyFrom(WorkflowCompatibilityTest.class.getResourceAsStream("/hudson/plugins/ccm/parser/ccm.result.xml"));
        job.setDefinition(new CpsFlowDefinition(
        "node {" +
        "  step([$class: 'CcmPublisher', pattern: '**/ccm.xml', unstableTotalAll: '0', usePreviousBuildAsReference: false])" +
        "}"));
        j.assertBuildStatus(Result.UNSTABLE, job.scheduleBuild2(0).get());
        CcmResultAction result = job.getLastBuild().getAction(CcmResultAction.class);
        assertEquals(22, result.getResult().getAnnotations().size());
    }
}
