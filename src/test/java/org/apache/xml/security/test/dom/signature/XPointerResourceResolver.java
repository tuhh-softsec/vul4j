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
package org.apache.xml.security.test.dom.signature;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of a resource resolver, which evaluates xpointer expressions.
 * 
 * @author wglas
 */
public class XPointerResourceResolver extends ResourceResolverSpi {
    private static org.slf4j.Logger log = 
        org.slf4j.LoggerFactory.getLogger(XPointerResourceResolver.class);

    private static final String XP_OPEN = "xpointer(";
    private static final String XNS_OPEN = "xmlns(";

    private Node baseNode;

    public XPointerResourceResolver(Node baseNode) {
        this.baseNode = baseNode;
    }

    @Override
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        String v = context.uriToResolve;

        if (v == null || v.length() <= 0) {
            return false;
        }

        if (v.charAt(0) != '#') {
            return false;
        }

        String xpURI;
        try {
            xpURI = URLDecoder.decode(v, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("utf-8 not a valid encoding",e);
            return false;
        }

        String parts[] = xpURI.substring(1).split("\\s");

        // plain ID reference.
        if (parts.length == 1 && !parts[0].startsWith(XNS_OPEN)) {
            return true;
        }

        int i = 0;
        for (; i < parts.length - 1; ++i) {
            if (!parts[i].endsWith(")") ||  !parts[i].startsWith(XNS_OPEN)) {
                return false;
            }
        }

        if (!parts[i].endsWith(")") || !parts[i].startsWith(XP_OPEN)) {
            return false;
        }

        log.debug("xpURI = " + xpURI);
        log.debug("BaseURI = " + context.baseUri);

        return true;
    }

    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) 
        throws ResourceResolverException {
        String v = context.uriToResolve;

        if (v.charAt(0) != '#') {
            return null;
        }

        String xpURI;
        try {
            xpURI = URLDecoder.decode(v, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("utf-8 not a valid encoding ", e);
            return null;
        }

        String parts[] = xpURI.substring(1).split("\\s");

        int i = 0;
        Map<String, String> namespaces = new HashMap<String, String>();
        
        if (parts.length > 1) {

            for (; i < parts.length - 1; ++i) {
                if (!parts[i].endsWith(")") ||  !parts[i].startsWith(XNS_OPEN)) {
                    return null;
                }

                String mapping = parts[i].substring(XNS_OPEN.length(), parts[i].length() - 1);

                int pos = mapping.indexOf('=');

                if (pos <= 0 || pos >= mapping.length() - 1) {
                    throw new ResourceResolverException(
                        "malformed namespace part of XPointer expression", context.uriToResolve, context.baseUri
                    );
                }

                namespaces.put(
                    mapping.substring(0, pos), 
                    mapping.substring(pos + 1)
                );
            }
        }

        try {
            Node node = null;
            NodeList nodes = null;

            // plain ID reference.
            if (i == 0 && !parts[i].startsWith(XP_OPEN)) {
                node = this.baseNode.getOwnerDocument().getElementById(parts[i]);
            } else {
                if (!parts[i].endsWith(")") || !parts[i].startsWith(XP_OPEN)) {
                    return null;
                }

                String xpathExpr = parts[i].substring(XP_OPEN.length(), parts[i].length() - 1);

                XPathFactory xpf = XPathFactory.newInstance();
                XPath xpath = xpf.newXPath();
                DSNamespaceContext namespaceContext = 
                    new DSNamespaceContext(namespaces);
                xpath.setNamespaceContext(namespaceContext);

                nodes = 
                    (NodeList) xpath.evaluate(
                        xpathExpr, this.baseNode, XPathConstants.NODESET
                    );

                if (nodes.getLength() == 0) {
                    return null;
                }
                if (nodes.getLength() == 1) {
                    node = nodes.item(0);
                }
            }

            XMLSignatureInput result = null;

            if (node != null) {
                result = new XMLSignatureInput(node);
            } else if (nodes != null) {
                Set<Node> nodeSet = new HashSet<Node>(nodes.getLength());

                for (int j = 0; j < nodes.getLength(); ++j) {
                    nodeSet.add(nodes.item(j));
                }

                result = new XMLSignatureInput(nodeSet);
            } else {
                return null;
            }

            result.setMIMEType("text/xml");
            result.setExcludeComments(true);
            result.setSourceURI((context.baseUri != null) ? context.baseUri.concat(v) : v);      

            return result;
        } catch (XPathExpressionException e) {
            throw new ResourceResolverException(
                 "Problem evaluating XPath expression", e, context.uriToResolve, context.baseUri
            );
        }
    }

}
