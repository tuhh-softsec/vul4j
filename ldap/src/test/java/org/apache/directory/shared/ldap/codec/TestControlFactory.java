/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.Control;


public class TestControlFactory implements IControlFactory<ITestControl,ITestCodecControl>
{
    public String getOid()
    {
        return ITestControl.OID;
    }

    public ITestControl newControl()
    {
        return new TestCodecControl();
    }

    public ITestCodecControl newCodecControl()
    {
        return new TestCodecControl();
    }

    public ITestCodecControl decorate( Control control )
    {
        return null;
    }

    public ITestCodecControl decorate( ITestControl control )
    {
        return null;
    }

    public javax.naming.ldap.Control toJndiControl( ITestControl control ) throws EncoderException
    {
        return null;
    }

    public ITestControl fromJndiControl( javax.naming.ldap.Control control ) throws DecoderException
    {
        return null;
    }
}