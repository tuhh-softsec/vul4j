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

package org.apache.directory.shared.dsmlv2;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.directory.shared.dsmlv2.request.Dsmlv2Grammar;
import org.apache.directory.shared.ldap.codec.standalone.StandaloneLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This class had to be used to create a Request TestCase
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractTest
{
    /** The LDAP encoder decoder service */
    private LdapCodecService codec = new StandaloneLdapCodecService();
    
    private Dsmlv2Grammar grammar = new Dsmlv2Grammar( codec );
    
    
    public Dsmlv2Parser newParser() throws Exception
    {
        return new Dsmlv2Parser( grammar );
    }
    
    
    public LdapCodecService getCodec()
    {
        return codec;
    }
    
    
    
    
    
    /**
     * Asserts that parsing throws a correct XmlPullParserException due to an incorrect file
     *
     * @param testClass
     *      the Class of the TestCase
     * @param filename
     *      the path of the xml file to parse 
     */
    public void testParsingFail( Class<?> testClass, String filename )
    {
        try
        {
            Dsmlv2Parser parser = new Dsmlv2Parser( grammar );

            parser.setInput( testClass.getResource( filename ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage(), true );
            return;
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
        fail();
    }
}
