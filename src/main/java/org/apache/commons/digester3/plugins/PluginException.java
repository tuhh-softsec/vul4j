package org.apache.commons.digester3.plugins;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Thrown when some plugin-related error has occurred, and none of the other exception types are appropriate.
 * 
 * @since 1.6
 */
public class PluginException
    extends Exception
{

    private static final long serialVersionUID = 1L;

    private Throwable cause = null;

    /**
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginException( Throwable cause )
    {
        this( cause.getMessage() );
        this.cause = cause;
    }

    /**
     * @param msg describes the reason this exception is being thrown.
     */
    public PluginException( String msg )
    {
        super( msg );
    }

    /**
     * @param msg describes the reason this exception is being thrown.
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginException( String msg, Throwable cause )
    {
        this( msg );
        this.cause = cause;
    }

    /**
     * @return the underlying exception that caused this to be thrown
     */
    @Override
    public Throwable getCause()
    {
        return cause;
    }

}
