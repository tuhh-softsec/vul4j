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
 * A representation of the XML <code>Manifest</code> element as defined in 
 * the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * The XML Schema Definition is defined as:
 * <pre><code>
 * &lt;element name="Manifest" type="ds:ManifestType"/&gt; 
 *   &lt;complexType name="ManifestType"&gt;
 *     &lt;sequence>
 *       &lt;element ref="ds:Reference" maxOccurs="unbounded"/&gt; 
 *     &lt;/sequence&gt;  
 *     &lt;attribute name="Id" type="ID" use="optional"/&gt; 
 *   &lt;/complexType&gt;
 * </code></pre>
 *
 * A <code>Manifest</code> instance may be created by invoking
 * one of the {@link XMLSignatureFactory#newManifest newManifest} 
 * methods of the {@link XMLSignatureFactory} class; for example: 
 *
 * <pre>
 *   XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
 *   List references = Collections.singletonList(factory.newReference
 *       ("#reference-1", DigestMethod.SHA1));
 *   Manifest manifest = factory.newManifest(references, "manifest-1");
 * </pre>
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newManifest(List)
 * @see XMLSignatureFactory#newManifest(List, String)
 */
public interface Manifest extends XMLStructure {

    /**
     * URI that identifies the <code>Manifest</code> element (this can be 
     * specified as the value of the <code>type</code> parameter of the 
     * {@link Reference} class to identify the referent's type).
     */
    String TYPE = "http://www.w3.org/2000/09/xmldsig#Manifest";

    /**
     * Returns the Id of this <code>Manifest</code>.
     *
     * @return the Id  of this <code>Manifest</code> (or <code>null</code> 
     *    if not specified)
     */
    String getId();
    
    /**
     * Returns an {@link java.util.Collections#unmodifiableList unmodifiable 
     * list} of one or more {@link Reference}s that are contained in this
     * <code>Manifest</code>. 
     *
     * @return an unmodifiable list of one or more <code>Reference</code>s 
     */
    List getReferences();
}
