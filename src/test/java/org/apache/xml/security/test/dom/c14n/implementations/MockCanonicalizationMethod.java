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
package org.apache.xml.security.test.dom.c14n.implementations;

import java.io.OutputStream;
import java.util.Set;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;

import org.w3c.dom.Node;

public class MockCanonicalizationMethod extends CanonicalizerSpi {

    public static final String MOCK_CANONICALIZATION_METHOD = "mock.canonicalization.method";
    private Canonicalizer11_OmitComments _impl;

    public MockCanonicalizationMethod() {
        _impl = new Canonicalizer11_OmitComments();
    }

    public byte[] engineCanonicalizeSubTree(Node rootNode)
        throws CanonicalizationException {
        return _impl.engineCanonicalizeSubTree(rootNode);
    }

    public byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces) 
        throws CanonicalizationException {
        return _impl.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces);
    }

    public byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces, boolean propagateDefaultNamespace)
            throws CanonicalizationException {
        return _impl.engineCanonicalizeSubTree(rootNode, inclusiveNamespaces, propagateDefaultNamespace);
    }

    public byte[] engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet)
        throws CanonicalizationException {
        return _impl.engineCanonicalizeXPathNodeSet(xpathNodeSet);
    }

    public byte[] engineCanonicalizeXPathNodeSet(Set<Node> xpathNodeSet, String inclusiveNamespaces) 
        throws CanonicalizationException {
        return _impl.engineCanonicalizeXPathNodeSet(xpathNodeSet, inclusiveNamespaces);
    }

    public boolean engineGetIncludeComments() {
        return _impl.engineGetIncludeComments();
    }

    public String engineGetURI() {
        return MOCK_CANONICALIZATION_METHOD;
    }

    public void setWriter(OutputStream os) {
        _impl.setWriter(os);
    }

}
