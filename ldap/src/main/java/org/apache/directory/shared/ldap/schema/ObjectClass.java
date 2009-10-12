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
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.ObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.registries.Registries;


/**
 * An objectClass definition.
 * <p>
 * According to ldapbis [MODELS]:
 * </p>
 * 
 * <pre>
 *  Object Class definitions are written according to the ABNF:
 *  
 *    ObjectClassDescription = LPAREN WSP
 *        numericoid                ; object identifier
 *        [ SP &quot;NAME&quot; SP qdescrs ]  ; short names (descriptors)
 *        [ SP &quot;DESC&quot; SP qdstring ] ; description
 *        [ SP &quot;OBSOLETE&quot; ]         ; not active
 *        [ SP &quot;SUP&quot; SP oids ]      ; superior object classes
 *        [ SP kind ]               ; kind of class
 *        [ SP &quot;MUST&quot; SP oids ]     ; attribute types
 *        [ SP &quot;MAY&quot; SP oids ]      ; attribute types
 *        extensions WSP RPAREN
 * 
 *     kind = &quot;ABSTRACT&quot; / &quot;STRUCTURAL&quot; / &quot;AUXILIARY&quot;
 * 
 *   where:
 *     [numericoid] is object identifier assigned to this object class;
 *     NAME [qdescrs] are short names (descriptors) identifying this object
 *         class;
 *     DESC [qdstring] is a short descriptive string;
 *     OBSOLETE indicates this object class is not active;
 *     SUP [oids] specifies the direct superclasses of this object class;
 *     the kind of object class is indicated by one of ABSTRACT,
 *         STRUCTURAL, or AUXILIARY, default is STRUCTURAL;
 *     MUST and MAY specify the sets of required and allowed attribute
 *         types, respectively; and
 *    [extensions] describe extensions.
 * </pre>
 * 
 * @see <a href="http://www.faqs.org/rfcs/rfc2252.html">RFC2252 Section 4.4</a>
 * @see <a
 *      href="http://www.ietf.org/internet-drafts/draft-ietf-ldapbis-models-11.txt">ldapbis
 *      [MODELS]</a>
 * @see DescriptionUtils#getDescription(ObjectClass)
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ObjectClass extends SchemaObject
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The ObjectClass type : ABSTRACT, AUXILIARY or STRUCTURAL */
    private ObjectClassTypeEnum objectClassType = ObjectClassTypeEnum.STRUCTURAL;
    
    /** The ObjectClass superior OIDs */
    private List<String> superiorOids;

    /** The ObjectClass superiors */
    private List<ObjectClass> superiors;

    /** The list of allowed AttributeType OIDs */
    private List<String> mayAttributeTypeOids;

    /** The list of allowed AttributeTypes */
    private List<AttributeType> mayAttributeTypes;

    /** The list of required AttributeType OIDs */
    private List<String> mustAttributeTypeOids;

    /** The list of required AttributeTypes */
    private List<AttributeType> mustAttributeTypes;

    /**
     * Creates a new instance of MatchingRuleUseDescription
     * @param oid the OID for this objectClass
     */
    public ObjectClass( String oid )
    {
        super(  SchemaObjectType.OBJECT_CLASS, oid );
        
        mayAttributeTypeOids = new ArrayList<String>();
        mustAttributeTypeOids = new ArrayList<String>();
        superiorOids = new ArrayList<String>();

        mayAttributeTypes = new ArrayList<AttributeType>();
        mustAttributeTypes = new ArrayList<AttributeType>();
        superiors = new ArrayList<ObjectClass>();
        objectClassType = ObjectClassTypeEnum.STRUCTURAL;
    }
    
    
    /**
     * Inject the registries into this Object, updating the references to
     * other SchemaObject
     *
     * @param registries The Registries
     * @throws Exception on failure
     *
     */
    public void applyRegistries( Registries registries ) throws NamingException
    {
        if ( registries != null )
        {
            AttributeTypeRegistry atRegistry = registries.getAttributeTypeRegistry();
            ObjectClassRegistry ocRegistry = registries.getObjectClassRegistry();
            
            if ( superiorOids != null )
            {
                superiors = new ArrayList<ObjectClass>( superiorOids.size() );
                
                for ( String superiorName : superiorOids )
                {
                	if ( superiorName.equals( "top" ) )
                	{
                		continue;
                	}
                	
                    superiors.add( ocRegistry.lookup( ocRegistry.getOidByName( superiorName ) ) );
                }
            }

            if ( mayAttributeTypeOids != null )
            {
                mayAttributeTypes = new ArrayList<AttributeType>( mayAttributeTypeOids.size() );
                
                for ( String mayAttributeTypeName : mayAttributeTypeOids )
                {
                    mayAttributeTypes.add( atRegistry.lookup( atRegistry.getOidByName( mayAttributeTypeName ) ) );
                }
            }

            if ( mustAttributeTypeOids != null )
            {
                mustAttributeTypes = new ArrayList<AttributeType>( mustAttributeTypeOids.size() );
                
                for ( String mustAttributeTypeName : mustAttributeTypeOids )
                {
                    mustAttributeTypes.add( atRegistry.lookup( atRegistry.getOidByName( mustAttributeTypeName ) ) );
                }
            }
        }
    }

    
    /**
     * @return the mayAttributeTypeOids
     */
    public List<String> getMayAttributeTypeOids()
    {
        return mayAttributeTypeOids;
    }


    /**
     * @return the mayAttributeTypes
     */
    public List<AttributeType> getMayAttributeTypes()
    {
        return mayAttributeTypes;
    }

    
    /**
     * Add an allowed AttributeType
     *
     * @param oid The attributeType oid
     */
    public void addMayAttributeTypeOids( String oid )
    {
        if ( !isReadOnly )
        {
            mayAttributeTypeOids.add( oid );
        }
    }


    /**
     * Add an allowed AttributeType
     *
     * @param attributeType The attributeType
     */
    public void addMayAttributeTypes( AttributeType attributeType )
    {
        if ( !isReadOnly )
        {
            if ( ! mayAttributeTypeOids.contains( attributeType.getOid() ) )
            {
                mayAttributeTypes.add( attributeType );
                mayAttributeTypeOids.add( attributeType.getOid() );
            }
        }
    }

    
    /**
     * @param mayAttributeTypeOids the mayAttributeTypeOids to set
     */
    public void setMayAttributeTypeOids( List<String> mayAttributeTypeOids )
    {
        if ( !isReadOnly )
        {
            this.mayAttributeTypeOids = mayAttributeTypeOids;
        }
    }
    
    
    /**
     * Sets the list of allowed AttributeTypes
     *
     * @param mayAttributeTypes the list of allowed AttributeTypes
     */
    public void setMayAttributeTypes( List<AttributeType> mayAttributeTypes )
    {
        if ( !isReadOnly )
        {
            this.mayAttributeTypes = mayAttributeTypes;
            
            // update the OIDS now
            mayAttributeTypeOids.clear();
            
            for ( AttributeType may : mayAttributeTypes )
            {
                mayAttributeTypeOids.add( may.getOid() );
            }
        }
    }


    /**
     * @return the mustAttributeTypeOids
     */
    public List<String> getMustAttributeTypeOids()
    {
        return mustAttributeTypeOids;
    }


    /**
     * @return the mustAttributeTypes
     */
    public List<AttributeType> getMustAttributeTypes()
    {
        return mustAttributeTypes;
    }

    
    /**
     * Add a required AttributeType OID
     *
     * @param oid The attributeType OID
     */
    public void addMustAttributeTypeOids( String oid )
    {
        if ( !isReadOnly )
        {
            mustAttributeTypeOids.add( oid );
        }
    }


    /**
     * Add a required AttributeType
     *
     * @param attributeType The attributeType
     */
    public void addMustAttributeTypes( AttributeType attributeType )
    {
        if ( !isReadOnly )
        {
            if ( ! mustAttributeTypeOids.contains( attributeType.getOid() ) )
            {
                mustAttributeTypes.add( attributeType );
                mustAttributeTypeOids.add( attributeType.getOid() );
            }
        }
    }


    /**
     * @param mustAttributeTypeOids the mustAttributeTypeOids to set
     */
    public void setMustAttributeTypeOids( List<String> mustAttributeTypeOids )
    {
        if ( !isReadOnly )
        {
            this.mustAttributeTypeOids = mustAttributeTypeOids;
        }
    }

    
    /**
     * Sets the list of required AttributeTypes
     *
     * @param mustAttributeTypes the list of required AttributeTypes
     */
    public void setMustAttributeTypes( List<AttributeType> mustAttributeTypes )
    {
        if ( !isReadOnly )
        {
            this.mustAttributeTypes = mustAttributeTypes;
            
            // update the OIDS now
            mustAttributeTypeOids.clear();
            
            for ( AttributeType may : mustAttributeTypes )
            {
                mustAttributeTypeOids.add( may.getOid() );
            }
        }
    }
    
    
    /**
     * Gets the superclasses of this ObjectClass.
     * 
     * @return the superclasses
     * @throws NamingException if there is a failure resolving the object
     */
    public List<ObjectClass> getSuperiors()
    {
        return superiors;
    }

    
    /**
     * Gets the superclasses OIDsof this ObjectClass.
     * 
     * @return the superclasses OIDs
     */
    public List<String> getSuperiorOids()
    {
        return superiorOids;
    }


    /**
     * Add some superior ObjectClass OIDs
     *
     * @param oids The superior ObjectClass OIDs
     */
    public void addSuperiorOids( String... oids )
    {
        if ( !isReadOnly )
        {
            for ( String oid : oids )
            {
                if ( !superiorOids.contains( oid ) )
                {
                    superiorOids.add( oid );
                }
            }
        }
    }


    /**
     * Add some superior ObjectClasses
     *
     * @param objectClasses The superior ObjectClasses
     */
    public void addSuperior( ObjectClass... objectClasses )
    {
        if ( !isReadOnly )
        {
            for ( ObjectClass objectClass : objectClasses )
            {
                if ( !superiorOids.contains( objectClass.getOid() ) )
                {
                    superiorOids.add( objectClass.getOid() );
                    superiors.add( objectClass );
                }
            }
        }
    }

    
    /**
     * Sets the superior object classes
     * 
     * @param superiors the object classes to set
     */
    public void setSuperiors( List<ObjectClass> superiors )
    {
        if ( !isReadOnly )
        {
            this.superiors = superiors;
            
            // update the OIDS now
            superiorOids.clear();
            
            for ( ObjectClass oc : superiors )
            {
                superiorOids.add( oc.getOid() );
            }
        }
    }

    
    /**
     * Sets the superior object class OIDs
     * 
     * @param superiorOids the object class OIDs to set
     */
    public void setSuperiorOids( List<String> superiorOids )
    {
        if ( !isReadOnly )
        {
            this.superiorOids = superiorOids;
        }
    }
    

    /**
     * Gets the type of this ObjectClass as a type safe enum.
     * 
     * @return the ObjectClass type as an enum
     */
    public ObjectClassTypeEnum getType()
    {
        return objectClassType;
    }
    
    
    /**
     * Set the ObjectClass type, one of ABSTRACT, AUXILIARY or STRUCTURAL.
     * 
     * @param objectClassType The ObjectClassType value
     */
    public void setType( ObjectClassTypeEnum objectClassType )
    {
        if ( !isReadOnly )
        {
            this.objectClassType = objectClassType;
        }
    }
    
    
    /**
     * Tells if the current ObjectClass is STRUCTURAL
     * 
     * @return <code>true</code> if the ObjectClass is STRUCTURAL
     */
    public boolean isStructural()
    {
        return objectClassType == ObjectClassTypeEnum.STRUCTURAL;
    }
    

    /**
     * Tells if the current ObjectClass is ABSTRACT
     * 
     * @return <code>true</code> if the ObjectClass is ABSTRACT
     */
    public boolean isAbstract()
    {
        return objectClassType == ObjectClassTypeEnum.ABSTRACT;
    }
    

    /**
     * Tells if the current ObjectClass is AUXILIARY
     * 
     * @return <code>true</code> if the ObjectClass is AUXILIARY
     */
    public boolean isAuxiliary()
    {
        return objectClassType == ObjectClassTypeEnum.AUXILIARY;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return DescriptionUtils.getDescription( this );
    }
}