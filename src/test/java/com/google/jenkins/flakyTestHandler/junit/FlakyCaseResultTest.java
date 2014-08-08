/* Copyright 2014 Google Inc. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.jenkins.flakyTestHandler.junit;

import junit.framework.TestCase;

import org.jvnet.localizer.LocaleProvider;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Test class copied from hudson.tasks.junit.CaseResultTest
 *
 * https://github.com/jenkinsci/jenkins/blob/master/core/src/test/java/hudson/tasks/junit/
 * CaseResultTest.java
 */
public class FlakyCaseResultTest extends TestCase {

    public FlakyCaseResultTest(String name) {
        super(name);
    }

    // @Bug(6824)
    public void testLocalizationOfStatus() throws Exception {
        LocaleProvider old = LocaleProvider.getProvider();
        try {
            final AtomicReference<Locale> locale = new AtomicReference<Locale>();
            LocaleProvider.setProvider(new LocaleProvider() {
                public @Override Locale get() {
                    return locale.get();
                }
            });
            locale.set(Locale.GERMANY);
            assertEquals("Erfolg", FlakyCaseResult.Status.PASSED.getMessage());
            locale.set(Locale.US);
            assertEquals("Passed", FlakyCaseResult.Status.PASSED.getMessage());
        } finally {
            LocaleProvider.setProvider(old);
        }
    }

}
