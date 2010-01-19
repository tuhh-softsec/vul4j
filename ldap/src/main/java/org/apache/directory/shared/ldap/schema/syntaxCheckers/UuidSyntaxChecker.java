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
package org.apache.directory.shared.ldap.schema.syntaxCheckers;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An UUID syntax checker.
 * 
 * UUID ::= OCTET STRING (SIZE(16)) -- constrained to an UUID [RFC4122]
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 736240 $
 */
public class UuidSyntaxChecker extends SyntaxChecker
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( UuidSyntaxChecker.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    private static final String UUID_REGEX = "^[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}$";
    
    /**
     * Creates a new instance of UUIDSyntaxChecker.
     */
    public UuidSyntaxChecker()
    {
        super( SchemaConstants.UUID_SYNTAX );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isValidSyntax( Object value )
    {
        if ( value == null )
        {
            LOG.debug( "Syntax invalid for '{}'", value );
            return false;
        }
 
        if ( ! ( value instanceof String ) )
        {
            LOG.debug( "Syntax invalid for '{}'", value );
            return false;
        }

        String uuid = ( String ) value;
        if ( ! uuid.matches( UUID_REGEX ) )
        {
            LOG.debug( "Syntax invalid for '{}'", value );
            return false;
        }
        
        // There is not that much more we can check.
        LOG.debug( "Syntax valid for '{}'", value );
        return true;
    }
}
