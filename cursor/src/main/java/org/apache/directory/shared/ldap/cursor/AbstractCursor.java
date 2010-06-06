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
package org.apache.directory.shared.ldap.cursor;


import java.util.Iterator;

import org.apache.directory.shared.i18n.I18n;


/**
 * Simple class that contains often used Cursor code.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractCursor<E> implements Cursor<E>
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
            throw new IllegalArgumentException( I18n.err( I18n.ERR_02001 ) );
        }
        
        this.monitor = monitor;
    }
    

    /**
     * Check that the cursor is not closed.
     * 
     * @param operation
     * @throws Exception
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
    public void close( Exception cause ) throws Exception
    {
        monitor.close( cause );
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
    public Iterator<E> iterator()
    {
        return new CursorIterator<E>( this );
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
