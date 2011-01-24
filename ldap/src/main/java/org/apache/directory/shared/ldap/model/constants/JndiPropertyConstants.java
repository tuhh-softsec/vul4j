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
 * This class contains all the Ldap specific properties described in the JNDI API.
 * See http://java.sun.com/j2se/1.5.0/docs/guide/jndi/jndi-ldap-gl.html
 * Final reference -> class shouldn't be extended
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class JndiPropertyConstants
{
    /**
     *  Ensures no construction of this class, also ensures there is no need for final keyword above
     *  (Implicit super constructor is not visible for default constructor),
     *  but is still self documenting.
     */
    private JndiPropertyConstants()
    {
    }

    // Pure JNDI properties
    /** Batch size of search results returned */
    public final static String JNDI_BATCHSIZE = "java.naming.batchsize";

    /** List of FQCNs of the control factory classes */
    public final static String JNDI_FACTORY_CONTROL = "java.naming.factory.control";

    /** FQCN of the factory creating the InitialContext */
    public final static String JNDI_FACTORY_INITIAL = "java.naming.factory.initial";

    /** List of FQCNs of the Object factory */
    public final static String JNDI_FACTORY_OBJECT = "java.naming.factory.object";

    /** List of FQCNs of the state factory */
    public final static String JNDI_FACTORY_STATE = "java.naming.factory.state";

    /** The Language to use */
    public final static String JNDI_LANGUAGE = "java.naming.language";

    /** The list of URL pointing to a LDAP server */
    public final static String JNDI_PROVIDER_URL = "java.naming.provider.url";

    /** Tells how the referral should be handled */
    public final static String JNDI_REFERRAL = "java.naming.referral";

    /** The Authentication mechanism */
    public final static String JNDI_SECURITY_AUTHENTICATION = "java.naming.security.authentication";

    /** The credentials */
    public final static String JNDI_SECURITY_CREDENTIALS = "java.naming.security.credentials";

    /** The Principal */
    public final static String JNDI_SECURITY_PRINCIPAL = "java.naming.security.principal";

    /** The security protocol to use */
    public final static String JNDI_SECURITY_PROTOCOL = "java.naming.security.protocol";

    // Ldap specific properties
    /** The list of binary attributes */
    public final static String JNDI_LDAP_ATTRIBUTES_BINARY = "java.naming.ldap.attributes.binary";

    /** The controls to send when connectiong */
    public final static String JNDI_LDAP_CONTROL_CONNECT = "java.naming.ldap.control.connect";

    /** Tells if the old Rdn must be deleted when doing a MODDN */
    public final static String JNDI_LDAP_DELETE_RDN = "java.naming.ldap.deleteRDN";

    /** Tells if and how we dereference aliases */
    public final static String JNDI_LDAP_DAP_DEREF_ALIASES = "java.naming.ldap.derefAliases";

    /** The FQCN of the socket factory to use to connect to the server */
    public final static String JNDI_FACTORY_SOCKET = "java.naming.ldap.factory.socket";

    /** The separator to use when dealing with RefAddr */
    public final static String JNDI_LDAP_REF_SEPARATOR = "java.naming.ldap.ref.separator";

    /** The maximum number of referral to follow in a chain of referrals */
    public final static String JNDI_LDAP_REFERRAL_LIMIT = "java.naming.ldap.referral.limit";

    /** tells that we want the attributeTypes only to be returned */
    public final static String JNDI_LDAP_TYPES_ONLY = "java.naming.ldap.typesOnly";

    /** Specifies the LDAP version to use */
    public final static String JNDI_LDAP_VERSION = "java.naming.ldap.version";

    // SASL properties
    /** The SASL authorization ID */
    public final static String JNDI_SASL_AUTHORIZATION_ID = "java.naming.security.sasl.authorizationId";

    /** The SASL Realm */
    public final static String JNDI_SASL_REALM = "java.naming.security.sasl.realm";

    /** An instance of CallbackHandler to use when required */
    public final static String JNDI_SASL_CALLBACK = "java.naming.security.sasl.callback";

    /** The SASL Quality Of Protection value */
    public final static String JNDI_SASL_QOP = "javax.security.sasl.qop";

    /** The cipher strength */
    public final static String JNDI_SASL_STRENGTH = "javax.security.sasl.strength";

    /** The maximum size of the receive buffer */
    public final static String JNDI_SASL_MAX_BUFFER = "javax.security.sasl.maxbuffer";

    /** Tells if the the server must authenticate the client */
    public final static String JNDI_SASL_AUTHENTICATION = "javax.security.sasl.server.authentication";

    /** Tells if the server must support forward secrecy */
    public final static String JNDI_SASL_POLICY_FORWARD = "javax.security.sasl.policy.forward";

    /** Tells if the server must require some credentials */
    public final static String JNDI_SASL_POLICY_CREDENTIALS = "javax.security.sasl.policy.credentials";

    /** Tells if the server allow Plain text mechanism */
    public final static String JNDI_SASL_POLICY_NO_PLAIN_TEXT = "javax.security.sasl.policy.noplaintext";

    /** Tells if the SASL mechanism is protected against active attacks */
    public final static String JNDI_SASL_POLICY_NO_ACTIVE = "javax.security.sasl.policy.noactive";

    /** Tells if the SASL mechanism is protected against dictionary attacks */
    public final static String JNDI_SASL_POLICY_NO_DICTIONARY = "javax.security.sasl.policy.nodictionary";

    /** Tells if the SASL mechanism accept or not anonymous connections */
    public final static String JNDI_SASL_POLICY_NO_ANONYMOUS = "javax.security.sasl.policy.noanonymous";
}
