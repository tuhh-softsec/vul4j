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
package org.apache.directory.shared.ldap.model.cursor;


import java.util.Comparator;

import org.apache.directory.shared.i18n.I18n;


/**
 * A Cursor over a single element.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @param <E> The type of element on which this cursor will iterate
 */
public class SingletonCursor<E> extends AbstractCursor<E>
{
    /** A flag to tell if the cursor is set before the first element */
    private boolean beforeFirst = true;

    /** A flag to tell if the cursor is set after the last element */
    private boolean afterLast;

    /** A flag to tell if the cursor is on the element */
    private boolean onSingleton;

    /** The comparator used for this cursor. */
    private final Comparator<E> comparator;

    /** The unique element stored in the cursor */
    private final E singleton;


    /**
     * Creates a new instance of SingletonCursor.
     *
     * @param singleton The unique element to store into this cursor
     */
    public SingletonCursor( E singleton )
    {
        this( singleton, null );
    }


    /**
     * Creates a new instance of SingletonCursor, with its associated
     * comparator
     *
     * @param singleton The unique element to store into this cursor
     * @param comparator The associated comparator
     */
    public SingletonCursor( E singleton, Comparator<E> comparator )
    {
        this.singleton = singleton;
        this.comparator = comparator;
    }


    /**
     * {@inheritDoc}
     */
    public boolean available()
    {
        return onSingleton;
    }


    /**
     * {@inheritDoc}
     */
    public void before( E element ) throws Exception
    {
        checkNotClosed( "before()" );

        if ( comparator == null )
        {
            throw new UnsupportedOperationException( I18n.err( I18n.ERR_02010_NO_COMPARATOR_CANT_MOVE_BEFORE ) );
        }

        int comparison = comparator.compare( singleton, element );

        if ( comparison < 0 )
        {
            first();
        }
        else
        {
            beforeFirst();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void after( E element ) throws Exception
    {
        checkNotClosed( "after()" );

        if ( comparator == null )
        {
            throw new UnsupportedOperationException( I18n.err( I18n.ERR_02011_NO_COMPARATOR_CANT_MOVE_AFTER ) );
        }

        int comparison = comparator.compare( singleton, element );

        if ( comparison > 0 )
        {
            first();
        }
        else
        {
            afterLast();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void beforeFirst() throws Exception
    {
        checkNotClosed( "beforeFirst" );
        beforeFirst = true;
        afterLast = false;
        onSingleton = false;
    }


    /**
     * {@inheritDoc}
     */
    public void afterLast() throws Exception
    {
        checkNotClosed( "afterLast" );
        beforeFirst = false;
        afterLast = true;
        onSingleton = false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean first() throws Exception
    {
        checkNotClosed( "first" );
        beforeFirst = false;
        onSingleton = true;
        afterLast = false;

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean last() throws Exception
    {
        checkNotClosed( "last" );
        beforeFirst = false;
        onSingleton = true;
        afterLast = false;

        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirst() throws Exception
    {
        checkNotClosed( "isFirst" );

        return onSingleton;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLast() throws Exception
    {
        checkNotClosed( "isLast" );

        return onSingleton;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAfterLast() throws Exception
    {
        checkNotClosed( "isAfterLast" );

        return afterLast;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBeforeFirst() throws Exception
    {
        checkNotClosed( "isBeforeFirst" );

        return beforeFirst;
    }


    /**
     * {@inheritDoc}
     */
    public boolean previous() throws Exception
    {
        checkNotClosed( "previous" );

        if ( beforeFirst )
        {
            return false;
        }

        if ( afterLast )
        {
            beforeFirst = false;
            onSingleton = true;
            afterLast = false;

            return true;
        }

        // must be on the singleton
        beforeFirst = true;
        onSingleton = false;
        afterLast = false;

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean next() throws Exception
    {
        checkNotClosed( "next" );

        if ( beforeFirst )
        {
            beforeFirst = false;
            onSingleton = true;
            afterLast = false;

            return true;
        }

        if ( afterLast )
        {
            return false;
        }

        // must be on the singleton
        beforeFirst = false;
        onSingleton = false;
        afterLast = true;

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public E get() throws Exception
    {
        checkNotClosed( "get" );

        if ( onSingleton )
        {
            return singleton;
        }

        if ( beforeFirst )
        {
            throw new InvalidCursorPositionException( I18n.err( I18n.ERR_02012_CANNOT_ACCESS_IF_BEFORE_FIRST ) );
        }
        else
        {
            throw new InvalidCursorPositionException( I18n.err( I18n.ERR_02013_CANNOT_ACCESS_IF_AFTER_LAST ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isElementReused()
    {
        return true;
    }
}
