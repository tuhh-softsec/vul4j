/*
 *
 * Copyright (c) 2004 John Dennis Casey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */
/*

 Copyright (c) 2002 John Casey. All rights reserved.

 SEE licenses/cj-license.txt FOR MORE INFORMATION.

 */
/*
 * ReflectorException.java
 *
 * Created on November 1, 2002, 7:33 AM
 */
package org.codehaus.plexus.util.reflection;

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