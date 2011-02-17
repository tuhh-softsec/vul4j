package org.apache.commons.digester3.xmlrules.metaparser;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.RulesModule;
import org.apache.commons.digester3.rulesbinder.ConverterBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;

final class PrefixedRulesBinder implements RulesBinder {

    private final RulesBinder wrappedRulesBinder;

    private final String prefix;

    public PrefixedRulesBinder(RulesBinder wrappedRulesBinder, String prefix) {
        this.wrappedRulesBinder = wrappedRulesBinder;
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    public ClassLoader getContextClassLoader() {
        return this.wrappedRulesBinder.getContextClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    public void addError(String messagePattern, Object... arguments) {
        this.wrappedRulesBinder.addError(messagePattern, arguments);
    }

    /**
     * {@inheritDoc}
     */
    public void addError(Throwable t) {
        this.wrappedRulesBinder.addError(t);
    }

    /**
     * {@inheritDoc}
     */
    public void install(RulesModule rulesModule) {
        this.wrappedRulesBinder.install(rulesModule);
    }

    /**
     * {@inheritDoc}
     */
    public LinkedRuleBuilder forPattern(String pattern) {
        if (this.prefix != null && this.prefix.length() > 0) {
            pattern = this.prefix + '/' + pattern;
        }
        return this.wrappedRulesBinder.forPattern(pattern);
    }

    /**
     * {@inheritDoc}
     */
    public <T> ConverterBuilder<T> convert(Class<T> type) {
        return this.wrappedRulesBinder.convert(type);
    }

}
