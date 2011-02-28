/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.model.filter;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Unit tests class UndefinedNode.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class UndefinedNodeTest
{

    ExprNode undefinedNode = UndefinedNode.UNDEFINED_NODE;


    @Test
    public void testIsLeaf() throws Exception
    {
        assertFalse( undefinedNode.isLeaf() );
    }


    @Test
    public void testIsSchemaAware() throws Exception
    {
        assertFalse( undefinedNode.isSchemaAware() );
    }


    @Test
    public void testAssertationType() throws Exception
    {
        assertEquals( undefinedNode.getAssertionType(), AssertionType.UNDEFINED );
    }


    @Test
    public void testAccept() throws Exception
    {
        assertNull( undefinedNode.accept( new FilterVisitor()
        {

            public Object visit( ExprNode node )
            {
                return null;
            }


            public boolean isPrefix()
            {
                return false;
            }


            public List<ExprNode> getOrder( BranchNode node, List<ExprNode> children )
            {
                return null;
            }


            public boolean canVisit( ExprNode node )
            {
                return false;
            }
        } ) );
    }

}
