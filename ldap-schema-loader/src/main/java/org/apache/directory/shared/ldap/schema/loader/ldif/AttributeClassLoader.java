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
package org.apache.directory.shared.ldap.schema.loader.ldif;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;


/**
 * A class loader that loads classes from an attribute within an entry.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeClassLoader extends ClassLoader
{
    public EntryAttribute attribute;
    

    public AttributeClassLoader()
    {
        super( AttributeClassLoader.class.getClassLoader() );
    }
    
    
    public void setAttribute( EntryAttribute attribute ) throws LdapException
    {
        if ( attribute.isHR() )
        {
            throw new LdapInvalidAttributeValueException( ResultCodeEnum.CONSTRAINT_VIOLATION, I18n.err( I18n.ERR_10001 ) );
        }
        
        this.attribute = attribute;
    }

    
    public Class<?> findClass( String name ) throws ClassNotFoundException
    {
        byte[] classBytes = null;
        
        Value<?> value = attribute.get();
        
        if ( value.isBinary() )
        {
            classBytes = value.getBytes();

            return defineClass( name, classBytes, 0, classBytes.length );
        }
        else
        {
            throw new ClassNotFoundException( I18n.err( I18n.ERR_10002 ) );
        }
    }
}
