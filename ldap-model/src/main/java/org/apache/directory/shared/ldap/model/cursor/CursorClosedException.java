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


/**
 * A specific form of IOException to note that an operation is being
 * attempted on a closed Cursor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CursorClosedException extends Exception
{
    /** The serialVersion UID */
    private static final long serialVersionUID = -5723233489761854394L;

    /** A static exception to be used by the monitor */
    public static final CursorClosedException INSTANCE = new CursorClosedException();


    /**
     * Creates a new instance of CursorClosedException.
     */
    public CursorClosedException()
    {
    }


    /**
     * Creates a new instance of CursorClosedException.
     *
     * @param message The associated message
     */
    public CursorClosedException( String message )
    {
        super( message );
    }
}
