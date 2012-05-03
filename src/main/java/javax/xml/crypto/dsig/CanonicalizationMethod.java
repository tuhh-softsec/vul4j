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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;

/**
 * A representation of the XML <code>CanonicalizationMethod</code> 
 * element as defined in the 
 * <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>. The XML 
 * Schema Definition is defined as:
 * <p>
 * <pre>
 *   &lt;element name="CanonicalizationMethod" type="ds:CanonicalizationMethodType"/&gt;
 *     &lt;complexType name="CanonicalizationMethodType" mixed="true"&gt;
 *       &lt;sequence&gt;
 *         &lt;any namespace="##any" minOccurs="0" maxOccurs="unbounded"/&gt;
 *           &lt;!-- (0,unbounded) elements from (1,1) namespace --&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Algorithm" type="anyURI" use="required"/&gt;
 *     &lt;/complexType&gt;
 * </pre>
 *
 * A <code>CanonicalizationMethod</code> instance may be created by invoking 
 * the {@link XMLSignatureFactory#newCanonicalizationMethod 
 * newCanonicalizationMethod} method of the {@link XMLSignatureFactory} class.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newCanonicalizationMethod(String, C14NMethodParameterSpec)
 */
public interface CanonicalizationMethod extends Transform {

    /**
     * The <a href="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical 
     * XML (without comments)</a> canonicalization method algorithm URI.
     */
    static final String INCLUSIVE = 
        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    /**
     * The 
     * <a href="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments">
     * Canonical XML with comments</a> canonicalization method algorithm URI.
     */
    static final String INCLUSIVE_WITH_COMMENTS =
        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";

    /**
     * The <a href="http://www.w3.org/2001/10/xml-exc-c14n#">Exclusive 
     * Canonical XML (without comments)</a> canonicalization method algorithm
     * URI.
     */
    static final String EXCLUSIVE =
        "http://www.w3.org/2001/10/xml-exc-c14n#";

    /**
     * The <a href="http://www.w3.org/2001/10/xml-exc-c14n#WithComments">
     * Exclusive Canonical XML with comments</a> canonicalization method 
     * algorithm URI.
     */
    static final String EXCLUSIVE_WITH_COMMENTS =
        "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";

    /**
     * Returns the algorithm-specific input parameters associated with this 
     * <code>CanonicalizationMethod</code>.
     *
     * <p>The returned parameters can be typecast to a 
     * {@link C14NMethodParameterSpec} object.
     *
     * @return the algorithm-specific input parameters (may be 
     *    <code>null</code> if not specified)
     */
    AlgorithmParameterSpec getParameterSpec();
}
