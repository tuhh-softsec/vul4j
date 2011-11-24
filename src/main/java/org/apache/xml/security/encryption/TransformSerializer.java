/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.encryption;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * Converts <code>String</code>s into <code>Node</code>s and visa versa. This requires Xalan to 
 * work properly.
 */
public class TransformSerializer extends AbstractSerializer {
    
    private TransformerFactory transformerFactory;
    
    /**
     * @param source
     * @param ctx
     * @return the Node resulting from the parse of the source
     * @throws XMLEncryptionException
     */
    public Node deserialize(String source, Node ctx) throws XMLEncryptionException {
        String fragment = createContext(source, ctx);
        
        try {
            Document contextDocument = null;
            if (Node.DOCUMENT_NODE == ctx.getNodeType()) {
                contextDocument = (Document)ctx;
            } else {
                contextDocument = ctx.getOwnerDocument();
            }
            Source src = new StreamSource(new StringReader(fragment));
            
            if (transformerFactory == null) {
                transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            }
            Transformer transformer = transformerFactory.newTransformer();
            
            DOMResult res = new DOMResult();

            Node placeholder = contextDocument.createDocumentFragment();
            res.setNode(placeholder);

            transformer.transform(src, res);

            // Skip dummy element
            Node dummyChild = placeholder.getFirstChild();
            Node child = dummyChild.getFirstChild();

            if (child != null && child.getNextSibling() == null) {
                return child;
            }

            DocumentFragment docfrag = contextDocument.createDocumentFragment();
            while (child != null) {
                dummyChild.removeChild(child);
                docfrag.appendChild(child);
                child = dummyChild.getFirstChild();
            }
            
            return docfrag;
        } catch (Exception e) {
            throw new XMLEncryptionException("empty", e);
        }
    }

}