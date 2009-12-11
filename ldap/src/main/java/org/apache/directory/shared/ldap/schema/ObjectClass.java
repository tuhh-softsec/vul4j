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
public class ObjectClass extends AbstractSchemaObject
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
        super( SchemaObjectType.OBJECT_CLASS, oid );

        mayAttributeTypeOids = new ArrayList<String>();
        mustAttributeTypeOids = new ArrayList<String>();
        superiorOids = new ArrayList<String>();

        mayAttributeTypes = new ArrayList<AttributeType>();
        mustAttributeTypes = new ArrayList<AttributeType>();
        superiors = new ArrayList<ObjectClass>();
        objectClassType = ObjectClassTypeEnum.STRUCTURAL;
    }


    /**
     * Inject the ObjectClass into the registries, updating the references to
     * other SchemaObject
     *
     * @param registries The Registries
     * @throws Exception on failure
     *
     */
    public void addToRegistries( List<Throwable> errors, Registries registries ) throws NamingException
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
                    superiors.add( ocRegistry.lookup( ocRegistry.getOidByName( superiorName ) ) );
                }
            }

            if ( mayAttributeTypeOids != null )
            {
                mayAttributeTypes = new ArrayList<AttributeType>( mayAttributeTypeOids.size() );

                List<String> toRemove = new ArrayList<String>();

                for ( String mayAttributeTypeName : mayAttributeTypeOids )
                {
                    AttributeType attributeType = atRegistry.lookup( mayAttributeTypeName );

                    if ( mayAttributeTypes.contains( attributeType ) )
                    {
                        // Already registrred : skip it and remove it from the OIDs list
                        toRemove.add( mayAttributeTypeName );
                        continue;
                    }

                    mayAttributeTypes.add( attributeType );
                }

                for ( String oid : toRemove )
                {
                    mayAttributeTypeOids.remove( oid );
                }
            }

            if ( mustAttributeTypeOids != null )
            {
                mustAttributeTypes = new ArrayList<AttributeType>( mustAttributeTypeOids.size() );

                List<String> toRemove = new ArrayList<String>();

                for ( String mustAttributeTypeName : mustAttributeTypeOids )
                {
                    AttributeType attributeType = atRegistry.lookup( mustAttributeTypeName );

                    if ( mustAttributeTypes.contains( attributeType ) )
                    {
                        // Already registrred : skip it and remove it from the OIDs list
                        toRemove.add( mustAttributeTypeName );
                        continue;
                    }

                    mustAttributeTypes.add( attributeType );
                }

                for ( String oid : toRemove )
                {
                    mustAttributeTypeOids.remove( oid );
                }
            }

            /**
             * Add the OC references (using and usedBy) : 
             * OC -> AT (MAY and MUST)
             * OC -> OC (SUPERIORS)
             */
            for ( AttributeType mayAttributeType : mayAttributeTypes )
            {
                registries.addReference( this, mayAttributeType );
            }

            for ( AttributeType mustAttributeType : mustAttributeTypes )
            {
                registries.addReference( this, mustAttributeType );
            }

            for ( ObjectClass superiorObjectClass : superiors )
            {
                registries.addReference( this, superiorObjectClass );
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
     * Add some allowed AttributeType
     *
     * @param oids The attributeType oids
     */
    public void addMayAttributeTypeOids( String... oids )
    {
        if ( !isReadOnly )
        {
            for ( String oid : oids )
            {
                mayAttributeTypeOids.add( oid );
            }
        }
    }


    /**
     * Add some allowed AttributeTypes
     *
     * @param attributeTypes The attributeTypes
     */
    public void addMayAttributeTypes( AttributeType... attributeTypes )
    {
        if ( !isReadOnly )
        {
            for ( AttributeType attributeType : attributeTypes )
            {
                if ( !mayAttributeTypeOids.contains( attributeType.getOid() ) )
                {
                    mayAttributeTypes.add( attributeType );
                    mayAttributeTypeOids.add( attributeType.getOid() );
                }
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
     * Update the associated MAY AttributeType, even if the SchemaObject is readOnly
     *
     * @param mayAttributeTypes the list of allowed AttributeTypes
     */
    public void updateMayAttributeTypes( List<AttributeType> mayAttributeTypes )
    {
        this.mayAttributeTypes.clear();
        this.mayAttributeTypes.addAll( mayAttributeTypes );

        // update the OIDS now
        mayAttributeTypeOids.clear();

        for ( AttributeType may : mayAttributeTypes )
        {
            mayAttributeTypeOids.add( may.getOid() );
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
     * Add some required AttributeType OIDs
     *
     * @param oid The attributeType OIDs
     */
    public void addMustAttributeTypeOids( String... oids )
    {
        if ( !isReadOnly )
        {
            for ( String oid : oids )
            {
                mustAttributeTypeOids.add( oid );
            }
        }
    }


    /**
     * Add some required AttributeTypes
     *
     * @param attributeTypes The attributeTypse
     */
    public void addMustAttributeTypes( AttributeType... attributeTypes )
    {
        if ( !isReadOnly )
        {
            for ( AttributeType attributeType : attributeTypes )
            {
                if ( !mustAttributeTypeOids.contains( attributeType.getOid() ) )
                {
                    mustAttributeTypes.add( attributeType );
                    mustAttributeTypeOids.add( attributeType.getOid() );
                }
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
     * Update the associated MUST AttributeType, even if the SchemaObject is readOnly
     *
     * @param mayAttributeTypes the list of allowed AttributeTypes
     */
    public void updateMustAttributeTypes( List<AttributeType> mustAttributeTypes )
    {
        this.mustAttributeTypes.clear();
        this.mustAttributeTypes.addAll( mustAttributeTypes );

        // update the OIDS now
        mustAttributeTypeOids.clear();

        for ( AttributeType must : mustAttributeTypes )
        {
            mustAttributeTypeOids.add( must.getOid() );
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
     * Update the associated SUPERIORS ObjectClasses, even if the SchemaObject is readOnly
     * 
     * @param superiors the object classes to set
     */
    public void updateSuperiors( List<ObjectClass> superiors )
    {
        this.superiors.clear();
        this.superiors.addAll( superiors );

        // update the OIDS now
        superiorOids.clear();

        for ( ObjectClass oc : superiors )
        {
            superiorOids.add( oc.getOid() );
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
        return objectType + " " + DescriptionUtils.getDescription( this );
    }


    /**
     * Copy an ObjectClass
     */
    public ObjectClass copy()
    {
        ObjectClass copy = new ObjectClass( oid );

        // Copy the SchemaObject common data
        copy.copy( this );

        // Copy the ObjectClass type
        copy.objectClassType = objectClassType;

        // Copy the Superiors ObjectClasses OIDs
        copy.superiorOids = new ArrayList<String>();

        for ( String oid : superiorOids )
        {
            copy.superiorOids.add( oid );
        }

        // Copy the Superiors ObjectClasses ( will be empty )
        copy.superiors = new ArrayList<ObjectClass>();

        // Copy the MAY AttributeTypes OIDs
        copy.mayAttributeTypeOids = new ArrayList<String>();

        for ( String oid : mayAttributeTypeOids )
        {
            copy.mayAttributeTypeOids.add( oid );
        }

        // Copy the MAY AttributeTypes ( will be empty )
        copy.mayAttributeTypes = new ArrayList<AttributeType>();

        // Copy the MUST AttributeTypes OIDs
        copy.mustAttributeTypeOids = new ArrayList<String>();

        for ( String oid : mustAttributeTypeOids )
        {
            copy.mustAttributeTypeOids.add( oid );
        }

        // Copy the MUST AttributeTypes ( will be empty )
        copy.mustAttributeTypes = new ArrayList<AttributeType>();

        return copy;
    }


    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        if ( !( o instanceof ObjectClass ) )
        {
            return false;
        }

        ObjectClass that = ( ObjectClass ) o;

        // The ObjectClassType
        if ( objectClassType != that.objectClassType )
        {
            return false;
        }

        // The Superiors OIDs
        if ( superiorOids.size() != that.superiorOids.size() )
        {
            return false;
        }

        // One way
        for ( String oid : superiorOids )
        {
            if ( !that.superiorOids.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( String oid : that.superiorOids )
        {
            if ( !superiorOids.contains( oid ) )
            {
                return false;
            }
        }

        // The Superiors
        if ( superiors.size() != that.superiors.size() )
        {
            return false;
        }

        // One way
        for ( ObjectClass oid : superiors )
        {
            if ( !that.superiors.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( ObjectClass oid : that.superiors )
        {
            if ( !superiors.contains( oid ) )
            {
                return false;
            }
        }

        // The MAY OIDs
        if ( mayAttributeTypeOids.size() != that.mayAttributeTypeOids.size() )
        {
            return false;
        }

        // One way
        for ( String oid : mayAttributeTypeOids )
        {
            if ( !that.mayAttributeTypeOids.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( String oid : that.mayAttributeTypeOids )
        {
            if ( !mayAttributeTypeOids.contains( oid ) )
            {
                return false;
            }
        }

        // The MAY
        if ( mayAttributeTypes.size() != that.mayAttributeTypes.size() )
        {
            return false;
        }

        // One way
        for ( AttributeType oid : mayAttributeTypes )
        {
            if ( !that.mayAttributeTypes.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( AttributeType oid : that.mayAttributeTypes )
        {
            if ( !mayAttributeTypes.contains( oid ) )
            {
                return false;
            }
        }

        // The MUST OIDs
        if ( mustAttributeTypeOids.size() != that.mustAttributeTypeOids.size() )
        {
            return false;
        }

        // One way
        for ( String oid : mustAttributeTypeOids )
        {
            if ( !that.mustAttributeTypeOids.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( String oid : that.mustAttributeTypeOids )
        {
            if ( !mustAttributeTypeOids.contains( oid ) )
            {
                return false;
            }
        }

        // The MUST
        if ( mustAttributeTypes.size() != that.mustAttributeTypes.size() )
        {
            return false;
        }

        // One way
        for ( AttributeType oid : mustAttributeTypes )
        {
            if ( !that.mustAttributeTypes.contains( oid ) )
            {
                return false;
            }
        }

        // The other way
        for ( AttributeType oid : that.mustAttributeTypes )
        {
            if ( !mustAttributeTypes.contains( oid ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        // Clear the common elements
        super.clear();

        // Clear the references
        mayAttributeTypes.clear();
        mayAttributeTypeOids.clear();
        mustAttributeTypes.clear();
        mustAttributeTypeOids.clear();
        superiors.clear();
        superiorOids.clear();
    }
}