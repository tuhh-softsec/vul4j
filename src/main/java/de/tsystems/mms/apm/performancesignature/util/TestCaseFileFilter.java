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

package de.tsystems.mms.apm.performancesignature.util;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * Created by rapi on 13.06.2014.
 */
public class TestCaseFileFilter implements FileFilter, Serializable {
    private static final long serialVersionUID = 1L;
    private final String testCase;

    public TestCaseFileFilter(final String testCase) {
        this.testCase = testCase;
    }

    public boolean accept(final File pathname) {
        return pathname.getName().matches(testCase);
    }
}
