/*
 * Copyright 2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto.dsig;

import javax.xml.crypto.XMLStructure;
import java.io.InputStream;
import java.util.List;

/**
 * An representation of the XML <code>SignedInfo</code> element as 
 * defined in the <a href="http://www.w3.org/TR/xmldsig-core/">
 * W3C Recommendation for XML-Signature Syntax and Processing</a>.
 * The XML Schema Definition is defined as:
 * <pre><code>
 * &lt;element name="SignedInfo" type="ds:SignedInfoType"/&gt; 
 * &lt;complexType name="SignedInfoType"&gt;
 *   &lt;sequence&gt; 
 *     &lt;element ref="ds:CanonicalizationMethod"/&gt; 
 *     &lt;element ref="ds:SignatureMethod"/&gt; 
 *     &lt;element ref="ds:Reference" maxOccurs="unbounded"/&gt; 
 *   &lt;/sequence&gt;  
 *   &lt;attribute name="Id" type="ID" use="optional"/&gt; 
 * &lt;/complexType&gt;
 * </code></pre>
 *
 * A <code>SignedInfo</code> instance may be created by invoking one of the
 * {@link XMLSignatureFactory#newSignedInfo newSignedInfo} methods of the
 * {@link XMLSignatureFactory} class.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see XMLSignatureFactory#newSignedInfo(CanonicalizationMethod, SignatureMethod, List)
 * @see XMLSignatureFactory#newSignedInfo(CanonicalizationMethod, SignatureMethod, List, String)
 */
public interface SignedInfo extends XMLStructure {

    /**
     * Returns the canonicalization method of this <code>SignedInfo</code>.
     *
     * @return the canonicalization method
     */
    CanonicalizationMethod getCanonicalizationMethod();
    
    /**
     * Returns the signature method of this <code>SignedInfo</code>.
     *
     * @return the signature method
     */
    SignatureMethod getSignatureMethod();
    
    /**
     * Returns an {@link java.util.Collections#unmodifiableList 
     * unmodifiable list} of one or more {@link Reference}s. 
     *
     * @return an unmodifiable list of one or more {@link Reference}s
     */
    List getReferences();
    
    /**
     * Returns the optional <code>Id</code> attribute of this 
     * <code>SignedInfo</code>.
     *
     * @return the id (may be <code>null</code> if not specified)
     */
    String getId();

    /**
     * Returns the canonicalized signed info bytes after a signing or 
     * validation operation. This method is useful for debugging.
     *
     * @return an <code>InputStream</code> containing the canonicalized bytes, 
     *    or <code>null</code> if this <code>SignedInfo</code> has not been 
     *    signed or validated yet
     */
    InputStream getCanonicalizedData();
}
