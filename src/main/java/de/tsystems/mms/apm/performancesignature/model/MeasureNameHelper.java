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

package de.tsystems.mms.apm.performancesignature.model;

public class MeasureNameHelper {
    private final String chartDashlet, measure, description;

    public MeasureNameHelper(final String chartDashlet, final String measure, final String description) {
        this.chartDashlet = chartDashlet;
        this.measure = measure;
        this.description = description;
    }

    public String getChartDashlet() {
        return chartDashlet;
    }

    public String getMeasure() {
        return measure;
    }

    public String getDescription() {
        return description;
    }
}
