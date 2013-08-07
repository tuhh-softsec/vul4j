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

import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 * A representation of the XML <code>Transform</code> element as 
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>. 
 * The XML Schema Definition is defined as:
 *
 * <pre>
 * &lt;element name="Transform" type="ds:TransformType"/&gt;
 *   &lt;complexType name="TransformType" mixed="true"&gt;
 *     &lt;choice minOccurs="0" maxOccurs="unbounded"&gt;
 *       &lt;any namespace="##other" processContents="lax"/&gt;
 *       &lt;!-- (1,1) elements from (0,unbounded) namespaces --&gt;
 *       &lt;element name="XPath" type="string"/&gt;
 *     &lt;/choice&gt;
 *     &lt;attribute name="Algorithm" type="anyURI" use="required"/&gt;
 *   &lt;/complexType&gt;
 * </pre>
 *
 * A <code>Transform</code> instance may be created by invoking the
 * {@link XMLSignatureFactory#newTransform newTransform} method 
 * of the {@link XMLSignatureFactory} class.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newTransform(String, TransformParameterSpec)
 */
public interface Transform extends XMLStructure, AlgorithmMethod {

    /**
     * The <a href="http://www.w3.org/2000/09/xmldsig#base64">Base64</a> 
     * transform algorithm URI.
     */
    String BASE64 = "http://www.w3.org/2000/09/xmldsig#base64";

    /**
     * The <a href="http://www.w3.org/2000/09/xmldsig#enveloped-signature">
     * Enveloped Signature</a> transform algorithm URI.
     */
    String ENVELOPED = 
        "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    /**
     * The <a href="http://www.w3.org/TR/1999/REC-xpath-19991116">XPath</a> 
     * transform algorithm URI.
     */
    String XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";

    /**
     * The <a href="http://www.w3.org/2002/06/xmldsig-filter2">
     * XPath Filter 2</a> transform algorithm URI.
     */
    String XPATH2 = "http://www.w3.org/2002/06/xmldsig-filter2";

    /**
     * The <a href="http://www.w3.org/TR/1999/REC-xslt-19991116">XSLT</a> 
     * transform algorithm URI.
     */
    String XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";

    /**
     * Returns the algorithm-specific input parameters associated with this
     * <code>Transform</code>.
     * <p>
     * The returned parameters can be typecast to a 
     * {@link TransformParameterSpec} object.
     *
     * @return the algorithm-specific input parameters (may be <code>null</code>
     *    if not specified)
     */
    AlgorithmParameterSpec getParameterSpec();

    /**
     * Transforms the specified data using the underlying transform algorithm.
     *
     * @param data the data to be transformed
     * @param context the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @return the transformed data
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     * @throws TransformException if an error occurs while executing the
     *    transform
     */
    Data transform(Data data, XMLCryptoContext context)
        throws TransformException;

    /**
     * Transforms the specified data using the underlying transform algorithm.
     * If the output of this transform is an <code>OctetStreamData</code>, then 
     * this method returns <code>null</code> and the bytes are written to the 
     * specified <code>OutputStream</code>. Otherwise, the 
     * <code>OutputStream</code> is ignored and the method behaves as if 
     * {@link #transform(Data, XMLCryptoContext)} were invoked.
     *
     * @param data the data to be transformed
     * @param context the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @param os the <code>OutputStream</code> that should be used to write
     *    the transformed data to
     * @return the transformed data (or <code>null</code> if the data was
     *    written to the <code>OutputStream</code> parameter)
     * @throws NullPointerException if <code>data</code> or <code>os</code>
     *    is <code>null</code>
     * @throws TransformException if an error occurs while executing the
     *    transform
     */
    Data transform(Data data, XMLCryptoContext context, OutputStream os)
        throws TransformException;
}
