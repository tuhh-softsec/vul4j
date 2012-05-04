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
 * Portions copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * =========================================================================== 
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

/**
 * A representation of the XML <code>Object</code> element as defined in
 * the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * An <code>XMLObject</code> may contain any data and may include optional 
 * MIME type, ID, and encoding attributes. The XML Schema Definition is 
 * defined as:
 *
 * <pre><code>
 * &lt;element name="Object" type="ds:ObjectType"/&gt; 
 * &lt;complexType name="ObjectType" mixed="true"&gt;
 *   &lt;sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *     &lt;any namespace="##any" processContents="lax"/&gt;
 *   &lt;/sequence&gt;
 *   &lt;attribute name="Id" type="ID" use="optional"/&gt; 
 *   &lt;attribute name="MimeType" type="string" use="optional"/&gt;
 *   &lt;attribute name="Encoding" type="anyURI" use="optional"/&gt; 
 * &lt;/complexType&gt;
 * </code></pre>
 *
 * A <code>XMLObject</code> instance may be created by invoking the
 * {@link XMLSignatureFactory#newXMLObject newXMLObject} method of the
 * {@link XMLSignatureFactory} class; for example:
 *
 * <pre>
 *   XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
 *   List content = Collections.singletonList(fac.newManifest(references)));
 *   XMLObject object = factory.newXMLObject(content, "object-1", null, null);
 * </pre>
 *
 * <p>Note that this class is named <code>XMLObject</code> rather than
 * <code>Object</code> to avoid naming clashes with the existing 
 * {@link java.lang.Object java.lang.Object} class.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @author Joyce L. Leung
 * @see XMLSignatureFactory#newXMLObject(List, String, String, String)
 */
public interface XMLObject extends XMLStructure {

    /**
     * URI that identifies the <code>Object</code> element (this can be 
     * specified as the value of the <code>type</code> parameter of the 
     * {@link Reference} class to identify the referent's type).
     */
    String TYPE = "http://www.w3.org/2000/09/xmldsig#Object";

    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable 
     * list} of {@link XMLStructure}s contained in this <code>XMLObject</code>,
     * which represent elements from any namespace. 
     *
     *<p>If there is a public subclass representing the type of 
     * <code>XMLStructure</code>, it is returned as an instance of that class
     * (ex: a <code>SignatureProperties</code> element would be returned
     * as an instance of {@link javax.xml.crypto.dsig.SignatureProperties}).
     *
     * @return an unmodifiable list of <code>XMLStructure</code>s (may be empty 
     *    but never <code>null</code>)
     */
    List getContent();

    /**
     * Returns the Id of this <code>XMLObject</code>.
     * 
     * @return the Id (or <code>null</code> if not specified)
     */
    String getId();
    
    /**
     * Returns the mime type of this <code>XMLObject</code>. The
     * mime type is an optional attribute which describes the data within this
     * <code>XMLObject</code> (independent of its encoding).
     *
     * @return the mime type (or <code>null</code> if not specified)
     */
    String getMimeType();
    
    /**
     * Returns the encoding URI of this <code>XMLObject</code>. The encoding
     * URI identifies the method by which the object is encoded.
     * 
     * @return the encoding URI (or <code>null</code> if not specified)
     */
    String getEncoding();
}
