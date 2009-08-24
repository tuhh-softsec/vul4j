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

package org.apache.directory.shared.ldap.schema.parsers;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;


/**
 * RFC 4512 - 4.1.7.  DIT Structure Rule Description
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DITStructureRuleDescription extends SchemaObject
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The rule ID. A DSR does not have an OID */
    private int ruleId;
    
    /** The associated NameForm */
    private String form;

    /** The list of superiors rules */
    private List<Integer> superRules;


    /**
     * Creates a new instance of DITStructureRuleDescription
     */
    public DITStructureRuleDescription( int ruleId )
    {
        super(  SchemaObjectType.DIT_STRUCTURE_RULE, null );
        this.ruleId = ruleId;
        form = null;
        superRules = new ArrayList<Integer>();
    }


    /**
     *  @return The associated NameForm's OID
     */
    public String getForm()
    {
        return form;
    }


    /**
     * Sets the associated NameForm's OID
     *
     * @param form The NameForm's OID
     */
    public void setForm( String form )
    {
        this.form = form;
    }


    /**
     * @return The Rule ID
     */
    public int getRuleId()
    {
        return ruleId;
    }


    /**
     * Sets the rule identifier of this DIT structure rule;
     *
     * @param ruleId the rule identifier of this DIT structure rule;
     */
    public void setRuleId( int ruleId )
    {
        this.ruleId = ruleId;
    }


    /**
     * @return The list of superiors RuleIDs
     */
    public List<Integer> getSuperRules()
    {
        return superRules;
    }


    /**
     * Sets the list of superior RuleIds
     * 
     * @param superRules the list of superior RuleIds
     */
    public void setSuperRules( List<Integer> superRules )
    {
        this.superRules = superRules;
    }


    /**
     * Adds a new superior RuleId
     *
     * @param superRule The superior RuleID to add
     */
    public void addSuperRule( Integer superRule )
    {
        superRules.add( superRule );
    }
    
    
    /**
     * The DSR does not have an OID, so throw an exception
     */
    public String getOid()
    {
        throw new NotImplementedException(); 
    }
}
