package org.apache.commons.digester3.internal.rulesbinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.RuleProvider;
import org.apache.commons.digester3.spi.Rules;

final class ProvidersRegistry {

    /**
     * The data structure where storing the providers binding.
     */
    private final Collection<RegisteredProvider> providers = new ArrayList<RegisteredProvider>();

    private final Map<String, List<RuleProvider<? extends Rule>>> providersIndex =
        new HashMap<String, List<RuleProvider<? extends Rule>>>();

    /**
     * 
     *
     * @param <R>
     * @param <RP>
     * @param keyPattern
     * @param provider
     * @return
     */
    public <R extends Rule, RP extends RuleProvider<R>> void registerProvider(String keyPattern, RP provider) {
        this.providers.add(new RegisteredProvider(keyPattern, provider));

        List<RuleProvider<? extends Rule>> indexedProviders = this.providersIndex.get(keyPattern);
        if (indexedProviders == null) {
            indexedProviders = new ArrayList<RuleProvider<? extends Rule>>();
            this.providersIndex.put(keyPattern, indexedProviders);
        }
        indexedProviders.add(provider);
    }

    public <R extends Rule, RP extends RuleProvider<R>> RP getProvider(String keyPattern,
            /* @Nullable */ String namespaceURI,
            Class<RP> type) {
        List<RuleProvider<? extends Rule>> indexedProviders = this.providersIndex.get(keyPattern);

        if (indexedProviders == null || indexedProviders.isEmpty()) {
            return null;
        }

        for (RuleProvider<? extends Rule> ruleProvider  : indexedProviders) {
            if (type.isInstance(ruleProvider)) {
                if (namespaceURI == null) {
                    if (ruleProvider.getNamespaceURI() == null) {
                        return type.cast(ruleProvider);
                    }
                } else if (ruleProvider.getNamespaceURI() != null
                        && namespaceURI.equals(ruleProvider.getNamespaceURI())) {
                    return type.cast(ruleProvider);
                }
            }
        }

        return null;
    }

    public void registerRules(Rules rules) {
        for (RegisteredProvider registeredProvider : this.providers) {
            rules.add(registeredProvider.getPattern(), registeredProvider.getProvider().get());
        }
    }

    /**
     * Used to associate rule providers with paths in the rules binder.
     */
    private static final class RegisteredProvider {

        private final String pattern;

        private final RuleProvider<? extends Rule> provider;

        public <R extends Rule> RegisteredProvider(String pattern, RuleProvider<R> provider) {
            this.pattern = pattern;
            this.provider = provider;
        }

        public String getPattern() {
            return pattern;
        }

        public RuleProvider<? extends Rule> getProvider() {
            return provider;
        }

    }

}
