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


import java.util.Iterator;
import org.w3c.dom.Element;


/**
 * A wrapper for a pointer from a key value of an <code>EncryptedKey</code> to
 * items encrypted by that key value (<code>EncryptedData</code> or
 * <code>EncryptedKey</code> elements).
 * <p>
 * It is defined as follows:
 * <xmp>
 * <complexType name='ReferenceType'>
 *     <sequence>
 *         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
 *     </sequence>
 *     <attribute name='URI' type='anyURI' use='required'/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 * @see ReferenceList
 */
public interface Reference {
    /**
     * Returns a <code>URI</code> that points to an <code>Element</code> that
     * were encrypted using the key defined in the enclosing
     * <code>EncryptedKey</code> element.
     *
     * @return an Uniform Resource Identifier that qualifies an
     *   <code>EncryptedType</code>.
     */
    String getURI();

    /**
     * Sets a <code>URI</code> that points to an <code>Element</code> that
     * were encrypted using the key defined in the enclosing
     * <code>EncryptedKey</code> element.
     *
     * @param uri the Uniform Resource Identifier that qualifies an
     *   <code>EncryptedType</code>.
     */
    void setURI(String uri);

    /**
     * Returns an <code>Iterator</code> over all the child elements contained in
     * this <code>Reference</code> that will aid the recipient in retrieving the
     * <code>EncryptedKey</code> and/or <code>EncryptedData</code> elements.
     * These could include information such as XPath transforms, decompression
     * transforms, or information on how to retrieve the elements from a
     * document storage facility.
     *
     * @return child elements.
     */
    Iterator getElementRetrievalInformation();

    /**
     * Adds retrieval information.
     *
     * @param node.
     */
    void addElementRetrievalInformation(Element info);

    /**
     * Removes the specified retrieval information.
     *
     * @param node.
     */
    void removeElementRetrievalInformation(Element info);
}
