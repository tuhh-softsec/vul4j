/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.util;


import java.util.List;


/**
 * Monitor used to track existence or duplication of components.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ComponentsMonitor
{

    /**
     * Use a component.
     *
     * @param component the component
     * @return this components monitor
     * @throws IllegalArgumentException if the component is already used
     */
    ComponentsMonitor useComponent( String component ) throws IllegalArgumentException;


    /**
     * Check if all components are used.
     *
     * @return true if all components are used
     */
    boolean allComponentsUsed();


    /**
     * Checks if the final state is valid. That depends whether the components are mandatory
     * or optional.
     *
     * @return true if the final state is valid
     */
    boolean finalStateValid();


    /**
     * Gets the remaining components.
     *
     * @return the remaining components
     */
    List<String> getRemainingComponents();

}
