/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.model.constants;


/**
 * Apache meta schema specific constants used throughout the server.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
//This will suppress PMD.AvoidUsingHardCodedIP warnings in this class
public final class MetaSchemaConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private MetaSchemaConstants()
    {
    }

    public final static String SCHEMA_NAME = "apachemeta";
    public final static String SCHEMA_OTHER = "other";

    // -- objectClass names --
    public final static String META_TOP_OC                      = "metaTop";
    public final static String META_TOP_OC_OID                  = "1.3.6.1.4.1.18060.0.4.0.3.1";
    
    public final static String META_OBJECT_CLASS_OC             = "metaObjectClass";
    public final static String META_OBJECT_CLASS_OC_OID         = "1.3.6.1.4.1.18060.0.4.0.3.2";
    
    public final static String META_ATTRIBUTE_TYPE_OC           = "metaAttributeType";
    public final static String META_ATTRIBUTE_TYPE_OC_OID       = "1.3.6.1.4.1.18060.0.4.0.3.3";
    
    public final static String META_SYNTAX_OC                   = "metaSyntax";
    public final static String META_SYNTAX_OC_OID               = "1.3.6.1.4.1.18060.0.4.0.3.4";

    public final static String META_MATCHING_RULE_OC            = "metaMatchingRule";
    public final static String META_MATCHING_RULE_OC_OID        = "1.3.6.1.4.1.18060.0.4.0.3.5";
    
    public final static String META_DIT_STRUCTURE_RULE_OC       = "metaDITStructureRule";
    public final static String META_DIT_STRUCTURE_RULE_OC_OID   = "1.3.6.1.4.1.18060.0.4.0.3.6";
    
    public final static String META_NAME_FORM_OC                = "metaNameForm";
    public final static String META_NAME_FORM_OC_OID            = "1.3.6.1.4.1.18060.0.4.0.3.7";

    public final static String META_MATCHING_RULE_USE_OC        = "metaMatchingRuleUse";
    public final static String META_MATCHING_RULE_USE_OC_OID    = "1.3.6.1.4.1.18060.0.4.0.3.8";

    public final static String META_DIT_CONTENT_RULE_OC         = "metaDITContentRule";
    public final static String META_DIT_CONTENT_RULE_OC_OID     = "1.3.6.1.4.1.18060.0.4.0.3.9";
    
    public final static String META_SYNTAX_CHECKER_OC           = "metaSyntaxChecker";
    public final static String META_SYNTAX_CHECKER_OC_OID       = "1.3.6.1.4.1.18060.0.4.0.3.10";
    
    public final static String META_SCHEMA_OC                   = "metaSchema";
    public final static String META_SCHEMA_OC_OID               = "1.3.6.1.4.1.18060.0.4.0.3.11";
    
    public final static String META_NORMALIZER_OC               = "metaNormalizer";
    public final static String META_NORMALIZER_OC_OID           = "1.3.6.1.4.1.18060.0.4.0.3.12";
    
    public final static String META_COMPARATOR_OC               = "metaComparator";
    public final static String META_COMPARATOR_OC_OID           = "1.3.6.1.4.1.18060.0.4.0.3.13";


    // -- attributeType names --
    public final static String M_OID_AT                         = "m-oid";
    public final static String M_OID_AT_OID                     = "1.3.6.1.4.1.18060.0.4.0.2.1 ";

    public final static String M_NAME_AT                        = "m-name";
    public final static String M_NAME_AT_OID                    = "1.3.6.1.4.1.18060.0.4.0.2.2 ";

    public final static String M_DESCRIPTION_AT                 = "m-description";
    public final static String M_DESCRIPTION_AT_OID             = "1.3.6.1.4.1.18060.0.4.0.2.3 ";
    
    public final static String M_OBSOLETE_AT                    = "m-obsolete";
    public final static String M_OBSOLETE_AT_OID                = "1.3.6.1.4.1.18060.0.4.0.2.4 ";
    
    public final static String M_SUP_OBJECT_CLASS_AT            = "m-supObjectClass";
    public final static String M_SUP_OBJECT_CLASS_AT_OID        = "1.3.6.1.4.1.18060.0.4.0.2.5 ";
    
    public final static String M_MUST_AT                        = "m-must";
    public final static String M_MUST_AT_OID                    = "1.3.6.1.4.1.18060.0.4.0.2.6 ";

    public final static String M_MAY_AT                         = "m-may";
    public final static String M_MAY_AT_OID                     = "1.3.6.1.4.1.18060.0.4.0.2.7 ";

    public final static String M_TYPE_OBJECT_CLASS_AT           = "m-typeObjectClass";
    public final static String M_TYPE_OBJECT_CLASS_AT_OID       = "1.3.6.1.4.1.18060.0.4.0.2.8 ";
    
    public final static String M_SUP_ATTRIBUTE_TYPE_AT          = "m-supAttributeType";
    public final static String M_SUP_ATTRIBUTE_TYPE_AT_OID      = "1.3.6.1.4.1.18060.0.4.0.2.10";

    public final static String M_EQUALITY_AT                    = "m-equality";
    public final static String M_EQUALITY_AT_OID                = "1.3.6.1.4.1.18060.0.4.0.2.11";

    public final static String M_ORDERING_AT                    = "m-ordering";
    public final static String M_ORDERING_AT_OID                = "1.3.6.1.4.1.18060.0.4.0.2.12";

    public final static String M_SUBSTR_AT                      = "m-substr";
    public final static String M_SUBSTR_AT_OID                  = "1.3.6.1.4.1.18060.0.4.0.2.13";

    public final static String M_SYNTAX_AT                      = "m-syntax";
    public final static String M_SYNTAX_AT_OID                  = "1.3.6.1.4.1.18060.0.4.0.2.14";

    public final static String M_SINGLE_VALUE_AT                = "m-singleValue";
    public final static String M_SINGLE_VALUE_AT_OID            = "1.3.6.1.4.1.18060.0.4.0.2.15";
    
    public final static String M_COLLECTIVE_AT                  = "m-collective";
    public final static String M_COLLECTIVE_AT_OID              = "1.3.6.1.4.1.18060.0.4.0.2.16";

    public final static String M_NO_USER_MODIFICATION_AT        = "m-noUserModification";
    public final static String M_NO_USER_MODIFICATION_AT_OID    = "1.3.6.1.4.1.18060.0.4.0.2.17";

    public final static String M_USAGE_AT                       = "m-usage";
    public final static String M_USAGE_AT_OID                   = "1.3.6.1.4.1.18060.0.4.0.2.18";
    
    public final static String M_RULE_ID_AT                     = "m-ruleId";
    public final static String M_RULE_ID_AT_OID                 = "1.3.6.1.4.1.18060.0.4.0.2.20";
    
    public final static String M_FORM_AT                        = "m-form";
    public final static String M_FORM_AT_OID                    = "1.3.6.1.4.1.18060.0.4.0.2.21";
    
    public final static String M_SUP_DIT_STRUCTURE_RULE_AT      = "m-supDITStructureRule";
    public final static String M_SUP_DIT_STRUCTURE_RULE_AT_OID  = "1.3.6.1.4.1.18060.0.4.0.2.22";

    public final static String M_OC_AT                          = "m-oc";
    public final static String M_OC_AT_OID                      = "1.3.6.1.4.1.18060.0.4.0.2.24";
    
    public final static String M_AUX_AT                         = "m-aux";
    public final static String M_AUX_AT_OID                     = "1.3.6.1.4.1.18060.0.4.0.2.26";

    public final static String M_NOT_AT                         = "m-not";
    public final static String M_NOT_AT_OID                     = "1.3.6.1.4.1.18060.0.4.0.2.27";
    
    public final static String M_APPLIES_AT                     = "m-applies";
    public final static String M_APPLIES_AT_OID                 = "1.3.6.1.4.1.18060.0.4.0.2.29";
    
    public final static String M_MATCHING_RULE_SYNTAX_AT        = "m-matchingRuleSyntax";
    public final static String M_MATCHING_RULE_SYNTAX_AT_OID    = "1.3.6.1.4.1.18060.0.4.0.2.31";

    public final static String M_FQCN_AT                        = "m-fqcn";
    public final static String M_FQCN_AT_OID                    = "1.3.6.1.4.1.18060.0.4.0.2.32";

    public final static String M_BYTECODE_AT                    = "m-bytecode";
    public final static String M_BYTECODE_AT_OID                = "1.3.6.1.4.1.18060.0.4.0.2.33";
    
    public final static String X_HUMAN_READABLE_AT              = "x-humanReadable";
    public final static String X_HUMAN_READABLE_AT_OID          = "1.3.6.1.4.1.18060.0.4.0.2.34";

    public final static String M_DISABLED_AT                    = "m-disabled";
    public final static String M_DISABLED_AT_OID                = "1.3.6.1.4.1.18060.0.4.0.2.37";

    public final static String M_DEPENDENCIES_AT                = "m-dependencies";
    public final static String M_DEPENDENCIES_AT_OID            = "1.3.6.1.4.1.18060.0.4.0.2.38";
    
    public final static String M_LENGTH_AT                      = "m-length";
    public final static String M_LENGTH_AT_OID                  = "1.3.6.1.4.1.18060.0.4.0.2.39";
    
    // -- schema extensions & values --
    public final static String X_SCHEMA                         = "X-SCHEMA";
    public final static String X_IS_HUMAN_READABLE              = "X-IS-HUMAN-READABLE";
    public final static String X_READ_ONLY                      = "X-READ-ONLY";
    public final static String X_ENABLED                        = "X-ENABLED";
}
