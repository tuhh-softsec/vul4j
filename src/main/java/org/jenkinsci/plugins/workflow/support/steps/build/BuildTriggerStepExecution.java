package org.jenkinsci.plugins.workflow.support.steps.build;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import hudson.AbortException;
import hudson.console.ModelHyperlinkNote;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Queue;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.queue.QueueTaskFuture;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import org.jenkinsci.plugins.workflow.actions.LabelAction;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

/**
 * @author Vivek Pandey
 */
public class BuildTriggerStepExecution extends AbstractStepExecutionImpl {

    private static final Logger LOGGER = Logger.getLogger(BuildTriggerStepExecution.class.getName());

    @StepContextParameter
    private transient TaskListener listener;
    @StepContextParameter private transient Run<?,?> invokingRun;
    @StepContextParameter private transient FlowNode node;

    @Inject(optional=true) transient BuildTriggerStep step;

    @SuppressWarnings({"unchecked", "rawtypes"}) // cannot get from ParameterizedJob back to ParameterizedJobMixIn trivially
    @Override
    public boolean start() throws Exception {
        String job = step.getJob();
        final ParameterizedJobMixIn.ParameterizedJob project = Jenkins.getActiveInstance().getItem(job, invokingRun.getParent(), ParameterizedJobMixIn.ParameterizedJob.class);
        if (project == null) {
            throw new AbortException("No parameterized job named " + job + " found");
        }
        listener.getLogger().println("Scheduling project: " + ModelHyperlinkNote.encodeTo(project));

        node.addAction(new LabelAction(Messages.BuildTriggerStepExecution_building_(project.getFullDisplayName())));
        List<Action> actions = new ArrayList<Action>();
        if (step.getWait()) {
            StepContext context = getContext();
            actions.add(new BuildTriggerAction(context, step.isPropagate()));
            LOGGER.log(Level.FINER, "scheduling a build of {0} from {1}", new Object[] {project, context});
        }
        actions.add(new CauseAction(new Cause.UpstreamCause(invokingRun)));
        List<ParameterValue> parameters = step.getParameters();
        if (parameters != null) {
            parameters = completeDefaultParameters(parameters, (Job) project);
            actions.add(new ParametersAction(parameters));
        }
        Integer quietPeriod = step.getQuietPeriod();
        // TODO use new convenience method in 1.621
        if (quietPeriod == null) {
            quietPeriod = project.getQuietPeriod();
        }
        QueueTaskFuture<?> f = new ParameterizedJobMixIn() {
            @Override protected Job asJob() {
                return (Job) project;
            }
        }.scheduleBuild2(quietPeriod, actions.toArray(new Action[actions.size()]));
        if (f == null) {
            throw new AbortException("Failed to trigger build of " + project.getFullName());
        }

        if (step.getWait()) {
            return false;
        } else {
            getContext().onSuccess(null);
            return true;
        }
    }

    private List<ParameterValue> completeDefaultParameters(List<ParameterValue> parameters, Job<?,?> project) {
        List<ParameterValue> completeListOfParameters = Lists.newArrayList(parameters);
        List<String> names = Lists.transform(parameters, new Function<ParameterValue, String>() {
            @Override public String apply(ParameterValue input) {
                return input.getName();
            }
        });
        if (project != null) {
            ParametersDefinitionProperty pdp = project.getProperty(ParametersDefinitionProperty.class);
            if (pdp != null) {
                for (ParameterDefinition pDef : pdp.getParameterDefinitions()) {
                    if (!names.contains(pDef.getName())) {
                        ParameterValue defaultP = pDef.getDefaultParameterValue();
                        if (defaultP != null) {
                            completeListOfParameters.add(defaultP);
                        }
                    }
                }
            }
        }
        return completeListOfParameters;
    }

    @Override
    public void stop(Throwable cause) {
        StepContext context = getContext();
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            context.onFailure(cause);
            return;
        }

        boolean interrupted = false;

        Queue q = jenkins.getQueue();
        // if the build is still in the queue, abort it.
        // BuildQueueListener will report the failure, so this method shouldn't call getContext().onFailure()
        for (Queue.Item i : q.getItems()) {
            for (BuildTriggerAction bta : i.getActions(BuildTriggerAction.class)) {
                if (bta.getStepContext().equals(context)) {
                    // Note that it is a little questionable to cancel the queue item in case it has other causes,
                    // but in the common case that this is the only cause, it is most intuitive to do so.
                    // The same applies to aborting the actual build once started.
                    q.cancel(i);
                    interrupted = true;
                }
            }
        }

        // if there's any in-progress build already, abort that.
        // when the build is actually aborted, BuildTriggerListener will take notice and report the failure,
        // so this method shouldn't call getContext().onFailure()
        for (Computer c : jenkins.getComputers()) {
            for (Executor e : c.getExecutors()) {
                interrupted |= maybeInterrupt(e, cause, context);
            }
            for (Executor e : c.getOneOffExecutors()) {
                interrupted |= maybeInterrupt(e, cause, context);
            }
        }

        if (!interrupted) {
            context.onFailure(cause);
        }
    }
    private static boolean maybeInterrupt(Executor e, Throwable cause, StepContext context) {
        boolean interrupted = false;
        Queue.Executable exec = e.getCurrentExecutable();
        if (exec instanceof Run) {
            for (BuildTriggerAction bta : ((Run) exec).getActions(BuildTriggerAction.class)) {
                if (bta.getStepContext().equals(context)) {
                    e.interrupt(Result.ABORTED, new BuildTriggerCancelledCause(cause));
                    bta.interruption = cause;
                    try {
                        ((Run) exec).save();
                    } catch (IOException x) {
                        LOGGER.log(Level.WARNING, "failed to save interrupt cause on " + exec, x);
                    }
                    interrupted = true;
                }
            }
        }
        return interrupted;
    }

    @Override public String getStatus() {
        for (Queue.Item i : Queue.getInstance().getItems()) {
            for (BuildTriggerAction bta : i.getActions(BuildTriggerAction.class)) {
                if (bta.getStepContext().equals(getContext())) {
                    return "waiting to schedule " + i.task.getFullDisplayName() + "; blocked: " + i.getWhy();
                }
            }
        }
        for (Computer c : Jenkins.getActiveInstance().getComputers()) {
            for (Executor e : c.getExecutors()) {
                String r = running(e);
                if (r != null) {
                    return r;
                }
            }
            for (Executor e : c.getOneOffExecutors()) {
                String r = running(e);
                if (r != null) {
                    return r;
                }
            }
        }
        // TODO QueueTaskFuture does not allow us to record the queue item ID
        return "unsure what happened to downstream build";
    }
    private @CheckForNull String running(@Nonnull Executor e) {
        Queue.Executable exec = e.getCurrentExecutable();
        if (exec instanceof Run) {
            Run<?,?> run = (Run) exec;
            for (BuildTriggerAction bta : run.getActions(BuildTriggerAction.class)) {
                if (bta.getStepContext().equals(getContext())) {
                    return "running " + run;
                }
            }
        }
        return null;
    }

    private static final long serialVersionUID = 1L;

}
