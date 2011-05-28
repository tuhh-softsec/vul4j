package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Regular expression matching strategy for RegexRules.
 * 
 * @since 1.5
 */
public abstract class RegexMatcher
{

    /**
     * Returns true if the given pattern matches the given path according to the regex algorithm that this strategy
     * applies.
     * 
     * @param pathPattern the standard digester path representing the element
     * @param rulePattern the regex pattern the path will be tested against
     * @return true if the given pattern matches the given path
     */
    public abstract boolean match( String pathPattern, String rulePattern );

}
