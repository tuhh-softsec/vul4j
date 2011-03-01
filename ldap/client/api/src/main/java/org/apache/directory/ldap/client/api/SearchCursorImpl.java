/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.ldap.client.api;


import java.util.concurrent.TimeUnit;

import org.apache.directory.ldap.client.api.future.SearchFuture;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.cursor.AbstractCursor;
import org.apache.directory.shared.ldap.model.cursor.InvalidCursorPositionException;
import org.apache.directory.shared.ldap.model.cursor.SearchCursor;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;


/**
 * An implementation of Cursor based on the underlying SearchFuture instance.
 * 
 * Note: This is a forward only cursor hence the only valid operations are next(), get() and close() 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchCursorImpl extends AbstractCursor<Response> implements SearchCursor
{
    /** the search future */
    private SearchFuture future;

    /** wait time while polling for a SearchResponse */
    private long timeout;

    /** time units of timeout value */
    private TimeUnit timeUnit;

    /** a reference to hold the retrieved SearchResponse object from SearchFuture */
    private Response response;

    /** the done flag */
    private boolean done;

    /** a reference to hold the SearchResultDone response */
    private SearchResultDone searchDoneResp;


    /**
     * Instantiates a new search cursor.
     *
     * @param future the future
     * @param timeout the timeout
     * @param timeUnit the time unit
     */
    public SearchCursorImpl( SearchFuture future, long timeout, TimeUnit timeUnit )
    {
        this.future = future;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }


    /**
     * {@inheritDoc}
     */
    public boolean next() throws Exception
    {
        if ( done )
        {
            return false;
        }

        try
        {
            if ( future.isCancelled() )
            {
                response = null;
                done = true;
                return false;
            }

            response = future.get( timeout, timeUnit );
        }
        catch ( Exception e )
        {
            LdapException ldapException = new LdapException( LdapNetworkConnection.NO_RESPONSE_ERROR );
            ldapException.initCause( e );

            // Send an abandon request
            if ( !future.isCancelled() )
            {
                future.cancel( true );
            }

            // close the cursor
            close( ldapException );

            throw ldapException;
        }

        if ( response == null )
        {
            future.cancel( true );

            throw new LdapException( LdapNetworkConnection.TIME_OUT_ERROR );
        }

        done = ( response instanceof SearchResultDone );

        if ( done )
        {
            searchDoneResp = ( SearchResultDone ) response;
            response = null;
        }

        return !done;
    }


    /**
     * {@inheritDoc}
     */
    public Response get() throws Exception
    {
        if ( !available() )
        {
            throw new InvalidCursorPositionException();
        }

        return response;
    }


    /**
     * {@inheritDoc}
     */
    public SearchResultDone getSearchResultDone()
    {
        return searchDoneResp;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isElementReused()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean available()
    {
        return response != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception
    {
        close( null );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close( Exception cause ) throws Exception
    {
        if ( done )
        {
            super.close();
            return;
        }

        if ( !future.isCancelled() )
        {
            future.cancel( true );
        }

        if ( cause != null )
        {
            super.close( cause );
        }
        else
        {
            super.close();
        }
    }


    // rest of all operations will throw UnsupportedOperationException

    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public void after( Response element ) throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "after( Response element )" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public void afterLast() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "afterLast()" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public void before( Response element ) throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "before( Response element )" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public void beforeFirst() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "beforeFirst()" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public boolean first() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "first()" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public boolean last() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "last()" ) ) );
    }


    /**
     * This operation is not supported in SearchCursor.
     * {@inheritDoc}
     */
    public boolean previous() throws Exception
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_02014_UNSUPPORTED_OPERATION, getClass().getName()
            .concat( "." ).concat( "previous()" ) ) );
    }

}
