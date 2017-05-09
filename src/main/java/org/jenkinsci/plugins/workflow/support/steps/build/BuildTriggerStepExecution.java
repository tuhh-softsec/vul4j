package org.jenkinsci.plugins.workflow.support.steps.build;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.console.ModelHyperlinkNote;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Computer;
import hudson.model.Describable;
import hudson.model.Executor;
import hudson.model.Item;
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
import hudson.model.queue.ScheduleResult;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        Item item = Jenkins.getActiveInstance().getItem(job, invokingRun.getParent(), Item.class);
        if (item == null) {
            throw new AbortException("No item named " + job + " found");
        }
        item.checkPermission(Item.BUILD);
        if (step.getWait() && !(item instanceof Job)) {
            // TODO find some way of allowing ComputedFolders to hook into the listener code
            throw new AbortException("Waiting for non-job items is not supported");
        }
        if (item instanceof ParameterizedJobMixIn.ParameterizedJob) {
            final ParameterizedJobMixIn.ParameterizedJob project = (ParameterizedJobMixIn.ParameterizedJob) item;
            listener.getLogger().println("Scheduling project: " + ModelHyperlinkNote.encodeTo(project));

            node.addAction(new LabelAction(Messages.BuildTriggerStepExecution_building_(project.getFullDisplayName())));
            List<Action> actions = new ArrayList<>();
            if (step.getWait()) {
                StepContext context = getContext();
                actions.add(new BuildTriggerAction(context, step.isPropagate()));
                LOGGER.log(Level.FINER, "scheduling a build of {0} from {1}", new Object[]{project, context});
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
                @Override
                protected Job asJob() {
                    return (Job) project;
                }
            }.scheduleBuild2(quietPeriod, actions.toArray(new Action[actions.size()]));
            if (f == null) {
                throw new AbortException("Failed to trigger build of " + project.getFullName());
            }
        } else if (item instanceof Queue.Task){
            if (step.getParameters() != null && !step.getParameters().isEmpty()) {
                throw new AbortException("Item type does not support parameters");
            }
            Queue.Task task = (Queue.Task) item;
            listener.getLogger().println("Scheduling item: " + ModelHyperlinkNote.encodeTo(item));
            node.addAction(new LabelAction(Messages.BuildTriggerStepExecution_building_(task.getFullDisplayName())));
            List<Action> actions = new ArrayList<>();
            if (step.getWait()) {
                StepContext context = getContext();
                actions.add(new BuildTriggerAction(context, step.isPropagate()));
                LOGGER.log(Level.FINER, "scheduling a build of {0} from {1}", new Object[]{task, context});
            }
            actions.add(new CauseAction(new Cause.UpstreamCause(invokingRun)));
            Integer quietPeriod = step.getQuietPeriod();
            if (quietPeriod == null) {
                try {
                    Method getQuietPeriod = task.getClass().getMethod("getQuietPeriod");
                    if (getQuietPeriod.getReturnType().equals(int.class)) {
                        quietPeriod = (Integer) getQuietPeriod.invoke(task);
                    }
                } catch (NoSuchMethodException e) {
                    // ignore, best effort only
                } catch (IllegalAccessError | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING, "Could not determine quiet period of " + item.getFullName(), e);
                }
            }
            if (quietPeriod == null) {
                quietPeriod = Jenkins.getActiveInstance().getQuietPeriod();
            }
            ScheduleResult scheduleResult = Jenkins.getActiveInstance().getQueue().schedule2(task, quietPeriod,actions);
            if (scheduleResult.isRefused()) {
                throw new AbortException("Failed to trigger build of " + item.getFullName());
            }
        } else {
            throw new AbortException("The item named " + job + " is a "
                    + (item instanceof Describable
                    ? ((Describable) item).getDescriptor().getDisplayName()
                    : item.getClass().getName())
                    + " which is not something that can be built");
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

    @SuppressFBWarnings(value="RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", justification="TODO 1.653+ switch to Jenkins.getInstanceOrNull")
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
            for (BuildTriggerAction.Trigger trigger : BuildTriggerAction.triggersFor(i)) {
                if (trigger.context.equals(context)) {
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
            for (BuildTriggerAction.Trigger trigger : BuildTriggerAction.triggersFor((Run) exec)) {
                if (trigger.context.equals(context)) {
                    e.interrupt(Result.ABORTED, new BuildTriggerCancelledCause(cause));
                    trigger.interruption = cause;
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
            for (BuildTriggerAction.Trigger trigger : BuildTriggerAction.triggersFor(i)) {
                if (trigger.context.equals(getContext())) {
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
            for (BuildTriggerAction.Trigger trigger : BuildTriggerAction.triggersFor(run)) {
                if (trigger.context.equals(getContext())) {
                    return "running " + run;
                }
            }
        }
        return null;
    }

    private static final long serialVersionUID = 1L;

}
