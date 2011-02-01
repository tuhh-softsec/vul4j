/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class manages the creation of Digester instances from digester rules modules.
 */
public final class DigesterLoader {

    /**
     * Creates a new {@link DigesterLoader} instance given one or more {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     * @return A new {@link DigesterLoader} instance
     */
    public static DigesterLoader newLoader(RulesModule...rulesModules) {
        if (rulesModules == null || rulesModules.length == 0) {
            throw new DigesterLoadingException("At least one RulesModule has to be specified");
        }
        return newLoader(Arrays.asList(rulesModules));
    }

    /**
     * Creates a new {@link DigesterLoader} instance given a collection of {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     * @return A new {@link DigesterLoader} instance
     */
    public static DigesterLoader newLoader(Collection<RulesModule> rulesModules) {
        if (rulesModules == null || rulesModules.isEmpty()) {
            throw new DigesterLoadingException("At least one RulesModule has to be specified");
        }

        return new DigesterLoader(rulesModules);
    }

    /**
     * The concrete {@link RulesBinder} implementation.
     */
    private final RulesBinderImpl rulesBinder = new RulesBinderImpl();

    /**
     * Creates a new {@link DigesterLoader} instance given a collection of {@link RulesModule} instance.
     *
     * @param rulesModules The modules containing the {@code Rule} binding
     */
    private DigesterLoader(Collection<RulesModule> rulesModules) {
        for (RulesModule rulesModule : rulesModules) {
            rulesModule.configure(this.rulesBinder);
        }

        // check if there were errors while binding rules
        if (this.rulesBinder.containsErrors()) {
            throw new DigesterLoadingException(this.rulesBinder.getErrors());
        }
    }

}
