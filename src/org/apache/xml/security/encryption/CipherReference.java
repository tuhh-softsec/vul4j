/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.encryption;



/**
 * <code>CipherReference</code> identifies a source which, when processed,
 * yields the encrypted octet sequence.
 * <p>
 * The actual value is obtained as follows. The <code>CipherReference URI</code>
 * contains an identifier that is dereferenced. Should the
 * <code>CipherReference</code> element contain an OPTIONAL sequence of
 * Transforms, the data resulting from dereferencing the <code>URI</code> is
 * transformed as specified so as to yield the intended cipher value. For
 * example, if the value is base64 encoded within an XML document; the
 * transforms could specify an XPath expression followed by a base64 decoding so
 * as to extract the octets.
 * <p>
 * The syntax of the <code>URI</code> and Transforms is similar to that of
 * [XML-DSIG]. However, there is a difference between signature and encryption
 * processing. In [XML-DSIG] both generation and validation processing start
 * with the same source data and perform that transform in the same order. In
 * encryption, the decryptor has only the cipher data and the specified
 * transforms are enumerated for the decryptor, in the order necessary to obtain
 * the octets. Consequently, because it has different semantics Transforms is in
 * the &xenc; namespace.
 * <p>
 * The schema definition is as follows:
 * <xmp>
 * <element name='CipherReference' type='xenc:CipherReferenceType'/>
 * <complexType name='CipherReferenceType'>
 *     <sequence>
 *         <element name='Transforms' type='xenc:TransformsType' minOccurs='0'/>
 *     </sequence>
 *     <attribute name='URI' type='anyURI' use='required'/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface CipherReference {
    /**
     * Returns an <code>URI</code> that contains an identifier that should be
     * dereferenced.
     */
    String getURI();

    /**
     * Returns the <code>Transforms</code> that specifies how to transform the
     * <code>URI</code> to yield the appropiate cipher value.
     *
     * @return the transform that specifies how to transform the reference to
     *   yield the intended cipher value.
     */
    Transforms getTransforms();

    /**
     * Sets the <code>Transforms</code> that specifies how to transform the
     * <code>URI</code> to yield the appropiate cipher value.
     *
     * @param transforms the set of <code>Transforms</code> that specifies how
     *   to transform the reference to yield the intended cipher value.
     */
    void setTransforms(Transforms transforms);
}

