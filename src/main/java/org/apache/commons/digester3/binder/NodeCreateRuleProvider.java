package org.apache.commons.digester3.binder;

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
 * Builder chained when invoking {@link LinkedRuleBuilder#createNode()}.
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

    /**
     * {@link NodeCreateRule} instance will be created either a DOM {@link org.w3c.dom.Element Element}
     * or a DOM {@link org.w3c.dom.DocumentFragment DocumentFragment}, depending on the value of the
     * <code>nodeType</code> parameter.
     *
     * @param nodeType the type of node to create, which can be either
     *                 {@link org.w3c.dom.Node#ELEMENT_NODE Node.ELEMENT_NODE} or
     *                 {@link org.w3c.dom.Node#DOCUMENT_FRAGMENT_NODE Node.DOCUMENT_FRAGMENT_NODE}
     * @return this builder instance
     */
    public NodeCreateRuleProvider ofType( NodeType nodeType )
    {
        if ( nodeType == null )
        {
            reportError( "createNode().ofType( NodeType )", "Null NodeType not allowed" );
        }

        this.nodeType = nodeType;
        return this;
    }

    /**
     * {@link NodeCreateRule} instance will be created a DOM {@link org.w3c.dom.Element Element}, but
     * lets users specify the JAXP <code>DocumentBuilder</code> that should be used when constructing the node tree.
     *
     * @param documentBuilder the JAXP <code>DocumentBuilder</code> to use
     * @return this builder instance
     */
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
     * Enumeration that wraps admitted {@link org.w3c.dom.Node} node constants.
     */
    public enum NodeType
    {

        /**
         * @see org.w3c.dom.Node#ATTRIBUTE_NODE
         */
        ATTRIBUTE( ATTRIBUTE_NODE ),
        /**
         * @see org.w3c.dom.Node#CDATA_SECTION_NODE
         */
        CDATA( CDATA_SECTION_NODE ),
        /**
         * @see org.w3c.dom.Node#COMMENT_NODE
         */
        COMMENT( COMMENT_NODE ),
        /**
         * @see org.w3c.dom.Node#DOCUMENT_FRAGMENT_NODE
         */
        DOCUMENT_FRAGMENT( DOCUMENT_FRAGMENT_NODE ),
        /**
         * @see org.w3c.dom.Node#DOCUMENT_NODE
         */
        DOCUMENT( DOCUMENT_NODE ),
        /**
         * @see org.w3c.dom.Node#DOCUMENT_TYPE_NODE
         */
        DOCUMENT_TYPE( DOCUMENT_TYPE_NODE ),
        /**
         * @see org.w3c.dom.Node#ELEMENT_NODE
         */
        ELEMENT( ELEMENT_NODE ),
        /**
         * @see org.w3c.dom.Node#ENTITY_NODE
         */
        ENTITY( ENTITY_NODE ),
        /**
         * @see org.w3c.dom.Node#ENTITY_REFERENCE_NODE
         */
        ENTITY_REFERENCE( ENTITY_REFERENCE_NODE ),
        /**
         * @see org.w3c.dom.Node#NOTATION_NODE
         */
        NOTATION( NOTATION_NODE ),
        /**
         * @see org.w3c.dom.Node#PROCESSING_INSTRUCTION_NODE
         */
        PROCESSING_INSTRUCTION( PROCESSING_INSTRUCTION_NODE ),
        /**
         * @see org.w3c.dom.Node#TEXT_NODE
         */
        TEXT( TEXT_NODE );

        private final int documentType;

        private NodeType( final int documentType )
        {
            this.documentType = documentType;
        }

        private int getDocumentType()
        {
            return documentType;
        }

    }

}
