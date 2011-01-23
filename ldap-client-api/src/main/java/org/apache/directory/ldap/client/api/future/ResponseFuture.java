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

package org.apache.directory.ldap.client.api.future;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.shared.ldap.model.message.Response;


/**
 * A Future implementation used in LdapConnection operations.
 *
 * @param <R> The result type returned by this Future's <tt>get</tt> method
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ResponseFuture<R extends Response> implements Future<Response>
{
    /** the blocking queue holding LDAP responses */
    protected BlockingQueue<R> queue;

    /** flag to determine if this future is cancelled */
    protected boolean cancelled = false;

    /** If the request has been cancelled because of an exception  it will be stored here */
    protected Throwable cause;

    /** The messageID for this future */
    protected int messageId;

    /** The connection used by the request */
    protected LdapConnection connection;


    /**
     * Creates a new instance of ResponseFuture.
     *
     * @param connection The LdapConnection used by the request
     * @param messageId The associated message ID
     */
    public ResponseFuture( LdapConnection connection, int messageId )
    {
        queue = new LinkedBlockingQueue<R>();
        this.messageId = messageId;
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     */
    public boolean cancel( boolean mayInterruptIfRunning )
    {
        if ( cancelled )
        {
            return cancelled;
        }

        // set the cancel flag first
        cancelled = true;

        // Send an abandonRequest only if this future exists
        if ( connection.doesFutureExistFor( messageId ) )
        {
            connection.abandon( messageId );
        }

        // then clear the queue, cause the might be some incoming messages before this abandon request
        // hits the server
        queue.clear();

        return cancelled;
    }


    /**
     * {@inheritDoc}
     * @throws InterruptedException if the operation has been cancelled by client
     */
    public R get() throws InterruptedException, ExecutionException
    {
        R response = null;

        response = queue.take();

        return response;
    }


    /**
     * {@inheritDoc}
     * @throws InterruptedException if the operation has been cancelled by client
     */
    public void set( R response ) throws InterruptedException, ExecutionException
    {
        queue.add( response );
    }


    /**
     * {@inheritDoc}
     * @throws InterruptedException if the operation has been cancelled by client
     */
    public R get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
    {
        R response = queue.poll( timeout, unit );

        return response;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCancelled()
    {
        return cancelled;
    }


    /**
     * This operation is not supported in this implementation of Future.
     * 
     * {@inheritDoc}
     */
    public boolean isDone()
    {
        throw new UnsupportedOperationException( "Operation not supported" );
    }


    /**
     * @return the cause
     */
    public Throwable getCause()
    {
        return cause;
    }


    /**
     * Associate a cause to the ResponseFuture
     * @param cause the cause to set
     */
    public void setCause( Throwable cause )
    {
        this.cause = cause;
    }


    /**
     * Cancel the Future
     *
     */
    public void cancel()
    {
        // set the cancel flag first
        cancelled = true;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "[msgId : " ).append( messageId ).append( ", " );
        sb.append( "size : " ).append( queue.size() ).append( ", " );
        sb.append( "Canceled :" ).append( cancelled ).append( "]" );

        return sb.toString();
    }
}
