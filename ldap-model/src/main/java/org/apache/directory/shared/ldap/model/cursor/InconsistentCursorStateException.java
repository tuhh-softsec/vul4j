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
 * Thrown to indicate a condition in the Cursor where the state seems
 * inconsistent based on internal accounting.  This may indicate the
 * underlying structure has changed after the Cursor has been created.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InconsistentCursorStateException extends Exception
{
    /** The serialVersion UID */
    private static final long serialVersionUID = 6222645005251534704L;


    /**
     * Creates a new instance of InconsistentCursorStateException.
     */
    public InconsistentCursorStateException()
    {
    }


    /**
     * Creates a new instance of CursorClosedException.
     *
     * @param message The associated message
     */
    public InconsistentCursorStateException( String message )
    {
        super( message );
    }
}
