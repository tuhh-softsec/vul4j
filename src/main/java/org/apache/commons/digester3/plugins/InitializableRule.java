package org.apache.commons.digester3.plugins;

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
 * Defines an interface that a Rule class can implement if it wishes to get an initialisation callback after the rule
 * has been added to the set of Rules within a PluginRules instance.
 * 
 * @since 1.6
 */
public interface InitializableRule
{

    /**
     * Called after this Rule object has been added to the list of all Rules. Note that if a single InitializableRule
     * instance is associated with more than one pattern, then this method will be called more than once.
     * 
     * @param pattern is the digester match pattern that will trigger this rule.
     */
    void postRegisterInit( String pattern );

}
