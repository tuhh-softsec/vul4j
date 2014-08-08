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
package com.google.jenkins.flakyTestHandler.plugin.deflake;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DeflakeActionTest {

  private final static String TEST_CLASS_ONE = "classOne";
  private final static String TEST_CLASS_TWO = "classTwo";

  private final static String TEST_METHOD_ONE = "methodOne";
  private final static String TEST_METHOD_TWO = "methodTwo";
  private final static String TEST_METHOD_THREE = "methodThree";

  @Test
  public void testGenerateMavenTestParamsForSingleTest() {
    Map<String, Set<String>> classMethodMap = new LinkedHashMap<String, Set<String>>();
    classMethodMap.put(TEST_CLASS_TWO, Sets.newHashSet(TEST_METHOD_THREE));
    testGenerateMavenTestParams(classMethodMap, "classTwo#methodThree",
        "Wrong test parameters for single test class");
  }

  @Test
  public void testGenerateMavenTestParamsForMultipleTests() {
    Map<String, Set<String>> classMethodMap = new LinkedHashMap<String, Set<String>>();
    classMethodMap.put(TEST_CLASS_ONE,
        Sets.newLinkedHashSet(Arrays.asList(TEST_METHOD_ONE, TEST_METHOD_TWO)));
    classMethodMap.put(TEST_CLASS_TWO, Sets.newHashSet(TEST_METHOD_THREE));
    testGenerateMavenTestParams(classMethodMap, "classOne#methodOne+methodTwo,classTwo#methodThree",
        "Wrong test parameters for multiple test classes");
  }

  private void testGenerateMavenTestParams(Map<String, Set<String>> classMethodMap,
      String expectedTestParam, String errorMsg) {
    DeflakeAction action = new DeflakeAction(classMethodMap);
    assertEquals(errorMsg, expectedTestParam,
        action.generateMavenTestParams());
  }
}