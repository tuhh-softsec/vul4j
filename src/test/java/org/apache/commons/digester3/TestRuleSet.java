/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.digester3;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RuleSetBase;

/**
 * RuleSet that mimics the rules set used for Employee and Address creation, optionally associated with a particular
 * namespace URI.
 * 
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TestRuleSet
    extends RuleSetBase
{

    // ----------------------------------------------------------- Constructors

    /**
     * Construct an instance of this RuleSet with default values.
     */
    public TestRuleSet()
    {

        this( null, null );

    }

    /**
     * Construct an instance of this RuleSet associated with the specified prefix, associated with no namespace URI.
     * 
     * @param prefix Matching pattern prefix (must end with '/') or null.
     */
    public TestRuleSet( String prefix )
    {

        this( prefix, null );

    }

    /**
     * Construct an instance of this RuleSet associated with the specified prefix and namespace URI.
     * 
     * @param prefix Matching pattern prefix (must end with '/') or null.
     * @param namespaceURI The namespace URI these rules belong to
     */
    public TestRuleSet( String prefix, String namespaceURI )
    {

        super(namespaceURI);
        if ( prefix == null )
        {
            this.prefix = "";
        }
        else
        {
            this.prefix = prefix;
        }

    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The prefix for each matching pattern added to the Digester instance, or an empty String for no prefix.
     */
    protected String prefix = null;

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public void addRuleInstances( Digester digester )
    {

        digester.addObjectCreate( prefix + "employee", Employee.class );
        digester.addSetProperties( prefix + "employee" );
        digester.addObjectCreate( "employee/address", "org.apache.commons.digester3.Address" );
        digester.addSetProperties( prefix + "employee/address" );
        digester.addSetNext( prefix + "employee/address", "addAddress" );

    }

}
