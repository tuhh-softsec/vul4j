package org.jenkinsci.plugins.workflow.support.steps.build;

import hudson.AbortException;
import hudson.Extension;
import hudson.console.ModelHyperlinkNote;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import java.util.List;
import java.util.logging.Level;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.util.logging.Logger;
import static java.util.logging.Level.WARNING;
import javax.annotation.Nonnull;

@Extension
public class BuildTriggerListener extends RunListener<Run<?,?>>{

    private static final Logger LOGGER = Logger.getLogger(BuildTriggerListener.class.getName());

    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        for (BuildTriggerAction buildTriggerAction : run.getActions(BuildTriggerAction.class)) {
            StepContext stepContext = buildTriggerAction.getStepContext();
            if (stepContext != null && stepContext.isReady()) {
                LOGGER.log(Level.FINE, "started building {0} from #{1} in {2}", new Object[] {run, run.getQueueId(), stepContext});
                try {
                    TaskListener taskListener = stepContext.get(TaskListener.class);
                    // encodeTo(Run) calls getDisplayName, which does not include the project name.
                    taskListener.getLogger().println("Starting building: " + ModelHyperlinkNote.encodeTo("/" + run.getUrl(), run.getFullDisplayName()));
                } catch (Exception e) {
                    LOGGER.log(WARNING, null, e);
                }
            } else {
                LOGGER.log(Level.FINE, "{0} unavailable in {1}", new Object[] {stepContext, run});
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation") // TODO JENKINS-39404 Actionable offers no standard way of removing actions
    public void onCompleted(Run<?,?> run, @Nonnull TaskListener listener) {
        List<BuildTriggerAction> actions = run.getActions(BuildTriggerAction.class);
        for (BuildTriggerAction action : actions) {
            LOGGER.fine("completing " + run + " for " + action.getStepContext());
            if (!action.isPropagate() || run.getResult() == Result.SUCCESS) {
                if (action.interruption == null) {
                    action.getStepContext().onSuccess(new RunWrapper(run, false));
                } else {
                    action.getStepContext().onFailure(action.interruption);
                }
            } else {
                action.getStepContext().onFailure(new AbortException(run.getFullDisplayName() + " completed with status " + run.getResult() + " (propagate: false to ignore)"));
            }
        }
        run.getActions().removeAll(actions);
    }

    @Override
    public void onDeleted(Run<?,?> run) {
        for (BuildTriggerAction action : run.getActions(BuildTriggerAction.class)) {
            action.getStepContext().onFailure(new AbortException(run.getFullDisplayName() + " was deleted"));
        }
    }
}