package org.jenkinsci.plugins.workflow.support.steps.build;

import hudson.Extension;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Describable;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ItemVisitor;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Queue;
import hudson.model.TopLevelItem;
import hudson.util.FormValidation;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import jenkins.model.ParameterizedJobMixIn;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.util.StaplerReferer;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class BuildTriggerStep extends AbstractStepImpl {

    private final String job;
    private List<ParameterValue> parameters;
    private boolean wait = true;
    private boolean propagate = true;
    private Integer quietPeriod;

    @DataBoundConstructor
    public BuildTriggerStep(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public List<ParameterValue> getParameters() {
        return parameters;
    }

    @DataBoundSetter public void setParameters(List<ParameterValue> parameters) {
        this.parameters = parameters;
    }

    public boolean getWait() {
        return wait;
    }

    @DataBoundSetter public void setWait(boolean wait) {
        this.wait = wait;
    }

    public Integer getQuietPeriod() {
        return quietPeriod;
    }

    @DataBoundSetter public void setQuietPeriod(Integer quietPeriod) {
        this.quietPeriod = quietPeriod;
    }

    public boolean isPropagate() {
        return propagate;
    }

    @DataBoundSetter public void setPropagate(boolean propagate) {
        this.propagate = propagate;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(BuildTriggerStepExecution.class);
        }

        @Override public Step newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            BuildTriggerStep step = (BuildTriggerStep) super.newInstance(req, formData);
            // Cf. ParametersDefinitionProperty._doBuild:
            Object parameter = formData.get("parameter");
            JSONArray params = parameter != null ? JSONArray.fromObject(parameter) : null;
            if (params != null) {
                Jenkins jenkins = Jenkins.getInstance();
                Job<?,?> context = StaplerReferer.findItemFromRequest(Job.class);
                Job<?,?> job = jenkins != null ? jenkins.getItem(step.getJob(), context, Job.class) : null;
                if (job != null) {
                    ParametersDefinitionProperty pdp = job.getProperty(ParametersDefinitionProperty.class);
                    if (pdp != null) {
                        List<ParameterValue> values = new ArrayList<ParameterValue>();
                        for (Object o : params) {
                            JSONObject jo = (JSONObject) o;
                            String name = jo.getString("name");
                            ParameterDefinition d = pdp.getParameterDefinition(name);
                            if (d == null) {
                                throw new IllegalArgumentException("No such parameter definition: " + name);
                            }
                            ParameterValue parameterValue = d.createValue(req, jo);
                            if (parameterValue != null) {
                                values.add(parameterValue);
                            } else {
                                throw new IllegalArgumentException("Cannot retrieve the parameter value: " + name);
                            }
                        }
                        step.setParameters(values);
                    }
                }
            }
            return step;
        }

        @Override
        public String getFunctionName() {
            return "build";
        }

        @Override
        public String getDisplayName() {
            return "Build a job";
        }

        public AutoCompletionCandidates doAutoCompleteJob(@AncestorInPath ItemGroup<?> container, @QueryParameter final String value) {
            // TODO remove code copy&pasted from AutoCompletionCandidates.ofJobNames when it supports union of classes
            final AutoCompletionCandidates candidates = new AutoCompletionCandidates();
            class Visitor extends ItemVisitor {
                String prefix;

                Visitor(String prefix) {
                    this.prefix = prefix;
                }

                @Override
                public void onItem(Item i) {
                    String n = contextualNameOf(i);
                    if ((n.startsWith(value) || value.startsWith(n))
                            // 'foobar' is a valid candidate if the current value is 'foo'.
                            // Also, we need to visit 'foo' if the current value is 'foo/bar'
                            && (value.length() > n.length() || !n.substring(value.length()).contains("/"))
                            // but 'foobar/zot' isn't if the current value is 'foo'
                            // we'll first show 'foobar' and then wait for the user to type '/' to show the rest
                            && i.hasPermission(Item.READ)
                        // and read permission required
                            ) {
                        if ((i instanceof ParameterizedJobMixIn.ParameterizedJob || i instanceof Queue.Task) && n.startsWith(value))
                            candidates.add(n);

                        // recurse
                        String oldPrefix = prefix;
                        prefix = n;
                        super.onItem(i);
                        prefix = oldPrefix;
                    }
                }

                private String contextualNameOf(Item i) {
                    if (prefix.endsWith("/") || prefix.length() == 0)
                        return prefix + i.getName();
                    else
                        return prefix + '/' + i.getName();
                }
            }

            if (container == null || container == Jenkins.getInstance()) {
                new Visitor("").onItemGroup(Jenkins.getInstance());
            } else {
                new Visitor("").onItemGroup(container);
                if (value.startsWith("/"))
                    new Visitor("/").onItemGroup(Jenkins.getInstance());

                for (String p = "../"; value.startsWith(p); p += "../") {
                    container = ((Item) container).getParent();
                    new Visitor(p).onItemGroup(container);
                }
            }
            return candidates;
            // END of copy&paste
        }

        @Restricted(DoNotUse.class) // for use from config.jelly
        public String getContext() {
            Job<?,?> job = StaplerReferer.findItemFromRequest(Job.class);
            return job != null ? job.getFullName() : null;
        }

        public FormValidation doCheckPropagate(@QueryParameter boolean value, @QueryParameter boolean wait) {
            if (!value && !wait) {
                return FormValidation.warningWithMarkup(Messages.BuildTriggerStep_explicitly_disabling_both_propagate_and_wait());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckWait(@AncestorInPath ItemGroup<?> context, @QueryParameter boolean value, @QueryParameter String job) {
            if (!value) {
                return FormValidation.ok();
            }
            Item item = Jenkins.getActiveInstance().getItem(job, context, Item.class);
            if (item == null) {
                return FormValidation.ok();
            }
            if (item instanceof Job) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.BuildTriggerStep_no_wait_for_non_jobs());
        }

        public FormValidation doCheckJob(@AncestorInPath ItemGroup<?> context, @QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.ok();
            }
            Item item = Jenkins.getActiveInstance().getItem(value, context, Item.class);
            if (item == null) {
                return FormValidation.error(Messages.BuildTriggerStep_cannot_find(value));
            }
            if (item instanceof Job) {
                return FormValidation.ok();
            }
            if (item instanceof Queue.Task) {
                return FormValidation.ok();
            }
            if (item instanceof Describable) {
                return FormValidation.error(Messages.BuildTriggerStep_unsupported(((Describable)item).getDescriptor().getDisplayName()));
            }
            return FormValidation.error(Messages.BuildTriggerStep_unsupported(item.getClass().getName()));
        }

    }
}
