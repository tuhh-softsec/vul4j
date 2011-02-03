/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.model.message.controls;


/**
 * Simple ManageDsaIT implementation class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ManageDsaITImpl extends AbstractControl implements ManageDsaIT
{
    /**
     * Default constructor.
     */
    public ManageDsaITImpl()
    {
        super( OID );
    }


    /**
     * Creates instance and sets criticality at same time.
     * 
     * @param isCritical true if critical, false otherwise
     */
    public ManageDsaITImpl( boolean isCritical )
    {
        super( OID );
        setCritical( isCritical );
    }
}