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
 * Thrown when a bug is detected in the plugins code.
 * <p>
 * This class is intended to be used in assertion statements, similar to the way that java 1.4's native assertion
 * mechanism is used. However there is a difference: when a java 1.4 assertion fails, an AssertionError is thrown, which
 * is a subclass of Error; here, the PluginAssertionFailure class extends RuntimeException rather than Error.
 * <p>
 * This difference in design is because throwing Error objects is not good in a container-based architecture.
 * <p>
 * Example:
 * 
 * <pre>
 * if ( impossibleCondition )
 * {
 *     throw new PluginAssertionFailure( &quot;internal error: impossible condition is true&quot; );
 * }
 * </pre>
 * <p>
 * Note that PluginAssertionFailure should <i>not</i> be thrown when user input is bad, or when code external to the
 * Digester module passes invalid parameters to a plugins method. It should be used only in checks for problems which
 * indicate internal bugs within the plugins module.
 * 
 * @since 1.6
 */
public class PluginAssertionFailure
    extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginAssertionFailure( Throwable cause )
    {
        super( cause );
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param msg describes the reason this exception is being thrown.
     */
    public PluginAssertionFailure( String msg )
    {
        super( msg );
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param msg describes the reason this exception is being thrown.
     * @param cause underlying exception that caused this to be thrown
     */
    public PluginAssertionFailure( String msg, Throwable cause )
    {
        super( msg, cause );
    }

}
