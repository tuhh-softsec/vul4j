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
 * <code>EncryptionMethod</code> describes the encryption algorithm applied to
 * the cipher data. If the element is absent, the encryption algorithm must be
 * known by the recipient or the decryption will fail.
 * <p>
 * It is defined as follows:
 * <xmp>
 * <complexType name='EncryptionMethodType' mixed='true'>
 *     <sequence>
 *         <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
 *         <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
 *         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
 *     </sequence>
 *     <attribute name='Algorithm' type='anyURI' use='required'/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface EncryptionMethod {
    /**
     * Returns the algorithm applied to the cipher data.
     *
     * @return the encryption algorithm.
     */
    String getAlgorithm();

    /**
     * Returns the key size of the key of the algorithm applied to the cipher
     * data.
     *
     * @return the key size.
     */
    int getKeySize();

    /**
     * Sets the size of the key of the algorithm applied to the cipher data.
     *
     * @param size the key size.
     */
    void setKeySize(int size);

    /**
     * Returns the OAEP parameters of the algorithm applied applied to the
     * cipher data.
     *
     * @return the OAEP parameters.
     */
    byte[] getOAEPparams();

    /**
     * Sets the OAEP parameters.
     *
     * @param parameters the OAEP parameters.
     */
    void setOAEPparams(byte[] parameters);

    /**
     * Returns an iterator over all the additional elements contained in the
     * <code>EncryptionMethod</code>.
     *
     * @return an <code>Iterator</code> over all the additional infomation
     *   about the <code>EncryptionMethod</code>.
     */
    Iterator getEncryptionMethodInformation();

    /**
     * Adds encryption method information.
     *
     * @param information additional encryption method information.
     */
    void addEncryptionMethodInformation(Element information);

    /**
     * Removes encryption method information.
     *
     * @param information the information to remove from the
     *   <code>EncryptionMethod</code>.
     */
    void removeEncryptionMethodInformation(Element information);
}

