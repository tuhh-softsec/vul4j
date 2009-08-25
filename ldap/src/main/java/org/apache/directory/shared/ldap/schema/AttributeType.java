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
package org.apache.directory.shared.ldap.schema;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.LdapSyntaxRegistry;
import org.apache.directory.shared.ldap.schema.registries.MatchingRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.Registries;


/**
 * An attributeType specification. attributeType specifications describe the
 * nature of attributes within the directory. The attributeType specification's
 * properties are accessible through this interface.
 * <p>
 * According to ldapbis [MODELS]:
 * </p>
 * 
 * <pre>
 *  4.1.2. Attribute Types
 *  
 *    Attribute Type definitions are written according to the ABNF:
 *  
 *      AttributeTypeDescription = LPAREN WSP
 *          numericoid                   ; object identifier
 *          [ SP &quot;NAME&quot; SP qdescrs ]     ; short names (descriptors)
 *          [ SP &quot;DESC&quot; SP qdstring ]    ; description
 *          [ SP &quot;OBSOLETE&quot; ]            ; not active
 *          [ SP &quot;SUP&quot; SP oid ]          ; supertype
 *          [ SP &quot;EQUALITY&quot; SP oid ]     ; equality matching rule
 *          [ SP &quot;ORDERING&quot; SP oid ]     ; ordering matching rule
 *          [ SP &quot;SUBSTR&quot; SP oid ]       ; substrings matching rule
 *          [ SP &quot;SYNTAX&quot; SP noidlen ]   ; value syntax
 *          [ SP &quot;SINGLE-VALUE&quot; ]        ; single-value
 *          [ SP &quot;COLLECTIVE&quot; ]          ; collective
 *          [ SP &quot;NO-USER-MODIFICATION&quot; ]; not user modifiable
 *          [ SP &quot;USAGE&quot; SP usage ]      ; usage
 *          extensions WSP RPAREN        ; extensions
 *  
 *      usage = &quot;userApplications&quot;     / ; user
 *              &quot;directoryOperation&quot;   / ; directory operational
 *              &quot;distributedOperation&quot; / ; DSA-shared operational
 *              &quot;dSAOperation&quot;           ; DSA-specific operational
 *  
 *    where:
 *      [numericoid] is object identifier assigned to this attribute type;
 *      NAME [qdescrs] are short names (descriptors) identifying this
 *          attribute type;
 *      DESC [qdstring] is a short descriptive string;
 *      OBSOLETE indicates this attribute type is not active;
 *      SUP oid specifies the direct supertype of this type;
 *      EQUALITY, ORDERING, SUBSTRING provide the oid of the equality,
 *          ordering, and substrings matching rules, respectively;
 *      SYNTAX identifies value syntax by object identifier and may suggest
 *          a minimum upper bound;
 *      COLLECTIVE indicates this attribute type is collective [X.501];
 *      NO-USER-MODIFICATION indicates this attribute type is not user
 *          modifiable;
 *      USAGE indicates the application of this attribute type; and
 *      [extensions] describe extensions.
 *  
 *    Each attribute type description must contain at least one of the SUP
 *    or SYNTAX fields.
 *  
 *    Usage of userApplications, the default, indicates that attributes of
 *    this type represent user information.  That is, they are user
 *    attributes.
 *  
 *    COLLECTIVE requires usage userApplications.  Use of collective
 *    attribute types in LDAP is not discussed in this technical
 *    specification.
 *  
 *    A usage of directoryOperation, distributedOperation, or dSAOperation
 *    indicates that attributes of this type represent operational and/or
 *    administrative information.  That is, they are operational attributes.
 *  
 *    directoryOperation usage indicates that the attribute of this type is
 *    a directory operational attribute.  distributedOperation usage
 *    indicates that the attribute of this DSA-shared usage operational
 *    attribute.  dSAOperation usage indicates that the attribute of this
 *    type is a DSA-specific operational attribute.
 *  
 *    NO-USER-MODIFICATION requires an operational usage.
 *  
 *    Note that the [AttributeTypeDescription] does not list the matching
 *    rules which can be used with that attribute type in an extensibleMatch
 *    search filter.  This is done using the 'matchingRuleUse' attribute
 *    described in Section 4.1.4.
 *  
 *    This document refines the schema description of X.501 by requiring
 *    that the SYNTAX field in an [AttributeTypeDescription] be a string
 *    representation of an object identifier for the LDAP string syntax
 *    definition with an optional indication of the suggested minimum bound
 *    of a value of this attribute.
 *  
 *    A suggested minimum upper bound on the number of characters in a value
 *    with a string-based syntax, or the number of bytes in a value for all
 *    other syntaxes, may be indicated by appending this bound count inside
 *    of curly braces following the syntax's OBJECT IDENTIFIER in an
 *  
 *    Attribute Type Description.  This bound is not part of the syntax name
 *    itself.  For instance, &quot;1.3.6.4.1.1466.0{64}&quot; suggests that server
 *    implementations should allow a string to be 64 characters long,
 *    although they may allow longer strings.  Note that a single character
 *    of the Directory String syntax may be encoded in more than one octet
 *    since UTF-8 is a variable-length encoding.
 * </pre>
 * 
 * @see <a href="http://www.faqs.org/rfcs/rfc2252.html">RFC 2252 Section 4.2</a>
 * @see <a
 *      href="http://www.ietf.org/internet-drafts/draft-ietf-ldapbis-models-11.txt">
 *      ldapbis [MODELS]</a>
 * @see DescriptionUtils#getDescription(AttributeType)
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AttributeType extends SchemaObject
{
    /** The serialVersionUID */
    public static final long serialVersionUID = 1L;
    
    /** The syntax OID associated with this AttributeType */
    private String syntaxOid;
    
    /** The syntax associated with the syntaxID */
    private LdapSyntax syntax;
    
    /** The equality OID associated with this AttributeType */
    private String equalityOid;

    /** The equality MatchingRule associated with the equalityID */
    private MatchingRule equality;
    
    /** The substring OID associated with this AttributeType */
    private String substrOid;

    /** The substring MatchingRule associated with the substringID */
    private MatchingRule substr;
    
    /** The ordering OID associated with this AttributeType */
    private String orderingOid;
    
    /** The ordering MatchingRule associated with the orderingID */
    private MatchingRule ordering;
    
    /** The superior AttributeType OID */
    private String supOid;
    
    /** The superior AttributeType */
    private AttributeType sup;
    
    /** whether or not this type is single valued */
    private boolean isSingleValue = false;

    /** whether or not this type is a collective attribute */
    private boolean isCollective = false;

    /** whether or not this type can be modified by directory users */
    private boolean canUserModify = true;

    /** the usage for this attributeType */
    private UsageEnum usage = UsageEnum.USER_APPLICATIONS;

    /** the length of this attribute in bytes */
    private int length = -1;
    
    /**
     * Creates a AttributeType object using a unique OID.
     * 
     * @param oid the OID for this AttributeType
     */
    public AttributeType( String oid )
    {
        super( SchemaObjectType.ATTRIBUTE_TYPE, oid );
    }
    
    
    /**
     * Inject the registries into this Object, updating the references to
     * other SchemaObject
     *
     * @param registries The Registries
     */
    public void applyRegistries( Registries registries ) throws NamingException
    {
        if ( registries != null )
        {
            AttributeTypeRegistry atRegistry = registries.getAttributeTypeRegistry();
            
            sup = atRegistry.lookup( supOid );
            
            MatchingRuleRegistry mrRegistry = registries.getMatchingRuleRegistry();
            
            equality = mrRegistry.lookup( equalityOid );
            ordering = mrRegistry.lookup( orderingOid );
            substr = mrRegistry.lookup( substrOid );
            
            LdapSyntaxRegistry lsRegistry = registries.getLdapSyntaxRegistry();
            
            syntax = lsRegistry.lookup( syntaxOid );
            
        }
    }
    
    
    /**
     * Gets whether or not this AttributeType is single-valued.
     * 
     * @return true if only one value can exist for this AttributeType, false
     *         otherwise
     */
    public boolean isSingleValue()
    {
        return isSingleValue;
    }


    /**
     * Tells if this AttributeType is SIngle Valued or not
     *
     * @param singleValue True if the AttributeType is single-vlaued
     */
    public void setSingleValue( boolean singleValue )
    {
        if ( !isReadOnly )
        {
            this.isSingleValue = singleValue;
        }
    }

    
    /**
     * Gets whether or not this AttributeType can be modified by a user.
     * 
     * @return true if users can modify it, false if only the directory can.
     */
    public boolean isCanUserModify()
    {
        return canUserModify;
    }

    
    /**
     * Tells if this AttributeType can be modified by a user or not
     *
     * @param canUserModify The flag to set
     */
    public void setCanUserModify( boolean canUserModify )
    {
        if ( !isReadOnly )
        {
            this.canUserModify = canUserModify;
        }
    }
    
    

    /**
     * Gets whether or not this AttributeType is a collective attribute.
     * 
     * @return true if the attribute is collective, false otherwise
     */
    public boolean isCollective()
    {
        return isCollective;
    }


    /**
     * Tells if this AttributeType is a collective attribute or not
     *
     * @param collective True if the AttributeType is collective
     */
    public void setCollective( boolean collective )
    {
        if ( !isReadOnly )
        {
            this.isCollective = collective;
        }
    }
    
    
    /**
     * Determines the usage for this AttributeType.
     * 
     * @return a type safe UsageEnum
     */
    public UsageEnum getUsage()
    {
        return usage;
    }


    /**
     * Sets the AttributeType usage, one of :<br>
     * <li>USER_APPLICATIONS
     * <li>DIRECTORY_OPERATION
     * <li>DISTRIBUTED_OPERATION
     * <li>DSA_OPERATION
     * <br>
     * @see UsageEnum
     * @param usage The AttributeType usage
     */
    public void setUsage( UsageEnum usage )
    {
        if ( !isReadOnly )
        {
            this.usage = usage;
        }
    }
    

    /**
     * Gets a length limit for this AttributeType.
     * 
     * @return the length of the attribute
     */
    public int getLength()
    {
        return length;
    }
    
    
    /**
     * Sets the length limit of this AttributeType based on its associated
     * syntax.
     * 
     * @param length the new length to set
     */
    public void setLength( int length )
    {
        if ( !isReadOnly )
        {
            this.length = length;
        }
    }
    
    
    /**
     * Gets the the superior AttributeType of this AttributeType.
     * 
     * @return the superior AttributeType for this AttributeType
     */
    public AttributeType getSup()
    {
        return sup;
    }

    
    /**
     * Gets the OID of the superior AttributeType for this AttributeType.
     * 
     * @return The OID of the superior AttributeType for this AttributeType.
     */
    public String getSupOid()
    {
        return supOid;
    }

    
    /**
     * Sets the superior AttributeType OID of this AttributeType
     *
     * @param superiorOid The superior AttributeType OID of this AttributeType
     */
    public void setSuperiorOid( String superiorOid ) throws NamingException
    {
        if ( !isReadOnly )
        {
            this.supOid = superiorOid;
        }
    }


    /**
     * Gets the Syntax for this AttributeType's values.
     * 
     * @return the value syntax
     */
    public LdapSyntax getSyntax()
    {
        return syntax;
    }


    /**
     * Gets the Syntax OID for this AttributeType's values.
     * 
     * @return the value syntax's OID
     */
    public String getSyntaxOid()
    {
        return syntaxOid;
    }


    /**
     * Sets the Syntax OID for this AttributeType
     *
     * @param superiorOid The syntax OID for this AttributeType
     * @throws NamingException if there is a failure to resolve the matchingRule
     */
    public void setSyntaxOid( String syntaxOid ) throws NamingException
    {
        if ( !isReadOnly )
        {
            this.syntaxOid = syntaxOid;
        }
    }

    
    /**
     * Gets the MatchingRule for this AttributeType used for equality matching.
     * 
     * @return the equality matching rule
     */
    public MatchingRule getEquality()
    {
        return equality;
    }


    /**
     * Gets the Equality OID for this AttributeType's values.
     * 
     * @return the value Equality's OID
     */
    public String getEqualityOid()
    {
        return equalityOid;
    }


    /**
     * Sets the Equality OID for this AttributeType
     *
     * @param equalityOid The Equality OID for this AttributeType
     * @throws NamingException if there is a failure to resolve the matchingRule
     */
    public void setEqualityOid( String equalityOid ) throws NamingException
    {
        if ( !isReadOnly )
        {
            this.equalityOid = equalityOid;
        }
    }
    

    /**
     * Gets the MatchingRule for this AttributeType used for Ordering matching.
     * 
     * @return the Ordering matching rule
     */
    public MatchingRule getOrdering()
    {
        return ordering;
    }


    /**
     * Gets the Ordering OID for this AttributeType's values.
     * 
     * @return the value Equality's OID
     */
    public String getOrderingOid()
    {
        return orderingOid;
    }


    /**
     * Sets the Ordering OID for this AttributeType
     *
     * @param orderingOid The Ordering OID for this AttributeType
     * @throws NamingException if there is a failure to resolve the matchingRule
     */
    public void setOrderingOid( String orderingOid ) throws NamingException
    {
        if ( !isReadOnly )
        {
            this.orderingOid = orderingOid;
        }
    }

    
    /**
     * Gets the MatchingRule for this AttributeType used for Substr matching.
     * 
     * @return the Substr matching rule
     */
    public MatchingRule getSubstr()
    {
        return substr;
    }


    /**
     * Gets the Substr OID for this AttributeType's values.
     * 
     * @return the value Substr's OID
     */
    public String getSubstrOid()
    {
        return substrOid;
    }


    /**
     * Sets the Substr OID for this AttributeType
     *
     * @param substrOid The Substr OID for this AttributeType
     * @throws NamingException if there is a failure to resolve the matchingRule
     */
    public void setSubstrOid( String substrOid ) throws NamingException
    {
        if ( !isReadOnly )
        {
            this.substrOid = substrOid;
        }
    }


    /**
     * Checks to see if this AttributeType is the ancestor of another
     * attributeType.
     *
     * @param descendant the perspective descendant to check
     * @return true if the descendant is truely a derived from this AttributeType
     * @throws NamingException if there are problems resolving superior types
     */
    public boolean isAncestorOf( AttributeType descendant ) throws NamingException
    {
        if ( ( descendant == null ) || this.equals( descendant ) )
        {
            return false;
        }

        return isAncestorOrEqual( this, descendant );
    }

    
    /**
     * Checks to see if this AttributeType is the descendant of another
     * attributeType.
     *
     * @param ancestor the perspective ancestor to check
     * @return true if this AttributeType truely descends from the ancestor
     * @throws NamingException if there are problems resolving superior types
     */
    public boolean isDescendantOf( AttributeType ancestor ) throws NamingException
    {
        if ( ( ancestor == null ) || equals( ancestor ) )
        {
            return false;
        }

        return isAncestorOrEqual( ancestor, this );
    }


    
    
    /**
     * Recursive method which checks to see if a descendant is really an ancestor or if the two
     * are equal.
     *
     * @param ancestor the possible ancestor of the descendant
     * @param descendant the possible descendant of the ancestor
     * @return true if the ancestor equals the descendant or if the descendant is really
     * a subtype of the ancestor. otherwise false
     * @throws NamingException if there are issues with superior attribute resolution
     */
    private boolean isAncestorOrEqual( AttributeType ancestor, AttributeType descendant ) throws NamingException
    {
        if ( ( ancestor == null ) || ( descendant == null ) )
        {
            return false;
        }

        if ( ancestor.equals( descendant ) )
        {
            return true;
        }

        return isAncestorOrEqual( ancestor, descendant.getSup() );
    }
}
