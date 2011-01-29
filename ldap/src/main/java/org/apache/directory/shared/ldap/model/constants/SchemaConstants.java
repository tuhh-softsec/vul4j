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
package org.apache.directory.shared.ldap.model.constants;



/**
 * A utility class where we declare all the schema objects being used by any
 * ldap server.
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class SchemaConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private SchemaConstants()
    {
    }

    // SchemaEntity names
    public final static String ATTRIBUTE_TYPE                       = "AttributeType";
    public final static String COMPARATOR                           = "Comparator";
    public final static String DIT_CONTENT_RULE                     = "DitContentRule";
    public final static String DIT_STRUCTURE_RULE                   = "DitStructureRule";
    public final static String MATCHING_RULE                        = "MatchingRule";
    public final static String MATCHING_RULE_USE                    = "MatchingRuleUse";
    public final static String NAME_FORM                            = "NameForm";
    public final static String NORMALIZER                           = "Normalizer";
    public final static String OBJECT_CLASS                         = "ObjectCLass";
    public final static String SYNTAX                               = "Syntax";
    public final static String SYNTAX_CHECKER                       = "SyntaxChecker";

    // SchemaEntity paths
    public final static String ATTRIBUTES_TYPE_PATH                 = "ou=attributetypes";
    public final static String COMPARATORS_PATH                     = "ou=comparators";
    public final static String DIT_CONTENT_RULES_PATH               = "ou=ditcontentrules";
    public final static String DIT_STRUCTURE_RULES_PATH             = "ou=ditstructurerules";
    public final static String MATCHING_RULES_PATH                  = "ou=matchingrules";
    public final static String MATCHING_RULE_USE_PATH               = "ou=matchingruleuse";
    public final static String NAME_FORMS_PATH                      = "ou=nameforms";
    public final static String NORMALIZERS_PATH                     = "ou=normalizers";
    public final static String OBJECT_CLASSES_PATH                  = "ou=objectclasses";
    public final static String SYNTAXES_PATH                        = "ou=syntaxes";
    public final static String SYNTAX_CHECKERS_PATH                 = "ou=syntaxcheckers";

    // Schema root
    public final static String OU_SCHEMA                            = "ou=schema";

    // The Dn for the schema modifications
    public final static String SCHEMA_MODIFICATIONS_DN              = "cn=schemaModifications,ou=schema";


    // Special attributes 1.1 , * and + for search operations
    public final static String NO_ATTRIBUTE                         = "1.1";
    public final static String[] NO_ATTRIBUTE_ARRAY                 = new String[]{ NO_ATTRIBUTE };

    public final static String ALL_USER_ATTRIBUTES                  = "*";
    public final static String[] ALL_USER_ATTRIBUTES_ARRAY          = new String[]{ ALL_USER_ATTRIBUTES };

    public final static String ALL_OPERATIONAL_ATTRIBUTES           = "+";
    public final static String[] ALL_OPERATIONAL_ATTRIBUTES_ARRAY   = new String[]{ ALL_OPERATIONAL_ATTRIBUTES };

    // ---- ObjectClasses -----------------------------------------------------
    // Domain
    public final static String DOMAIN_OC                            = "domain";
    public final static String DOMAIN_OC_OID                        = "0.9.2342.19200300.100.4.13";

    // PosixAccount
    public final static String POSIX_ACCOUNT_OC                     = "posicAccount";
    public final static String POSIX_ACCOUNT_OC_OID                 = "1.3.6.1.1.1.2.0";

    // PosixGroup
    public final static String POSIX_GROUP_OC                       = "posixGroup";
    public final static String POSIX_GROUP_OC_OID                   = "1.3.6.1.1.1.2.2";

    // ExtensibleObject
    public final static String EXTENSIBLE_OBJECT_OC                 = "extensibleObject";
    public final static String EXTENSIBLE_OBJECT_OC_OID             = "1.3.6.1.4.1.1466.101.120.111";

    // DcObject
    public final static String DC_OBJECT_OC                         = "dcObject";
    public final static String DC_OBJECT_OC_OID                     = "1.3.6.1.4.1.1466.344";

    // Apache Meta Schema
    // MetaTop
    public final static String META_TOP_OC                          = "metaTop";
    public final static String META_TOP_OC_OID                      = "1.3.6.1.4.1.18060.0.4.0.3.1";

    // MetaObjectClass
    public final static String META_OBJECT_CLASS_OC                 = "metaObjectClass";
    public final static String META_OBJECT_CLASS_OC_OID             = "1.3.6.1.4.1.18060.0.4.0.3.2";

    // MetaAttributeType
    public final static String META_ATTRIBUTE_TYPE_OC               = "metaAttributeType";
    public final static String META_ATTRIBUTE_TYPE_OC_OID           = "1.3.6.1.4.1.18060.0.4.0.3.3";

    // MetaSyntax
    public final static String META_SYNTAX_OC                       = "metaSyntax";
    public final static String META_SYNTAX_OC_OID                   = "1.3.6.1.4.1.18060.0.4.0.3.4";

    // MetaMatchingRule
    public final static String META_MATCHING_RULE_OC                = "metaMatchingRule";
    public final static String META_MATCHING_RULE_OC_OID            = "1.3.6.1.4.1.18060.0.4.0.3.5";

    // MetaDITStructureRule
    public final static String META_DIT_STRUCTURE_RULE_OC           = "metaDITStructureRule";
    public final static String META_DIT_STRUCTURE_RULE_OC_OID       = "1.3.6.1.4.1.18060.0.4.0.3.6";

    // MetaNameForm
    public final static String META_NAME_FORM_OC                    = "metaNameForm";
    public final static String META_NAME_FORM_OC_OID                = "1.3.6.1.4.1.18060.0.4.0.3.7";

    // MetaMatchingRuleUse
    public final static String META_MATCHING_RULE_USE_OC            = "metaMatchingRuleUse";
    public final static String META_MATCHING_RULE_USE_OC_OID        = "1.3.6.1.4.1.18060.0.4.0.3.8";

    // MetaDITContentRule
    public final static String META_DIT_CONTENT_RULE_OC             = "metaDITContentRule";
    public final static String META_DIT_CONTENT_RULE_OC_OID         = "1.3.6.1.4.1.18060.0.4.0.3.9";

    // MetaSyntaxChecker
    public final static String META_SYNTAX_CHECKER_OC               = "metaSyntaxChecker";
    public final static String META_SYNTAX_CHECKER_OC_OID           = "1.3.6.1.4.1.18060.0.4.0.3.10";

    // MetaSchema
    public final static String META_SCHEMA_OC                       = "metaSchema";
    public final static String META_SCHEMA_OC_OID                   = "1.3.6.1.4.1.18060.0.4.0.3.11";

    // MetaNormalizer
    public final static String META_NORMALIZER_OC                   = "metaNormalizer";
    public final static String META_NORMALIZER_OC_OID               = "1.3.6.1.4.1.18060.0.4.0.3.12";

    // MetaComparator
    public final static String META_COMPARATOR_OC                   = "metaComparator";
    public final static String META_COMPARATOR_OC_OID               = "1.3.6.1.4.1.18060.0.4.0.3.13";

    // Krb5Principal
    public final static String KRB5_PRINCIPAL_OC                    = "krb5Principal";
    public final static String KRB5_PRINCIPAL_OC_OID                = "1.3.6.1.4.1.5322.10.2.1";

    // Top
    public final static String TOP_OC                               = "top";
    public final static String TOP_OC_OID                           = "2.5.6.0";

    // Alias
    public final static String ALIAS_OC                             = "alias";
    public final static String ALIAS_OC_OID                         = "2.5.6.1";

    // Country
    public final static String COUNTRY_OC                           = "country";
    public final static String COUNTRY_OC_OID                       = "2.5.6.2";

    // Locality
    public final static String LOCALITY_OC                          = "locality";
    public final static String LOCALITY_OC_OID                      = "2.5.6.3";

    // Organization
    public final static String ORGANIZATION_OC                      = "organization";
    public final static String ORGANIZATION_OC_OID                  = "2.5.6.4";

    // OrganizationalUnit
    public final static String ORGANIZATIONAL_UNIT_OC               = "organizationalUnit";
    public final static String ORGANIZATIONAL_UNIT_OC_OID           = "2.5.6.5";

    // Person
    public final static String PERSON_OC                            = "person";
    public final static String PERSON_OC_OID                        = "2.5.6.6";
    // OrganizationalPerson
    public final static String ORGANIZATIONAL_PERSON_OC             = "organizationalPerson";
    public final static String ORGANIZATIONAL_PERSON_OC_OID         = "2.5.6.7";

    // OrganizationalRole
    public final static String ORGANIZATIONAL_ROLE_OC               = "organizationalRole";
    public final static String ORGANIZATIONAL_ROLE_OC_OID           = "2.5.6.8";

    // GroupOfNames
    public final static String GROUP_OF_NAMES_OC                    = "groupOfNames";
    public final static String GROUP_OF_NAMES_OC_OID                = "2.5.6.9";

    // ResidentialPerson
    public final static String RESIDENTIAL_PERSON_OC                = "residentialPerson";
    public final static String RESIDENTIAL_PERSON_OC_OID            = "2.5.6.10";

    // GroupOfUniqueNames
    public final static String GROUP_OF_UNIQUE_NAMES_OC             = "groupOfUniqueNames";
    public final static String GROUP_OF_UNIQUE_NAMES_OC_OID         = "2.5.6.17";

    // Subentry
    public final static String SUBENTRY_OC                          = "subentry";
    public final static String SUBENTRY_OC_OID                      = "2.5.17.0";

    // AccessControlSubentry
    public final static String ACCESS_CONTROL_SUBENTRY_OC           = "accessControlSubentry";
    public final static String ACCESS_CONTROL_SUBENTRY_OC_OID       = "2.5.17.1";

    // CollectiveAttributeSubentry
    public final static String COLLECTIVE_ATTRIBUTE_SUBENTRY_OC     = "collectiveAttributeSubentry";
    public final static String COLLECTIVE_ATTRIBUTE_SUBENTRY_OC_OID = "2.5.17.2";

    // Subschema
    public final static String SUBSCHEMA_OC                         = "subschema";
    public final static String SUBSCHEMA_OC_OID                     = "2.5.20.1";

    // InetOrgPerson
    public final static String INET_ORG_PERSON_OC                   = "inetOrgPerson";
    public final static String INET_ORG_PERSON_OC_OID               = "2.16.840.1.113730.3.2.2";

    // Referral
    public final static String REFERRAL_OC                          = "referral";
    public final static String REFERRAL_OC_OID                      = "2.16.840.1.113730.3.2.6";


    // ---- AttributeTypes ----------------------------------------------------
    // Uid
    public final static String UID_AT                                   = "uid";
    public final static String USER_ID_AT                               = "userid";
    public final static String UID_AT_OID                               = "0.9.2342.19200300.100.1.1";

    // DomainComponent
    public final static String DC_AT = "dc";
    public final static String DOMAIN_COMPONENT_AT                      = "domainComponent";
    public final static String DOMAIN_COMPONENT_AT_OID                  = "0.9.2342.19200300.100.1.25";

    // UidObject
    public final static String UID_OBJECT_AT                            = "uidObject";
    public final static String UID_OBJECT_AT_OID                        = "1.3.6.1.1.3.1";

    // VendorName
    public final static String VENDOR_NAME_AT                           = "vendorName";
    public final static String VENDOR_NAME_AT_OID                       = "1.3.6.1.1.4";

    // VendorVersion
    public final static String VENDOR_VERSION_AT                        = "vendorVersion";
    public final static String VENDOR_VERSION_AT_OID                    = "1.3.6.1.1.5";

    // entryUUID
    public final static String ENTRY_UUID_AT                            = "entryUUID";
    public final static String ENTRY_UUID_AT_OID                        = "1.3.6.1.1.16.4";

    // entryDN
    public final static String ENTRY_DN_AT                              = "entryDN";
    public final static String ENTRY_DN_AT_OID                          = "1.3.6.1.1.20";

    // NamingContexts
    public final static String NAMING_CONTEXTS_AT                       = "namingContexts";
    public final static String NAMING_CONTEXTS_AT_OID                   = "1.3.6.1.4.1.1466.101.120.5";

    // SupportedExtension
    public final static String SUPPORTED_EXTENSION_AT                   = "supportedExtension";
    public final static String SUPPORTED_EXTENSION_AT_OID               = "1.3.6.1.4.1.1466.101.120.7";

    // supportedControl
    public final static String SUPPORTED_CONTROL_AT                     = "supportedControl";
    public final static String SUPPORTED_CONTROL_AT_OID                 = "1.3.6.1.4.1.1466.101.120.13";

    // supportedSASLMechanisms
    public final static String SUPPORTED_SASL_MECHANISMS_AT             = "supportedSASLMechanisms";
    public final static String SUPPORTED_SASL_MECHANISMS_AT_OID         = "1.3.6.1.4.1.1466.101.120.14";

    // SupportedLdapVersion
    public final static String SUPPORTED_LDAP_VERSION_AT                = "supportedLDAPVersion";
    public final static String SUPPORTED_LDAP_VERSION_AT_OID            = "1.3.6.1.4.1.1466.101.120.15";

    // LdapSyntaxes
    public final static String LDAP_SYNTAXES_AT                         = "ldapSyntaxes";
    public final static String LDAP_SYNTAXES_AT_OID                     = "1.3.6.1.4.1.1466.101.120.16";

    // SupportedFeatures
    public final static String SUPPORTED_FEATURES_AT                    = "supportedFeatures";
    public final static String SUPPORTED_FEATURES_AT_OID                = "1.3.6.1.4.1.4203.1.3.5";

    // entryCSN
    public final static String ENTRY_CSN_AT                             = "entryCSN";
    public final static String ENTRY_CSN_AT_OID                         = "1.3.6.1.4.1.4203.666.1.7";

    // contextCSN
    public final static String CONTEXT_CSN_AT                           = "contextCSN";
    public final static String CONTEXT_CSN_AT_OID                       = "1.3.6.1.4.1.4203.666.1.25";

    // AccessControlSubentries
    public final static String ACCESS_CONTROL_SUBENTRIES_AT             = "accessControlSubentries";
    public final static String ACCESS_CONTROL_SUBENTRIES_AT_OID         = "1.3.6.1.4.1.18060.0.4.1.2.11";

    // TriggerExecutionSubentries
    public final static String TRIGGER_EXECUTION_SUBENTRIES_AT          = "triggerExecutionSubentries";
    public final static String TRIGGER_EXECUTION_SUBENTRIES_AT_OID      = "1.3.6.1.4.1.18060.0.4.1.2.27";

    // Comparators
    public final static String COMPARATORS_AT                           = "comparators";
    public final static String COMPARATORS_AT_OID                       = "1.3.6.1.4.1.18060.0.4.1.2.32";

    // Normalizers
    public final static String NORMALIZERS_AT                           = "normalizers";
    public final static String NORMALIZERS_AT_OID                       = "1.3.6.1.4.1.18060.0.4.1.2.33";

    // SyntaxCheckers
    public final static String SYNTAX_CHECKERS_AT                       = "syntaxCheckers";
    public final static String SYNTAX_CHECKERS_AT_OID                   = "1.3.6.1.4.1.18060.0.4.1.2.34";

    // ChangeLogContext
    public final static String CHANGELOG_CONTEXT_AT                     = "changeLogContext";
    public final static String CHANGELOG_CONTEXT_AT_OID                 = "1.3.6.1.4.1.18060.0.4.1.2.49";

    // ObjectClass
    public final static String OBJECT_CLASS_AT                          = "objectClass";
    public final static String OBJECT_CLASS_AT_OID                      = "2.5.4.0";

    // AliasedObjectName
    public final static String ALIASED_OBJECT_NAME_AT                   = "aliasedObjectName";
    public final static String ALIASED_OBJECT_NAME_AT_OID               = "2.5.4.1";

    // Cn
    public final static String CN_AT                                    = "cn";
    public final static String COMMON_NAME_AT                           = "commonName";
    public final static String CN_AT_OID                                = "2.5.4.3";

    // Sn
    public final static String SN_AT                                    = "sn";
    public final static String SURNAME_AT                               = "surname";
    public final static String SN_AT_OID                                = "2.5.4.4";

    // St
    public final static String ST_AT = "st";
    public final static String STATEORPROVINCE_NAME_AT                  = "stateOrProvinceName";
    public final static String ST_AT_OID                                = "2.5.4.8";

    // Street
    public final static String STREET_AT                                = "street";
    public final static String STREET_ADDRESS_AT                        = "streetAddress";
    public final static String STREET_AT_OID                            = "2.5.4.9";

    // O
    public final static String O_AT                                     = "o";
    public final static String ORGANIZATION_NAME_AT                     = "organizationName";
    public final static String O_AT_OID                                 = "2.5.4.10";

    // Ou
    public final static String OU_AT = "ou";
    public final static String ORGANIZATIONAL_UNIT_NAME_AT              = "organizationalUnitName";
    public final static String OU_AT_OID                                = "2.5.4.11";

    // SearchGuide
    public final static String SEARCHGUIDE_AT                           = "searchguide";
    public final static String SEARCHGUIDE_AT_OID                       = "2.5.4.14";


    // PostalCode
    public final static String POSTALCODE_AT                            = "postalCode";
    public final static String POSTALCODE_AT_OID                        = "2.5.4.17";

    // PostalCode
    public final static String C_POSTALCODE_AT                          = "c-postalCode";
    public final static String C_POSTALCODE_AT_OID                      = "2.5.4.17.1";

    // PostOfficeBox
    public final static String POSTOFFICEBOX_AT                         = "postOfficeBox";
    public final static String POSTOFFICEBOX_AT_OID                     = "2.5.4.18";
    // Member
    public final static String MEMBER_AT                                = "member";
    public final static String MEMBER_AT_OID                            = "2.5.4.31";

    // UserPassword
    public final static String USER_PASSWORD_AT                         = "userPassword";
    public final static String USER_PASSWORD_AT_OID                     = "2.5.4.35";

    // Name
    public final static String NAME_AT                                  = "name";
    public final static String NAME_AT_OID                              = "2.5.4.41";

    // UniqueMember
    public final static String UNIQUE_MEMBER_AT                         = "uniqueMember";
    public final static String UNIQUE_MEMBER_AT_OID                     = "2.5.4.50";

    // ExcludeAllColectiveAttributes
    public final static String EXCLUDE_ALL_COLLECTIVE_ATTRIBUTES_AT     = "excludeAllCollectiveAttributes";
    public final static String EXCLUDE_ALL_COLLECTIVE_ATTRIBUTES_AT_OID = "2.5.18.0";

        // CreateTimestamp
    public final static String CREATE_TIMESTAMP_AT                      = "createTimestamp";
    public final static String CREATE_TIMESTAMP_AT_OID                  = "2.5.18.1";

    // ModifyTimestamp
    public final static String MODIFY_TIMESTAMP_AT                      = "modifyTimestamp";
    public final static String MODIFY_TIMESTAMP_AT_OID                  = "2.5.18.2";

    // CreatorsName
    public final static String CREATORS_NAME_AT                         = "creatorsName";
    public final static String CREATORS_NAME_AT_OID                     = "2.5.18.3";

    // ModifiersName
    public final static String MODIFIERS_NAME_AT                        = "modifiersName";
    public final static String MODIFIERS_NAME_AT_OID                    = "2.5.18.4";

    // AdministrativeRole
    public final static String ADMINISTRATIVE_ROLE_AT                   = "administrativeRole";
    public final static String ADMINISTRATIVE_ROLE_AT_OID               = "2.5.18.5";

    // SubtreeSpecification
    public final static String SUBTREE_SPECIFICATION_AT                 = "subtreeSpecification";
    public final static String SUBTREE_SPECIFICATION_AT_OID             = "2.5.18.6";

    // CollectiveExclusions
    public final static String COLLECTIVE_EXCLUSIONS_AT                 = "collectiveExclusions";
    public final static String COLLECTIVE_EXCLUSIONS_AT_OID             = "2.5.18.7";

    // hasSubordinates
    public final static String HAS_SUBORDINATES_AT                      = "hasSubordinates";
    public final static String HAS_SUBORDINATES_AT_OID                  = "2.5.18.9";

    // SubschemaSubentry
    public final static String SUBSCHEMA_SUBENTRY_AT                    = "subschemaSubentry";
    public final static String SUBSCHEMA_SUBENTRY_AT_OID                = "2.5.18.10";

    // CollectiveAttributeSubentries
    public final static String COLLECTIVE_ATTRIBUTE_SUBENTRIES_AT       = "collectiveAttributeSubentries";
    public final static String COLLECTIVE_ATTRIBUTE_SUBENTRIES_AT_OID   = "2.5.18.12";

    // DitStructureRules
    public final static String DIT_STRUCTURE_RULES_AT                   = "ditStructureRules";
    public final static String DIT_STRUCTURE_RULES_AT_OID               = "2.5.21.1";

    // DitContentRules
    public final static String DIT_CONTENT_RULES_AT                     = "ditContentRules";
    public final static String DIT_CONTENT_RULES_AT_OID                 = "2.5.21.2";

    // MatchingRules
    public final static String MATCHING_RULES_AT                        = "matchingRules";
    public final static String MATCHING_RULES_AT_OID                    = "2.5.21.4";

    // AttributeTypes
    public final static String ATTRIBUTE_TYPES_AT                       = "attributeTypes";
    public final static String ATTRIBUTE_TYPES_AT_OID                   = "2.5.21.5";

    // ObjectClasses
    public final static String OBJECT_CLASSES_AT                        = "objectClasses";
    public final static String OBJECT_CLASSES_AT_OID                    = "2.5.21.6";

    // NameForms
    public final static String NAME_FORMS_AT                            = "nameForms";
    public final static String NAME_FORMS_AT_OID                        = "2.5.21.7";

    // MatchingRuleUse
    public final static String MATCHING_RULE_USE_AT                     = "matchingRuleUse";
    public final static String MATCHING_RULE_USE_AT_OID                 = "2.5.21.8";

    // StructuralObjectClass
    public final static String STRUCTURAL_OBJECT_CLASS_AT               = "structuralObjectClass";
    public final static String STRUCTURAL_OBJECT_CLASS_AT_OID           = "2.5.21.9";

    // governingStructureRule
    public final static String GOVERNING_STRUCTURE_RULE_AT              = "governingStructureRule";
    public final static String GOVERNING_STRUCTURE_RULE_AT_OID          = "2.5.21.10";

    // AccessControlScheme
    public final static String ACCESS_CONTROL_SCHEME_AT                 = "accessControlScheme";
    public final static String ACCESS_CONTROL_SCHEME_OID                = "2.5.24.1";

    // PrescriptiveACI
    public final static String PRESCRIPTIVE_ACI_AT                      = "prescriptiveACI";
    public final static String PRESCRIPTIVE_ACI_AT_OID                  = "2.5.24.4";

    // EntryACI
    public final static String ENTRY_ACI_AT                             = "entryACI";
    public final static String ENTRY_ACI_AT_OID                         = "2.5.24.5";

    // SubentryACI
    public final static String SUBENTRY_ACI_AT                          = "subentryACI";
    public final static String SUBENTRY_ACI_AT_OID                      = "2.5.24.6";

    // Ref
    public final static String REF_AT                                   = "ref";
    public final static String REF_AT_OID                               = "2.16.840.1.113730.3.1.34";

    // DisplayName
    public final static String DISPLAY_NAME_AT                          = "displayName";
    public final static String DISPLAY_NAME_AT_OID                      = "2.16.840.1.113730.3.1.241";

    // numSubordinates, by Sun
    public final static String NUM_SUBORDINATES_AT                      = "numSubordinates";
    // no official OID in RFCs

    // subordinateCount, by Novell
    public final static String SUBORDINATE_COUNT_AT                     = "subordinateCount";
    // no official OID in RFCs

    //-------------------------------------------------------------------------
    // ---- Syntaxes ----------------------------------------------------------
    //-------------------------------------------------------------------------
    public final static String NAME_OR_NUMERIC_ID_SYNTAX                      = "1.3.6.1.4.1.18060.0.4.0.0.0";

    public final static String OBJECT_CLASS_TYPE_SYNTAX                       = "1.3.6.1.4.1.18060.0.4.0.0.1";

    public final static String NUMERIC_OID_SYNTAX                             = "1.3.6.1.4.1.18060.0.4.0.0.2";

    public final static String ATTRIBUTE_TYPE_USAGE_SYNTAX                    = "1.3.6.1.4.1.18060.0.4.0.0.3";

    // RFC 4517, par. 3.3.23
    public final static String NUMBER_SYNTAX                                  = "1.3.6.1.4.1.18060.0.4.0.0.4";

    public final static String OID_LEN_SYNTAX                                 = "1.3.6.1.4.1.18060.0.4.0.0.5";

    public final static String OBJECT_NAME_SYNTAX                             = "1.3.6.1.4.1.18060.0.4.0.0.6";

    // RFC 2252, removed in RFC 4517
    public final static String ACI_ITEM_SYNTAX                                = "1.3.6.1.4.1.1466.115.121.1.1";

    // RFC 2252, removed in RFC 4517
    public final static String ACCESS_POINT_SYNTAX                            = "1.3.6.1.4.1.1466.115.121.1.2";

    // RFC 4517, chap 3.3.1
    public final static String ATTRIBUTE_TYPE_DESCRIPTION_SYNTAX              = "1.3.6.1.4.1.1466.115.121.1.3";

    // RFC 2252, removed in RFC 4517
    public final static String AUDIO_SYNTAX                                   = "1.3.6.1.4.1.1466.115.121.1.4";

    // RFC 2252, removed in RFC 4517
    public final static String BINARY_SYNTAX                                  = "1.3.6.1.4.1.1466.115.121.1.5";

    // RFC 4517, chap 3.3.2
    public final static String BIT_STRING_SYNTAX                              = "1.3.6.1.4.1.1466.115.121.1.6";

    // RFC 4517, chap 3.3.3
    public final static String BOOLEAN_SYNTAX                                 = "1.3.6.1.4.1.1466.115.121.1.7";

    // RFC 2252, removed in RFC 4517, reintroduced in RFC 4523, chap. 2.1
    public final static String CERTIFICATE_SYNTAX                             = "1.3.6.1.4.1.1466.115.121.1.8";

    // RFC 2252, removed in RFC 4517, reintroduced in RFC 4523, chap. 2.2
    public final static String CERTIFICATE_LIST_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.9";

    // RFC 2252, removed in RFC 4517, reintroduced in RFC 4523, chap. 2.3
    public final static String CERTIFICATE_PAIR_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.10";

    // RFC 4517, chap 3.3.4
    public final static String COUNTRY_STRING_SYNTAX                          = "1.3.6.1.4.1.1466.115.121.1.11";

    // RFC 4517, chap 3.3.9
    public final static String DN_SYNTAX                                      = "1.3.6.1.4.1.1466.115.121.1.12";

    // RFC 2252, removed in RFC 4517
    public final static String DATA_QUALITY_SYNTAX                            = "1.3.6.1.4.1.1466.115.121.1.13";

    // RFC 4517, chap 3.3.5
    public final static String DELIVERY_METHOD_SYNTAX                         = "1.3.6.1.4.1.1466.115.121.1.14";

    // RFC 4517, chap 3.3.6
    public final static String DIRECTORY_STRING_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.15";

    // RFC 4517, chap 3.3.7
    public final static String DIT_CONTENT_RULE_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.16";

    // RFC 4517, chap 3.3.8
    public final static String DIT_STRUCTURE_RULE_SYNTAX                      = "1.3.6.1.4.1.1466.115.121.1.17";

    // RFC 2252, removed in RFC 4517
    public final static String DL_SUBMIT_PERMISSION_SYNTAX                    = "1.3.6.1.4.1.1466.115.121.1.18";

    // RFC 2252, removed in RFC 4517
    public final static String DSA_QUALITY_SYNTAX                             = "1.3.6.1.4.1.1466.115.121.1.19";

    // RFC 2252, removed in RFC 4517
    public final static String DSE_TYPE_SYNTAX                                = "1.3.6.1.4.1.1466.115.121.1.20";

    // RFC 4517, chap 3.3.10
    public final static String ENHANCED_GUIDE_SYNTAX                          = "1.3.6.1.4.1.1466.115.121.1.21";

    // RFC 4517, chap 3.3.11
    public final static String FACSIMILE_TELEPHONE_NUMBER_SYNTAX              = "1.3.6.1.4.1.1466.115.121.1.22";

    // RFC 4517, chap 3.3.12
    public final static String FAX_SYNTAX                                     = "1.3.6.1.4.1.1466.115.121.1.23";

    // RFC 4517, chap 3.3.13
    public final static String GENERALIZED_TIME_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.24";

    // RFC 4517, chap 3.3.14
    public final static String GUIDE_SYNTAX                                   = "1.3.6.1.4.1.1466.115.121.1.25";

    // RFC 4517, chap 3.3.15
    public final static String IA5_STRING_SYNTAX                              = "1.3.6.1.4.1.1466.115.121.1.26";

    // RFC 4517, chap 3.3.16
    public final static String INTEGER_SYNTAX                                 = "1.3.6.1.4.1.1466.115.121.1.27";

    // RFC 4517, chap 3.3.17
    public final static String JPEG_SYNTAX                                    = "1.3.6.1.4.1.1466.115.121.1.28";

    // RFC 2252, removed in RFC 4517
    public final static String MASTER_AND_SHADOW_ACCESS_POINTS_SYNTAX         = "1.3.6.1.4.1.1466.115.121.1.29";

    // RFC 4517, chap 3.3.19
    public final static String MATCHING_RULE_DESCRIPTION_SYNTAX               = "1.3.6.1.4.1.1466.115.121.1.30";

    // RFC 4517, chap 3.3.20
    public final static String MATCHING_RULE_USE_DESCRIPTION_SYNTAX           = "1.3.6.1.4.1.1466.115.121.1.31";

    // RFC 2252, removed in RFC 4517
    public final static String MAIL_PREFERENCE_SYNTAX                         = "1.3.6.1.4.1.1466.115.121.1.32";

    // RFC 2252, removed in RFC 4517
    public final static String MHS_OR_ADDRESS_SYNTAX                          = "1.3.6.1.4.1.1466.115.121.1.33";

    // RFC 4517, chap 3.3.21
    public final static String NAME_AND_OPTIONAL_UID_SYNTAX                   = "1.3.6.1.4.1.1466.115.121.1.34";

    // RFC 4517, chap 3.3.22
    public final static String NAME_FORM_DESCRIPTION_SYNTAX                   = "1.3.6.1.4.1.1466.115.121.1.35";

    // RFC 4517, chap 3.3.23
    public final static String NUMERIC_STRING_SYNTAX                          = "1.3.6.1.4.1.1466.115.121.1.36";

    // RFC 4517, chap 3.3.24
    public final static String OBJECT_CLASS_DESCRIPTION_SYNTAX                = "1.3.6.1.4.1.1466.115.121.1.37";

    // RFC 4517, chap 3.3.26
    public final static String OID_SYNTAX                                     = "1.3.6.1.4.1.1466.115.121.1.38";

    // RFC 4517, chap 3.3.27
    public final static String OTHER_MAILBOX_SYNTAX                           = "1.3.6.1.4.1.1466.115.121.1.39";

    // RFC 4517, chap 3.3.25
    public final static String OCTET_STRING_SYNTAX                            = "1.3.6.1.4.1.1466.115.121.1.40";

    // RFC 4517, chap 3.3.28
    public final static String POSTAL_ADDRESS_SYNTAX                          = "1.3.6.1.4.1.1466.115.121.1.41";

    // RFC 2252, removed in RFC 4517
    public final static String PROTOCOL_INFORMATION_SYNTAX                    = "1.3.6.1.4.1.1466.115.121.1.42";

    // RFC 2252, removed in RFC 4517
    public final static String PRESENTATION_ADDRESS_SYNTAX                    = "1.3.6.1.4.1.1466.115.121.1.43";

    // RFC 4517, chap 3.3.29
    public final static String PRINTABLE_STRING_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.44";

    // RFC 2252, removed in RFC 4517
    public final static String SUBTREE_SPECIFICATION_SYNTAX                   = "1.3.6.1.4.1.1466.115.121.1.45";

    // RFC 2252, removed in RFC 4517
    public final static String SUPPLIER_INFORMATION_SYNTAX                    = "1.3.6.1.4.1.1466.115.121.1.46";

    // RFC 2252, removed in RFC 4517
    public final static String SUPPLIER_OR_CONSUMER_SYNTAX                    = "1.3.6.1.4.1.1466.115.121.1.47";

    // RFC 2252, removed in RFC 4517
    public final static String SUPPLIER_AND_CONSUMER_SYNTAX                   = "1.3.6.1.4.1.1466.115.121.1.48";

    // RFC 2252, removed in RFC 4517, reintroduced in RFC 4523, chap. 2.4
    public final static String SUPPORTED_ALGORITHM_SYNTAX                     = "1.3.6.1.4.1.1466.115.121.1.49";

    // RFC 4517, chap 3.3.31
    public final static String TELEPHONE_NUMBER_SYNTAX                        = "1.3.6.1.4.1.1466.115.121.1.50";

    // RFC 4517, chap 3.3.32
    public final static String TELETEX_TERMINAL_IDENTIFIER_SYNTAX             = "1.3.6.1.4.1.1466.115.121.1.51";

    // RFC 4517, chap 3.3.33
    public final static String TELEX_NUMBER_SYNTAX                            = "1.3.6.1.4.1.1466.115.121.1.52";

    // RFC 4517, chap 3.3.34
    public final static String UTC_TIME_SYNTAX                                = "1.3.6.1.4.1.1466.115.121.1.53";

    // RFC 4517, chap 3.3.18
    public final static String LDAP_SYNTAX_DESCRIPTION_SYNTAX                 = "1.3.6.1.4.1.1466.115.121.1.54";

    // RFC 2252, removed in RFC 4517
    public final static String MODIFY_RIGHTS_SYNTAX                           = "1.3.6.1.4.1.1466.115.121.1.55";

    // RFC 2252, removed in RFC 4517
    public final static String LDAP_SCHEMA_DEFINITION_SYNTAX                  = "1.3.6.1.4.1.1466.115.121.1.56";

    // RFC 2252, removed in RFC 4517
    public final static String LDAP_SCHEMA_DESCRIPTION_SYNTAX                 = "1.3.6.1.4.1.1466.115.121.1.57";

    // RFC 4517, chap 3.3.30
    public final static String SUBSTRING_ASSERTION_SYNTAX                     = "1.3.6.1.4.1.1466.115.121.1.58";

    // From draft-ietf-pkix-ldap-v3-01.txt. Obsolete.
    public final static String ATTRIBUTE_CERTIFICATE_ASSERTION_SYNTAX         = "1.3.6.1.4.1.1466.115.121.1.59";

    //From RFC 4530, chap. 2.1
    public final static String UUID_SYNTAX                                    = "1.3.6.1.1.16.1";

    // From http://www.openldap.org/faq/data/cache/1145.html
    public final static String CSN_SYNTAX                                     = "1.3.6.1.4.1.4203.666.11.2.1";

    // From http://www.openldap.org/faq/data/cache/1145.html
    public final static String CSN_SID_SYNTAX                                 = "1.3.6.1.4.1.4203.666.11.2.4";

    // Apache DS
    public final static String JAVA_BYTE_SYNTAX                               = "1.3.6.1.4.1.18060.0.4.1.0.0";
    public final static String JAVA_CHAR_SYNTAX                               = "1.3.6.1.4.1.18060.0.4.1.0.1";
    public final static String JAVA_SHORT_SYNTAX                              = "1.3.6.1.4.1.18060.0.4.1.0.2";
    public final static String JAVA_LONG_SYNTAX                               = "1.3.6.1.4.1.18060.0.4.1.0.3";
    public final static String JAVA_INT_SYNTAX                                = "1.3.6.1.4.1.18060.0.4.1.0.4";

    // Comparator syntax
    public final static String COMPARATOR_SYNTAX                              = "1.3.6.1.4.1.18060.0.4.1.0.5";

    // Normalizer Syntax
    public final static String NORMALIZER_SYNTAX                              = "1.3.6.1.4.1.18060.0.4.1.0.6";

    // SyntaxChecker Syntax
    public final static String SYNTAX_CHECKER_SYNTAX                          = "1.3.6.1.4.1.18060.0.4.1.0.7";

    //-------------------------------------------------------------------------
    // ---- MatchingRules -----------------------------------------------------
    //-------------------------------------------------------------------------
    // caseExactIA5Match (RFC 4517, chap. 4.2.3)
    public final static String CASE_EXACT_IA5_MATCH_MR                        = "caseExactIA5Match";
    public final static String CASE_EXACT_IA5_MATCH_MR_OID                    = "1.3.6.1.4.1.1466.109.114.1";

    // caseIgnoreIA5Match (RFC 4517, chap. 4.2.7)
    public final static String CASE_IGNORE_IA5_MATCH_MR                       = "caseIgnoreIA5Match";
    public final static String CASE_IGNORE_IA5_MATCH_MR_OID                   = "1.3.6.1.4.1.1466.109.114.2";

    // caseIgnoreIA5SubstringsMatch (RFC 4517, chap. 4.2.8)
    public final static String CASE_IGNORE_IA5_SUBSTRINGS_MATCH_MR            = "caseIgnoreIA5SubstringsMatch";
    public final static String CASE_IGNORE_IA5_SUBSTRINGS_MATCH_MR_OID        = "1.3.6.1.4.1.1466.109.114.3";

    // objectIdentifierMatch (RFC 4517, chap. 4.2.26)
    public final static String OBJECT_IDENTIFIER_MATCH_MR                     = "objectIdentifierMatch";
    public final static String OBJECT_IDENTIFIER_MATCH_MR_OID                 = "2.5.13.0";

    // distinguishedNameMatch (RFC 4517, chap. 4.2.15)
    public final static String DISTINGUISHED_NAME_MATCH_MR                    = "distinguishedNameMatch";
    public final static String DISTINGUISHED_NAME_MATCH_MR_OID                = "2.5.13.1";

    // caseIgnoreMatch (RFC 4517, chap. 3.3.19)
    public final static String CASE_IGNORE_MATCH_MR                           = "caseIgnoreMatch";
    public final static String CASE_IGNORE_MATCH_MR_OID                       = "2.5.13.2";

    // caseIgnoreOrderingMatch (RFC 4517, chap. 4.2.12)
    public final static String CASE_IGNORE_ORDERING_MATCH_MR                  = "caseIgnoreOrderingMatch";
    public final static String CASE_IGNORE_ORDERING_MATCH_MR_OID              = "2.5.13.3";

    // caseIgnoreSubstringsMatch (RFC 4517, chap. 4.2.13)
    public final static String CASE_IGNORE_SUBSTRING_MATCH_MR                 = "caseIgnoreSubstringsMatch";
    public final static String CASE_IGNORE_SUBSTRING_MATCH_MR_OID             = "2.5.13.4";

    // caseExactMatch (RFC 4517, chap. 4.2.4)
    public final static String CASE_EXACT_MATCH_MR                            = "caseExactMatch";
    public final static String CASE_EXACT_MATCH_MR_OID                        = "2.5.13.5";

    // caseExactOrderingMatch (RFC 4517, chap. 4.2.5)
    public final static String CASE_EXACT_ORDERING_MATCH_MR                   = "caseExactOrderingMatch";
    public final static String CASE_EXACT_ORDERING_MATCH_MR_OID               = "2.5.13.6";

    // caseExactSubstringsMatch (RFC 4517, chap. 4.2.6)
    public final static String CASE_EXACT_SUBSTRING_MATCH_MR                  = "caseExactSubstringsMatch";
    public final static String CASE_EXACT_SUBSTRING_MATCH_MR_OID              = "2.5.13.7";

    // numericStringMatch (RFC 4517, chap. 4.2.22)
    public final static String NUMERIC_STRING_MATCH_MR                        = "numericStringMatch";
    public final static String NUMERIC_STRING_MATCH_MR_OID                    = "2.5.13.8";

    // numericStringOrderingMatch (RFC 4517, chap. 4.2.23)
    public final static String NUMERIC_STRING_ORDERING_MATCH_MR               = "numericStringOrderingMatch";
    public final static String NUMERIC_STRING_ORDERING_MATCH_MR_OID           = "2.5.13.9";

    // numericStringSubstringsMatch (RFC 4517, chap. 4.2.24)
    public final static String NUMERIC_STRING_SUBSTRINGS_MATCH_MR             = "numericStringSubstringsMatch";
    public final static String NUMERIC_STRING_SUBSTRINGS_MATCH_MR_OID         = "2.5.13.10";

    // caseIgnoreListMatch (RFC 4517, chap. 4.2.9)
    public final static String CASE_IGNORE_LIST_MATCH_MR                      = "caseIgnoreListMatch";
    public final static String CASE_IGNORE_LIST_MATCH_MR_OID                  = "2.5.13.11";

    // caseIgnoreListSubstringsMatch (RFC 4517, chap. 4.2.10)
    public final static String CASE_IGNORE_LIST_SUBSTRINGS_MATCH_MR           = "caseIgnoreListSubstringsMatch";
    public final static String CASE_IGNORE_LIST_SUBSTRINGS_MATCH_MR_OID       = "2.5.13.12";

    // booleanMatch (RFC 4517, chap. 4.2.2)
    public final static String BOOLEAN_MATCH_MR                               = "booleanMatch";
    public final static String BOOLEAN_MATCH_MR_OID                           = "2.5.13.13";

    // integerMatch (RFC 4517, chap. 4.2.19)
    public final static String INTEGER_MATCH_MR                               = "integerMatch";
    public final static String INTEGER_MATCH_MR_OID                           = "2.5.13.14";

    // integerOrderingMatch (RFC 4517, chap. 4.2.20)
    public final static String INTEGER_ORDERING_MATCH_MR                      = "integerOrderingMatch";
    public final static String INTEGER_ORDERING_MATCH_MR_OID                  = "2.5.13.15";

    // bitStringMatch (RFC 4517, chap. 4.2.1)
    public final static String BIT_STRING_MATCH_MR                            = "bitStringMatch";
    public final static String BIT_STRING_MATCH_MR_OID                        = "2.5.13.16";

    // octetStringMatch (RFC 4517, chap. 4.2.27)
    public final static String OCTET_STRING_MATCH_MR                          = "octetStringMatch";
    public final static String OCTET_STRING_MATCH_MR_OID                      = "2.5.13.17";

    // octetStringMatch (RFC 4517, chap. 4.2.28)
    public final static String OCTET_STRING_ORDERING_MATCH_MR                 = "octetStringOrderingMatch";
    public final static String OCTET_STRING_ORDERING_MATCH_MR_OID             = "2.5.13.18";

    // octetStringSubstringsMatch
    public final static String OCTET_STRING_SUBSTRINGS_MATCH_MR               = "octetStringSubstringsMatch";
    public final static String OCTET_STRING_SUBSTRINGS_MATCH_MR_OID           = "2.5.13.19";

    // telephoneNumberMatch (RFC 4517, chap. 4.2.29)
    public final static String TELEPHONE_NUMBER_MATCH_MR                      = "telephoneNumberMatch";
    public final static String TELEPHONE_NUMBER_MATCH_MR_OID                  = "2.5.13.20";

    // telephoneNumberMatch (RFC 4517, chap. 4.2.30)
    public final static String TELEPHONE_NUMBER_SUBSTRINGS_MATCH_MR           = "telephoneNumberSubstringsMatch";
    public final static String TELEPHONE_NUMBER_SUBSTRINGS_MATCH_MR_OID       = "2.5.13.21";

    // presentationAddressMatch Removed in RFC 4517
    public final static String PRESENTATION_ADDRESS_MATCH_MATCH_MR            = "presentationAddressMatch";
    public final static String PRESENTATION_ADDRESS_MATCH_MATCH_MR_OID        = "2.5.13.22";

    // uniqueMemberMatch (RFC 4517, chap. 4.2.31)
    public final static String UNIQUE_MEMBER_MATCH_MR                         = "uniqueMemberMatch";
    public final static String UNIQUE_MEMBER_MATCH_MR_OID                     = "2.5.13.23";

    // protocolInformationMatch Removed in RFC 4517
    public final static String PROTOCOL_INFORMATION_MATCH_MR                  = "protocolInformationMatch";
    public final static String PROTOCOL_INFORMATION_MATCH_MR_OID              = "2.5.13.24";

    // "2.5.13.25" is not used ...
    // "2.5.13.26" is not used ...

    // generalizedTimeMatch (RFC 4517, chap. 4.2.16)
    public final static String GENERALIZED_TIME_MATCH_MR                      = "generalizedTimeMatch";
    public final static String GENERALIZED_TIME_MATCH_MR_OID                  = "2.5.13.27";

    // generalizedTimeOrderingMatch (RFC 4517, chap. 4.2.17)
    public final static String GENERALIZED_TIME_ORDERING_MATCH_MR             = "generalizedTimeOrderingMatch";
    public final static String GENERALIZED_TIME_ORDERING_MATCH_MR_OID         = "2.5.13.28";

    // integerFirstComponentMatch (RFC 4517, chap. 4.2.18)
    public final static String INTEGER_FIRST_COMPONENT_MATCH_MR               = "integerFirstComponentMatch";
    public final static String INTEGER_FIRST_COMPONENT_MATCH_MR_OID           = "2.5.13.29";

    // objectIdentifierFirstComponentMatch (RFC 4517, chap. 4.2.25)
    public final static String OBJECT_IDENTIFIER_FIRST_COMPONENT_MATCH_MR     = "objectIdentifierFirstComponentMatch";
    public final static String OBJECT_IDENTIFIER_FIRST_COMPONENT_MATCH_MR_OID = "2.5.13.30";

    // directoryStringFirstComponentMatch (RFC 4517, chap. 4.2.14)
    public final static String DIRECTORY_STRING_FIRST_COMPONENT_MATCH_MR      = "directoryStringFirstComponentMatch";
    public final static String DIRECTORY_STRING_FIRST_COMPONENT_MATCH_MR_OID  = "2.5.13.31";

    // wordMatch (RFC 4517, chap. 4.2.32)
    public final static String WORD_MATCH_MR                                  = "wordMatch";
    public final static String WORD_MATCH_MR_OID                              = "2.5.13.32";

    // keywordMatch (RFC 4517, chap. 4.2.21)
    public final static String KEYWORD_MATCH_MR                               = "keywordMatch";
    public final static String KEYWORD_MATCH_MR_OID                           = "2.5.13.33";

    // uuidMatch
    public final static String UUID_MATCH_MR                                  = "uuidMatch";
    public final static String UUID_MATCH_MR_OID                              = "1.3.6.1.1.16.2";

    // uuidOrderingMatch
    public final static String UUID_ORDERING_MATCH_MR                         = "uuidOrderingMatch";
    public final static String UUID_ORDERING_MATCH_MR_OID                     = "1.3.6.1.1.16.3";

    // csnMatch
    public final static String CSN_MATCH_MR                                   = "csnMatch";
    public final static String CSN_MATCH_MR_OID                               = "1.3.6.1.4.1.4203.666.11.2.2";

    // csnOrderingMatch
    public final static String CSN_ORDERING_MATCH_MR                          = "csnOrderingMatch";
    public final static String CSN_ORDERING_MATCH_MR_OID                      = "1.3.6.1.4.1.4203.666.11.2.3";

    // csnSidMatch
    public final static String CSN_SID_MATCH_MR                               = "csnSidMatch";
    public final static String CSN_SID_MATCH_MR_OID                           = "1.3.6.1.4.1.4203.666.11.2.5";

    // nameOrNumericIdMatch
    public final static String NAME_OR_NUMERIC_ID_MATCH                       = "nameOrNumericIdMatch";
    public final static String NAME_OR_NUMERIC_ID_MATCH_OID                   = "1.3.6.1.4.1.18060.0.4.0.1.0";

    // objectClassTypeMatch
    public final static String OBJECT_CLASS_TYPE_MATCH                        = "objectClassTypeMatch";
    public final static String OBJECT_CLASS_TYPE_MATCH_OID                    = "1.3.6.1.4.1.18060.0.4.0.1.1";

    // numericOidMatch
    public final static String NUMERIC_OID_MATCH                              = "numericOidMatch";
    public final static String NUMERIC_OID_MATCH_OID                          = "1.3.6.1.4.1.18060.0.4.0.1.2";

    // supDITStructureRuleMatch
    public final static String SUP_DIT_STRUCTURE_RULE_MATCH                   = "supDITStructureRuleMatch";
    public final static String SUP_DIT_STRUCTURE_RULE_MATCH_OID               = "1.3.6.1.4.1.18060.0.4.0.1.3";

    // ruleIDMatch
    public final static String RULE_ID_MATCH                                  = "ruleIDMatch";
    public final static String RULE_ID_MATCH_OID                              = "1.3.6.1.4.1.18060.0.4.0.1.4";

    // ExactDnAsStringMatch
    public final static String EXACT_DN_AS_STRING_MATCH_MR                    = "exactDnAsStringMatch";
    public final static String EXACT_DN_AS_STRING_MATCH_MR_OID                = "1.3.6.1.4.1.18060.0.4.1.1.1";

    // BigIntegerMatch
    public final static String BIG_INTEGER_MATCH_MR                           = "bigIntegerMatch";
    public final static String BIG_INTEGER_MATCH_MR_OID                       = "1.3.6.1.4.1.18060.0.4.1.1.2";

    // JdbmStringMatch
    public final static String JDBM_STRING_MATCH_MR                           = "jdbmStringMatch";
    public final static String JDBM_STRING_MATCH_MR_OID                       = "1.3.6.1.4.1.18060.0.4.1.1.3";

    // ComparatorMatch
    public final static String COMPARATOR_MATCH_MR                            = "comparatorMatch";
    public final static String COMPARATOR_MATCH_MR_OID                        = "1.3.6.1.4.1.18060.0.4.1.1.5";

    // NormalizerMatch
    public final static String NORMALIZER_MATCH_MR                            = "normalizerMatch";
    public final static String NORMALIZER_MATCH_MR_OID                        = "1.3.6.1.4.1.18060.0.4.1.1.6";

    // SyntaxCheckerMatch
    public final static String SYNTAX_CHECKER_MATCH_MR                        = "syntaxCheckerMatch";
    public final static String SYNTAX_CHECKER_MATCH_MR_OID                    = "1.3.6.1.4.1.18060.0.4.1.1.7";

    // ---- Features ----------------------------------------------------------
    public final static String FEATURE_ALL_OPERATIONAL_ATTRIBUTES             = "1.3.6.1.4.1.4203.1.5.1";

    // ----Administrative roles -----------------------------------------------
    // AutonomousArea
    public final static String AUTONOMOUS_AREA                                = "autonomousArea";
    public final static String AUTONOMOUS_AREA_OID                            = "2.5.23.1";

    // AccessControlSpecificArea
    public final static String ACCESS_CONTROL_SPECIFIC_AREA                   = "accessControlSpecificArea";
    public final static String ACCESS_CONTROL_SPECIFIC_AREA_OID               = "2.5.23.2";

    // AccessControlInnerArea
    public final static String ACCESS_CONTROL_INNER_AREA                      = "accessControlInnerArea";
    public final static String ACCESS_CONTROL_INNER_AREA_OID                  = "2.5.23.3";

    // SubSchemaAdminSpecificArea
    public final static String SUB_SCHEMA_ADMIN_SPECIFIC_AREA                 = "subSchemaSpecificArea";
    public final static String SUB_SCHEMA_ADMIN_SPECIFIC_AREA_OID             = "2.5.23.4";

    // CollectiveAttributeSpecificArea
    public final static String COLLECTIVE_ATTRIBUTE_SPECIFIC_AREA             = "collectiveAttributeSpecificArea";
    public final static String COLLECTIVE_ATTRIBUTE_SPECIFIC_AREA_OID         = "2.5.23.5";

    // CollectiveAttributeInnerArea
    public final static String COLLECTIVE_ATTRIBUTE_INNER_AREA                = "collectiveAttributeInnerArea";
    public final static String COLLECTIVE_ATTRIBUTE_INNER_AREA_OID            = "2.5.23.6";

    // TriggerExecutionSpecificArea
    public final static String TRIGGER_EXECUTION_SPECIFIC_AREA                = "triggerExecutionSpecificArea";
    public final static String TRIGGER_EXECUTION_SPECIFIC_AREA_OID            = "1.3.6.1.4.1.18060.0.4.1.6.1";

    // TriggerExecutionInnerArea
    public final static String TRIGGER_EXECUTION_INNER_AREA                   = "triggerExecutionInnerArea";
    public final static String TRIGGER_EXECUTION_INNER_AREA_OID               = "1.3.6.1.4.1.18060.0.4.1.6.2";
}
