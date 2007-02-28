package org.codehaus.plexus.util.reflection;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Exception indicating that an error has occurred while instantiating a class
 * with the Reflector class. This exception is meant to put a more user-friendly
 * face on the myriad other exceptions throws during reflective object creation.
 * 
 * @author John Casey
 */
public class ReflectorException
    extends Exception
{
    /**
     * Create a new ReflectorException.
     */
    public ReflectorException()
    {
    }

    /**
     * Create a new ReflectorException with the specified message.
     * 
     * @param msg
     *            The message.
     */
    public ReflectorException( String msg )
    {
        super( msg );
    }

    /**
     * Create a new ReflectorException with the specified root cause.
     * 
     * @param root
     *            The root cause.
     */
    public ReflectorException( Throwable root )
    {
        super( root );
    }

    /**
     * Create a new ReflectorException with the specified message and root
     * cause.
     * 
     * @param msg
     *            The message.
     * @param root
     *            The root cause.
     */
    public ReflectorException( String msg, Throwable root )
    {
        super( msg, root );
    }
}