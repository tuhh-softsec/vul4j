/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.entry;


import java.util.List;
import java.util.Set;

import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.schema.AttributeType;


/**
 * A server side entry which is schema aware.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ServerEntry extends Entry
{
    /**
     * Tells if an entry has a specific ObjectClass Attribute
     * 
     * @param objectClass The ObjectClass we want to check
     * @return <code>true</code> if the ObjectClass value is present 
     * in the ObjectClass attribute
     */
    boolean hasObjectClass( EntryAttribute objectClass );

    
    /**
     * <p>
     * Put some new attributes using the attributeTypes. 
     * No value is inserted. 
     * </p>
     * <p>
     * If an existing Attribute is found, it will be replaced by an
     * empty attribute, and returned to the caller.
     * </p>
     * 
     * @param attributeTypes The AttributeTypes to add.
     * @return A list of replaced Attributes, of <code>null</code> if no attribute are removed.
     */
    List<EntryAttribute> set( AttributeType... attributeTypes );


    /**
     * A clone method to produce a clone of the current object
     */
    Entry clone();
    
    
    /**
     * Convert the ServerEntry to a ClientEntry
     *
     * @return An instance of ClientEntry
     */
    Entry toClientEntry() throws LdapException;
}
