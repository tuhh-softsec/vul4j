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
package org.apache.directory.shared.ldap.message.decorators;


import org.apache.directory.shared.ldap.model.message.ModifyRequest;

import java.util.LinkedList;
import java.util.List;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyRequestDecorator extends MessageDecorator
{

    /** The modify request length */
    private int modifyRequestLength;

    /** The changes length */
    private int changesLength;

    /** The list of all change lengths */
    private List<Integer> changeLength = new LinkedList<Integer>();

    /** The list of all the modification lengths */
    private List<Integer> modificationLength = new LinkedList<Integer>();

    /** The list of all the value lengths */
    private List<Integer> valuesLength = new LinkedList<Integer>();


    /**
     * Makes a ModifyRequest encodable.
     *
     * @param decoratedMessage the decorated ModifyRequest
     */
    public ModifyRequestDecorator( ModifyRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    public ModifyRequest getModifyRequest()
    {
        return ( ModifyRequest ) getMessage();
    }


    /**
     * @param modifyRequestLength The encoded ModifyRequest's length
     */
    public void setModifyRequestLength( int modifyRequestLength )
    {
        this.modifyRequestLength = modifyRequestLength;
    }


    /**
     * @return The encoded length
     */
    public int getModifyRequestLength()
    {
        return modifyRequestLength;
    }


    /**
     * @param changesLength The encoded Changes length
     */
    public void setChangesLength( int changesLength )
    {
        this.changesLength = changesLength;
    }


    /**
     * @return The encoded length
     */
    public int getChangesLength()
    {
        return changesLength;
    }


    /**
     * @return The list of encoded Change length
     */
    public void setChangeLength( List<Integer> changeLength )
    {
        this.changeLength = changeLength;
    }


    /**
     * @return The list of encoded Change length
     */
    public List<Integer> getChangeLength()
    {
        return changeLength;
    }


    /**
     * @param modificationLength The list of encoded Modification length
     */
    public void setModificationLength( List<Integer> modificationLength )
    {
        this.modificationLength = modificationLength;
    }


    /**
     * @return The list of encoded Modification length
     */
    public List<Integer> getModificationLength()
    {
        return modificationLength;
    }


    /**
     * @param valuesLength The list of encoded Values length
     */
    public void setValuesLength( List<Integer> valuesLength )
    {
        this.valuesLength = valuesLength;
    }


    /**
     * @return The list of encoded Values length
     */
    public List<Integer> getValuesLength()
    {
        return valuesLength;
    }
}
