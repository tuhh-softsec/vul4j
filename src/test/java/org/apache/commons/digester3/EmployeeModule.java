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

/**
 * Shared module that contains rules for parsing Employee/Address entities.
 */
final class EmployeeModule extends AbstractRulesModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        forPattern("employee").createObject().ofType(Employee.class);
        forPattern("employee/firstName").setBeanProperty();
        forPattern("employee/lastName").setBeanProperty();

        forPattern("employee/address")
            .createObject().ofType(Address.class)
            .then()
            .setNext("addAddress");
        forPattern("employee/address/type").setBeanProperty();
        forPattern("employee/address/city").setBeanProperty();
        forPattern("employee/address/state").setBeanProperty();
    }

}
