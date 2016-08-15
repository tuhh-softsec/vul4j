/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.viewer.rest.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationTestCase {
    private final String name;
    private final List<String> singleDashboards;
    private final List<String> comparisonDashboards;

    public ConfigurationTestCase(final String name) {
        this.name = name;
        this.singleDashboards = new ArrayList<String>();
        this.comparisonDashboards = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void addSingleDashboard(final String dashboard) {
        this.singleDashboards.add(dashboard);
    }

    public void addComparisonDashboard(final String dashboard) {
        this.comparisonDashboards.add(dashboard);
    }

    @Nonnull
    public List<String> getSingleDashboards() {
        return singleDashboards;
    }

    @Nonnull
    public List<String> getComparisonDashboards() {
        return comparisonDashboards;
    }
}

