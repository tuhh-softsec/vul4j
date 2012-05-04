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

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.XMLStructure;
import java.security.spec.AlgorithmParameterSpec;

/**
 * A representation of the XML <code>DigestMethod</code> element as 
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>. 
 * The XML Schema Definition is defined as:
 * <p>
 * <pre>
 *   &lt;element name="DigestMethod" type="ds:DigestMethodType"/&gt;
 *     &lt;complexType name="DigestMethodType" mixed="true"&gt;
 *       &lt;sequence&gt;
 *         &lt;any namespace="##any" minOccurs="0" maxOccurs="unbounded"/&gt;
 *           &lt;!-- (0,unbounded) elements from (1,1) namespace --&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Algorithm" type="anyURI" use="required"/&gt;
 *     &lt;/complexType&gt;
 * </pre>
 *
 * A <code>DigestMethod</code> instance may be created by invoking the 
 * {@link XMLSignatureFactory#newDigestMethod newDigestMethod} method 
 * of the {@link XMLSignatureFactory} class.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newDigestMethod(String, DigestMethodParameterSpec)
 */
public interface DigestMethod extends XMLStructure, AlgorithmMethod {

    /**
     * The <a href="http://www.w3.org/2000/09/xmldsig#sha1">
     * SHA1</a> digest method algorithm URI.
     */
    String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";

    /**
     * The <a href="http://www.w3.org/2001/04/xmlenc#sha256">
     * SHA256</a> digest method algorithm URI.
     */
    String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";

    /**
     * The <a href="http://www.w3.org/2001/04/xmlenc#sha512">
     * SHA512</a> digest method algorithm URI.
     */
    String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";

    /**
     * The <a href="http://www.w3.org/2001/04/xmlenc#ripemd160">
     * RIPEMD-160</a> digest method algorithm URI.
     */
    String RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";

    /**
     * Returns the algorithm-specific input parameters associated with this
     * <code>DigestMethod</code>.
     *
     * <p>The returned parameters can be typecast to a {@link 
     * javax.xml.crypto.dsig.spec.DigestMethodParameterSpec} object.
     *
     * @return the algorithm-specific parameters (may be <code>null</code> if
     *    not specified)
     */
    AlgorithmParameterSpec getParameterSpec();
}
