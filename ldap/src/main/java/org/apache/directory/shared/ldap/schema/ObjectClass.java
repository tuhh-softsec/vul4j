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


import java.util.List;

import javax.naming.NamingException;


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
    /**
     * Gets the superclasses of this ObjectClass.
     * 
     * @return the superclasses
     * @throws NamingException
     *             if there is a failure resolving the object
     */
    ObjectClass[] getSuperClasses() throws NamingException;


    /**
     * Gets the type of this ObjectClass as a type safe enum.
     * 
     * @return the ObjectClass type as an enum
     */
    ObjectClassTypeEnum getType();
    
    
    /**
     * Set the ObjectClass type, one of ABSTRACT, AUXILIARY or STRUCTURAL.
     * 
     * @param type The ObjectClassType value
     */
    void setType( ObjectClassTypeEnum type );
    
    
    /**
     * Tells if the current ObjectClass is STRUCTURAL
     * 
     * @return <code>true</code> if the ObjectClass is STRUCTURAL
     */
    boolean isStructural();
    

    /**
     * Tells if the current ObjectClass is ABSTRACT
     * 
     * @return <code>true</code> if the ObjectClass is ABSTRACT
     */
    boolean isAbstract();
    

    /**
     * Tells if the current ObjectClass is AUXILIARY
     * 
     * @return <code>true</code> if the ObjectClass is AUXILIARY
     */
    boolean isAuxiliary();


    /**
     * Gets the AttributeTypes OIDs whose attributes must be present within an entry
     * of this ObjectClass.
     * 
     * @return the OIDs of attributes that must be within entries of
     *         this ObjectClass
     * @throws NamingException if there is a failure resolving the object
     */
    List<String> getMustOids() throws NamingException;


    /**
     * Gets the AttributeTypes whose attributes must be present within an entry
     * of this ObjectClass.
     * 
     * @return the AttributeTypes that must be within entries of
     *         this ObjectClass
     * @throws NamingException if there is a failure resolving the object
     */
    List<AttributeType> getMustATs() throws NamingException;
    
    
    /**
     * Sets the attributeTypes' OID which must be present within an entry
     * of this ObjectClass
     * 
     * @param oids The list of OIDs representing the MUST AttributeTypes
     * @throws NamingException If the addition failed
     */
    void setMustOids( List<String> oids ) throws NamingException;


    /**
     * Gets the AttributeType OIDs whose attributes may be present within an entry
     * of this ObjectClass.
     * 
     * @return the OIDs of attributes that may be within entries of
     *         this ObjectClass
     * @throws NamingException
     *             if there is a failure resolving the object
     */
    List<String> getMayOids() throws NamingException;


    /**
     * Gets the AttributeType whose attributes may be present within an entry
     * of this ObjectClass.
     * 
     * @return the AttributeTypes that may be within entries of
     *         this ObjectClass
     * @throws NamingException
     *             if there is a failure resolving the object
     */
    List<AttributeType> getMayATs() throws NamingException;
    
    
    /**
     * Sets the attributeTypes' OID which may be present within an entry
     * of this ObjectClass
     * 
     * @param oids The list of OIDs representing the MAY AttributeTypes
     * @throws NamingException If the addition failed
     */
    void setMayOids( List<String> oids ) throws NamingException;
}