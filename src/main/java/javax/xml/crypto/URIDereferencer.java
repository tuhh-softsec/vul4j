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
package javax.xml.crypto;

/**
 * A dereferencer of {@link URIReference}s.
 * <p>
 * The result of dereferencing a <code>URIReference</code> is either an 
 * instance of {@link OctetStreamData} or {@link NodeSetData}. Unless the
 * <code>URIReference</code> is a <i>same-document reference</i> as defined
 * in section 4.2 of the W3C Recommendation for XML-Signature Syntax and 
 * Processing, the result of dereferencing the <code>URIReference</code>
 * MUST be an <code>OctetStreamData</code>.
 *
 * @author Sean Mullan
 * @author Joyce Leung
 * @author JSR 105 Expert Group
 * @see XMLCryptoContext#setURIDereferencer(URIDereferencer)
 * @see XMLCryptoContext#getURIDereferencer
 */
public interface URIDereferencer {

    /**
     * Dereferences the specified <code>URIReference</code> and returns the 
     * dereferenced data.
     *
     * @param uriReference the <code>URIReference</code>
     * @param context an <code>XMLCryptoContext</code> that may 
     *    contain additional useful information for dereferencing the URI. This 
     *    implementation should dereference the specified 
     *    <code>URIReference</code> against the context's <code>baseURI</code> 
     *    parameter, if specified.
     * @return the dereferenced data 
     * @throws NullPointerException if <code>uriReference</code> or 
     *    <code>context</code> are <code>null</code>
     * @throws URIReferenceException if an exception occurs while 
     *    dereferencing the specified <code>uriReference</code>
     */
    Data dereference(URIReference uriReference, XMLCryptoContext context) 
        throws URIReferenceException;
}
