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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.ObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.registries.Registries;


/**
 * A nameForm description. NameForms define the relationship between a
 * STRUCTURAL objectClass definition and the attributeTypes allowed to be used
 * for the naming of an Entry of that objectClass: it defines which attributes
 * can be used for the RDN.
 * <p>
 * According to ldapbis [MODELS]:
 * </p>
 * 
 * <pre>
 *  4.1.7.2. Name Forms
 *  
 *   A name form &quot;specifies a permissible RDN for entries of a particular
 *   structural object class.  A name form identifies a named object
 *   class and one or more attribute types to be used for naming (i.e.
 *   for the RDN).  Name forms are primitive pieces of specification
 *   used in the definition of DIT structure rules&quot; [X.501].
 * 
 *   Each name form indicates the structural object class to be named,
 *   a set of required attribute types, and a set of allowed attributes
 *   types.  A particular attribute type cannot be listed in both sets.
 * 
 *   Entries governed by the form must be named using a value from each
 *   required attribute type and zero or more values from the allowed
 *   attribute types.
 * 
 *   Each name form is identified by an object identifier (OID) and,
 *   optionally, one or more short names (descriptors).
 * 
 *   Name form descriptions are written according to the ABNF:
 * 
 *     NameFormDescription = LPAREN WSP
 *         numericoid                ; object identifier
 *         [ SP &quot;NAME&quot; SP qdescrs ]  ; short names (descriptors)
 *         [ SP &quot;DESC&quot; SP qdstring ] ; description
 *         [ SP &quot;OBSOLETE&quot; ]         ; not active
 *         SP &quot;OC&quot; SP oid            ; structural object class
 *         SP &quot;MUST&quot; SP oids         ; attribute types
 *         [ SP &quot;MAY&quot; SP oids ]      ; attribute types
 *         extensions WSP RPAREN     ; extensions
 * 
 *   where:
 * 
 *     [numericoid] is object identifier which identifies this name form;
 *     NAME [qdescrs] are short names (descriptors) identifying this name
 *         form;
 *     DESC [qdstring] is a short descriptive string;
 *     OBSOLETE indicates this name form is not active;
 *     OC identifies the structural object class this rule applies to,
 *     MUST and MAY specify the sets of required and allowed, respectively,
 *         naming attributes for this name form; and
 *     [extensions] describe extensions.
 * 
 *   All attribute types in the required (&quot;MUST&quot;) and allowed (&quot;MAY&quot;) lists
 *   shall be different.
 * </pre>
 * 
 * @see <a href="http://www.faqs.org/rfcs/rfc2252.html">RFC2252 Section 6.22</a>
 * @see <a
 *      href="http://www.ietf.org/internet-drafts/draft-ietf-ldapbis-models-11.txt">ldapbis
 *      [MODELS]</a>
 * @see DescriptionUtils#getDescription(NameForm)
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class NameForm extends SchemaObject
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The structural object class this rule applies to */
    private String structuralObjectClass;
    
    /** The set of required attributes for this name form */
    private List<String> mustAttributeTypes;

    /** The set of allowed attributes for this name form */
    private List<String> mayAttributeTypes;
    
    /** The associated AttributeType registry */
    private AttributeTypeRegistry atRegistry;
    
    /** The associated ObjectClass registry */
    private ObjectClassRegistry ocRegistry;
    

    /**
     * Creates a new instance of MatchingRule.
     *
     * @param oid The MatchingRule OID
     * @param registries The Registries reference
     */
    public NameForm( String oid, Registries registries )
    {
        super( SchemaObjectType.NAME_FORM, oid );
        
        mustAttributeTypes = new ArrayList<String>();
        mayAttributeTypes = new ArrayList<String>();
        
        if ( registries != null )
        {
            atRegistry = registries.getAttributeTypeRegistry();
            ocRegistry = registries.getObjectClassRegistry();
        }
    }


    /**
     * Gets the STRUCTURAL ObjectClass this name form specifies naming
     * attributes for.
     * 
     * @return the ObjectClass's oid this NameForm is for
     * @throws NamingException If the structuralObjectClass is invalid
     */
    public String getStructuralObjectClass() throws NamingException
    {
        return structuralObjectClass;
    }


    /**
     * Sets the structural object class this rule applies to
     * 
     * @param structuralObjectClass the structural object class to set
     */
    public void setStructuralObjectClass( String structuralObjectClass )
    {
        if ( !isReadOnly )
        {
            this.structuralObjectClass = structuralObjectClass;
        }
    }


    /**
     * Gets all the AttributeTypes of the attributes this NameForm specifies as
     * having to be used in the given objectClass for naming: as part of the
     * Rdn.
     * 
     * @return the AttributeTypes of the must use attributes
     * @throws NamingException if there is a failure resolving one AttributeTyoe
     */
    public List<String> getMustAttributeTypes() throws NamingException
    {
        return Collections.unmodifiableList( mustAttributeTypes );
        
        /*
        if ( mustAttributeTypes != null )
        {
            List<AttributeType> must = new ArrayList<AttributeType>();
            
            for ( String oid : mustAttributeTypes )
            {
                must.add( atRegistry.lookup( oid ) );
            }
            
            return must;
        }
        
        return null;
        */
    }


    /**
     * Sets the list of required AttributeTypes
     *
     * @param mustAttributeTypes the list of required AttributeTypes
     */
    public void setMustAttributeTypes( List<String> mustAttributeTypes )
    {
        if ( !isReadOnly )
        {
            this.mustAttributeTypes = mustAttributeTypes;
        }
    }


    /**
     * Add a required AttribyuteType
     *
     * @param oid The attributeType oid
     */
    public void addMustAttributeType( String oid )
    {
        if ( !isReadOnly )
        {
            mustAttributeTypes.add( oid );
        }
    }

    
    /**
     * Gets all the AttributeTypes of the attribute this NameForm specifies as
     * being useable without requirement in the given objectClass for naming: as
     * part of the Rdn.
     * 
     * @return the AttributeTypes of the may use attributes
     * @throws NamingException if there is a failure resolving one AttributeTyoe
     */
    public List<String> getMayAttributeTypes() throws NamingException
    {
        return Collections.unmodifiableList( mayAttributeTypes );
        
        /*
        if ( mayAttributeTypes != null )
        {
            List<AttributeType> may = new ArrayList<AttributeType>();
            
            for ( String oid : mayAttributeTypes )
            {
                may.add( atRegistry.lookup( oid ) );
            }
            
            return may;
        }
        
        return null;
        */
    }
    
    
    /**
     * Sets the list of allowed AttributeTypes
     *
     * @param mustAttributeTypes the list of allowed AttributeTypes
     */
    public void setMayAttributeTypes( List<String> mayAttributeTypes )
    {
        if ( !isReadOnly )
        {
            this.mayAttributeTypes = mayAttributeTypes;
        }
    }
    
    
    /**
     * Add an allowed AttribyuteType
     *
     * @param oid The attributeType oid
     */
    public void addMayAttributeType( String oid )
    {
        if ( !isReadOnly )
        {
            mayAttributeTypes.add( oid );
        }
    }
}
