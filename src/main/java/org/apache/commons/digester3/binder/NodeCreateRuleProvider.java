/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3.binder;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.digester3.NodeCreateRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#createNode()}.
 *
 * @since 3.0
 */
public final class NodeCreateRuleProvider
    extends AbstractBackToLinkedRuleBuilder<NodeCreateRule>
{

    private NodeType nodeType = NodeType.ELEMENT;

    private DocumentBuilder documentBuilder;

    NodeCreateRuleProvider( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                            LinkedRuleBuilder mainBuilder )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
    }

    public NodeCreateRuleProvider ofType( NodeType nodeType )
    {
        if ( nodeType == null )
        {
            reportError( "createNode().ofType( NodeType )", "Null NodeType not allowed" );
        }

        this.nodeType = nodeType;
        return this;
    }

    public NodeCreateRuleProvider usingDocumentBuilder( DocumentBuilder documentBuilder )
    {
        this.documentBuilder = documentBuilder;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeCreateRule createRule()
    {
        if ( documentBuilder == null )
        {
            try
            {
                return new NodeCreateRule( nodeType.getDocumentType() );
            }
            catch ( ParserConfigurationException e )
            {
                throw new RuntimeException( e );
            }
        }

        return new NodeCreateRule( nodeType.getDocumentType(), documentBuilder );
    }

    /**
     * 
     */
    public enum NodeType
    {

        ATTRIBUTE(ATTRIBUTE_NODE),
        CDATA(CDATA_SECTION_NODE),
        COMMENT(COMMENT_NODE),
        DOCUMENT_FRAGMENT(DOCUMENT_FRAGMENT_NODE),
        DOCUMENT(DOCUMENT_NODE),
        DOCUMENT_TYPE(DOCUMENT_TYPE_NODE),
        ELEMENT(ELEMENT_NODE),
        ENTITY(ENTITY_NODE),
        ENTITY_REFERENCE(ENTITY_REFERENCE_NODE),
        NOTATION(NOTATION_NODE),
        PROCESSING_INSTRUCTION(PROCESSING_INSTRUCTION_NODE),
        TEXT(TEXT_NODE);

        private final int documentType;

        private NodeType(final int documentType)
        {
            this.documentType = documentType;
        }

        int getDocumentType()
        {
            return documentType;
        }

    }

}
