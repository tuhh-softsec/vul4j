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
package org.apache.directory.shared.ldap.model.cursor;


import java.util.Iterator;

import org.apache.directory.shared.i18n.I18n;


/**
 * An abstract TupleCursor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @param <K> The key type for the Tuple
 * @param <V> The associated Value type
 */
public abstract class AbstractTupleCursor<K, V> implements TupleCursor<K, V>
{
    /** The default associated monitor */
    private ClosureMonitor monitor = new DefaultClosureMonitor();


    /**
     * {@inheritDoc}
     */
    public final void setClosureMonitor( ClosureMonitor monitor )
    {
        if ( monitor == null )
        {
            throw new IllegalArgumentException( "monitor" );
        }

        this.monitor = monitor;
    }


    /**
     * Check if the cursor is closed for the given operation
     *
     * @param operation The operation that will be applied if the cursor is not closed
     * @throws Exception If there is an issue
     */
    protected final void checkNotClosed( String operation ) throws Exception
    {
        monitor.checkNotClosed();
    }


    /**
     * {@inheritDoc}
     */
    public final boolean isClosed()
    {
        return monitor.isClosed();
    }


    /**
     * {@inheritDoc}
     */
    public void close() throws Exception
    {
        monitor.close();
    }


    /**
     * {@inheritDoc}
     */
    public void close( Exception cause ) throws Exception
    {
        monitor.close( cause );
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Tuple<K, V>> iterator()
    {
        return new CursorIterator<Tuple<K, V>>( this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAfterLast() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "isAfterLast()" ) ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isBeforeFirst() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "isBeforeFirst()" ) ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFirst() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "isFirst()" ) ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isLast() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "isLast()" ) ) );
    }
}
