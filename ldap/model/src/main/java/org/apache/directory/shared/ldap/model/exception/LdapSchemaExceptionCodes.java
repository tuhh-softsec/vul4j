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
package org.apache.directory.shared.ldap.model.exception;


import org.apache.directory.shared.ldap.model.schema.SchemaManager;


/**
 * This enum contains all the various codes that can be used to report issues 
 * during the integrity check of the schema by the {@link SchemaManager}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LdapSchemaExceptionCodes
{
    // Codes for all Schema Objects

    /** Characterizing a SO with an OID being already registered */
    OID_ALREADY_REGISTERED,
    
    /** Characterizing a SO with a name being already registered */
    NAME_ALREADY_REGISTERED,

    /** Characterizing an SO with a nonexistent schema */
    NONEXISTENT_SCHEMA,
    
    // Codes for Attribute Type

    /** Characterizing an AT with a nonexistent superior */
    AT_NONEXISTENT_SUPERIOR,

    /** Characterizing an AT sub-typing a Collective AT  */
    AT_CANNOT_SUBTYPE_COLLECTIVE_AT,

    /** Characterizing an AT containing a cycle in its type hierarchy */
    AT_CYCLE_TYPE_HIERARCHY,

    /** Characterizing an AT with a nonexistent syntax */
    AT_NONEXISTENT_SYNTAX,

    /** Characterizing an AT has no syntax and no superior */
    AT_SYNTAX_OR_SUPERIOR_REQUIRED,

    /** Characterizing an AT with a nonexistent equality matching rule */
    AT_NONEXISTENT_EQUALITY_MATCHING_RULE,

    /** Characterizing an AT with a nonexistent ordering matching rule */
    AT_NONEXISTENT_ORDERING_MATCHING_RULE,

    /** Characterizing an AT with a nonexistent substring matching rule */
    AT_NONEXISTENT_SUBSTRING_MATCHING_RULE,

    /** Characterizing an AT which has a different usage than its superior */
    AT_MUST_HAVE_SAME_USAGE_THAN_SUPERIOR,

    /** Characterizing an AT which has a 'userApplications' usage but is not user modifiable */
    AT_USER_APPLICATIONS_USAGE_MUST_BE_USER_MODIFIABLE,

    /** Characterizing an AT which is collective but does not have a 'userApplications' usage */
    AT_COLLECTIVE_MUST_HAVE_USER_APPLICATIONS_USAGE,

    /** Characterizing an AT which is collective and is single-valued */
    AT_COLLECTIVE_CANNOT_BE_SINGLE_VALUED,

    // Codes for Object Class

    /** Characterizing an abstract OC which inherits from an OC not being abstract */
    OC_ABSTRACT_MUST_INHERIT_FROM_ABSTRACT_OC,

    /** Characterizing an auxiliary OC which inherits from a structural OC */
    OC_AUXILIARY_CANNOT_INHERIT_FROM_STRUCTURAL_OC,

    /** Characterizing a structural OC which inherits from an auxiliary OC */
    OC_STRUCTURAL_CANNOT_INHERIT_FROM_AUXILIARY_OC,

    /** Characterizing an OC with a nonexistent superior */
    OC_NONEXISTENT_SUPERIOR,

    /** Characterizing an OC containing a cycle in its class hierarchy */
    OC_CYCLE_CLASS_HIERARCHY,

    /** Characterizing an OC with a collective AT in its must ATs list */
    OC_COLLECTIVE_NOT_ALLOWED_IN_MUST,

    /** Characterizing an OC with a collective AT in its may ATs list */
    OC_COLLECTIVE_NOT_ALLOWED_IN_MAY,

    /** Characterizing an OC with a duplicated AT in its must ATs list */
    OC_DUPLICATE_AT_IN_MUST,

    /** Characterizing an OC with a duplicated AT in its may ATs list */
    OC_DUPLICATE_AT_IN_MAY,

    /** Characterizing an OC with a nonexistent AT in its must ATs list */
    OC_NONEXISTENT_MUST_AT,

    /** Characterizing an OC with a nonexistent AT in its may ATs list */
    OC_NONEXISTENT_MAY_AT,

    /** Characterizing an OC with a duplicated AT in its may and must ATs list */
    OC_DUPLICATE_AT_IN_MAY_AND_MUST,
    
    // Codes for Matching Rule

    /** Characterizing a MR with a nonexistent syntax */
    MR_NONEXISTENT_SYNTAX,
}
