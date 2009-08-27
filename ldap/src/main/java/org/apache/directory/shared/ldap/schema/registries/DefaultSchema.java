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
package org.apache.directory.shared.ldap.schema.registries;

import java.util.Arrays;



/**
 * The default Schema interface implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DefaultSchema implements Schema
{
    private static final String[] NONE = new String[0];
    private static final String DEFAULT_OWNER = "uid=admin,ou=system";
    
    private boolean disabled;
    private String[] dependencies;
    private String owner;
    private String name;
    
    
    public DefaultSchema( String name )
    {
        this( name, null, null, false );
    }
    
        
    public DefaultSchema( String name, String owner )
    {
        this( name, owner, null, false );
    }
    
        
    public DefaultSchema( String name, String owner, String[] dependencies )
    {
        this( name, owner, dependencies, false );
    }
    
        
    public DefaultSchema( String name, String owner, String[] dependencies, boolean disabled )
    {
        if ( name == null )
        {
            throw new NullPointerException( "name cannot be null" );
        }
        
        this.name = name;
        
        if ( owner != null )
        {
            this.owner = owner;
        }
        else
        {
            this.owner = DEFAULT_OWNER;
        }
        
        if ( dependencies != null )
        {
            this.dependencies = dependencies;
        }
        else
        {
            this.dependencies = NONE;
        }
        
        this.disabled = disabled;
    }


    public String[] getDependencies()
    {
        String[] copy = new String[dependencies.length];
        System.arraycopy( dependencies, 0, copy, 0, dependencies.length );
        return copy;
    }

    
    public String getOwner()
    {
        return owner;
    }

    
    public String getSchemaName()
    {
        return name;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean isDisabled()
    {
        return disabled;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return !disabled;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void disable()
    {
        this.disabled = true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void enable()
    {
        this.disabled = false;
    }
    
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "\tSchema Name: " );
        sb.append( this.name );
        sb.append( "\n\t\tDisabled: " );
        sb.append( this.disabled );
        sb.append( "\n\t\tOwner: " );
        sb.append( this.owner );
        sb.append( "\n\t\tDependencies: " );
        sb.append( Arrays.toString( dependencies ) );
        return sb.toString();
    }
}
