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
package org.apache.directory.shared.ldap.model.schema.comparators;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.schema.LdapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Compares two objects taking into account that one might be a Comparable.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @param <T> the type, must extend {@link Comparable}
 */
public class ComparableComparator<T> extends LdapComparator<Comparable<T>>
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( ComparableComparator.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;


    /**
     * The ComparableComparator constructor.
     *
     * @param oid the comparator OID
     */
    public ComparableComparator( String oid )
    {
        super( oid );
    }


    /**
     * Compares two objects taking into account that one may be a Comparable. If
     * the first is a comparable then its compareTo operation is called and the
     * result returned as is. If the first is not a Comparable but the second is
     * then its compareTo method is called and the result is returned after
     * being negated. If none are comparables the hashCode of o1 minus the
     * hashCode of o2 is returned.
     *
     * @param o1 the first comparable
     * @param o2 the second comparable
     * @return {@inheritDoc}
     */
    public int compare( Comparable<T> o1, Comparable<T> o2 )
    {
        LOG.debug( "comparing objects '{}' with '{}'", o1, o2 );

        if ( ( o1 == null ) && ( o2 == null ) )
        {
            return 0;
        }

        if ( o1 instanceof Comparable<?> )
        {
            if ( o2 == null )
            {
                return -1;
            }
            else
            {
                // TODO: check type parameter
                return o1.compareTo( ( T ) o2 );
            }
        }

        if ( o2 == null )
        {
            return 1;
        }
        else if ( o2 instanceof Comparable<?> )
        {
            if ( o1 == null )
            {
                return -1;
            }
            else
            {
                // TODO: check type parameter
                return -o2.compareTo( ( T ) o1 );
            }
        }

        // before https://issues.apache.org/jira/browse/DIRSERVER-928 it was
        // return o1.hashCode() - o2.hashCode();

        // now we will blow a stack trace if none of the objects are Comparable
        throw new IllegalArgumentException( I18n.err( I18n.ERR_04217, o1, o2 ) );
    }
}
