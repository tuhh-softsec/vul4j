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

import javax.xml.crypto.XMLStructure;
import java.util.List;

/**
 * A representation of the XML <code>SignatureProperties</code> element as 
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>. 
 * The XML Schema Definition is defined as:
 * <pre><code>
 *&lt;element name="SignatureProperties" type="ds:SignaturePropertiesType"/&gt; 
 *   &lt;complexType name="SignaturePropertiesType"&gt;
 *     &lt;sequence&gt;
 *       &lt;element ref="ds:SignatureProperty" maxOccurs="unbounded"/&gt; 
 *     &lt;/sequence&gt;
 *     &lt;attribute name="Id" type="ID" use="optional"/&gt; 
 *   &lt;/complexType&gt;
 * </code></pre>
 *
 * A <code>SignatureProperties</code> instance may be created by invoking the
 * {@link XMLSignatureFactory#newSignatureProperties newSignatureProperties} 
 * method of the {@link XMLSignatureFactory} class; for example: 
 *
 * <pre>
 *   XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
 *   SignatureProperties properties = 
 *	factory.newSignatureProperties(props, "signature-properties-1");
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newSignatureProperties(List, String)
 * @see SignatureProperty
 */
public interface SignatureProperties extends XMLStructure {

    /**
     * URI that identifies the <code>SignatureProperties</code> element (this 
     * can be specified as the value of the <code>type</code> parameter of the 
     * {@link Reference} class to identify the referent's type).
     */
    String TYPE =
        "http://www.w3.org/2000/09/xmldsig#SignatureProperties";

    /**
     * Returns the Id of this <code>SignatureProperties</code>.
     *
     * @return the Id of this <code>SignatureProperties</code> (or 
     *    <code>null</code> if not specified)
     */
    String getId();
    
    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable 
     * list} of one or more {@link SignatureProperty}s that are contained in 
     * this <code>SignatureProperties</code>. 
     *
     * @return an unmodifiable list of one or more 
     *    <code>SignatureProperty</code>s 
     */
    List getProperties();
}
