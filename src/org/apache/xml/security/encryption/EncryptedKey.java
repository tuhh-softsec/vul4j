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


/**
 * The <code>EncryptedKey</code> element is used to transport encryption keys
 * from the originator to a known recipient(s). It may be used as a stand-alone
 * XML document, be placed within an application document, or appear inside an
 * <code>EncryptedData</code> element as a child of a <code>ds:KeyInfo</code>
 * element. The key value is always encrypted to the recipient(s). When
 * <code>EncryptedKey</code> is decrypted the resulting octets are made
 * available to the <code>EncryptionMethod</code> algorithm without any
 * additional processing.
 * <p>
 * Its schema definition is as follows:
 * <xmp>
 * <element name='EncryptedKey' type='xenc:EncryptedKeyType'/>
 * <complexType name='EncryptedKeyType'>
 *     <complexContent>
 *         <extension base='xenc:EncryptedType'>
 *             <sequence>
 *                 <element ref='xenc:ReferenceList' minOccurs='0'/>
 *                 <element name='CarriedKeyName' type='string' minOccurs='0'/>
 *             </sequence>
 *             <attribute name='Recipient' type='string' use='optional'/>
 *         </extension>
 *     </complexContent>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface EncryptedKey extends EncryptedType {
    /**
     * Returns a hint as to which recipient this encrypted key value is intended
     * for.
     *
     * @return the recipient of the <code>EncryptedKey</code>.
     */
    String getRecipient();

    /**
     * Sets the recipient for this <code>EncryptedKey</code>.
     *
     * @param recipient the recipient for this <code>EncryptedKey</code>.
     */
    void setRecipient(String recipient);

    /**
     * Returns pointers to data and keys encrypted using this key. The reference
     * list may contain multiple references to <code>EncryptedKey</code> and
     * <code>EncryptedData</code> elements. This is done using
     * <code>KeyReference</code> and <code>DataReference</code> elements
     * respectively.
     *
     * @return an <code>Iterator</code> over all the <code>ReferenceList</code>s
     *   contained in this <code>EncryptedKey</code>.
     */
    ReferenceList getReferenceList();

    /**
     * Sets the <code>ReferenceList</code> to the <code>EncryptedKey</code>.
     *
     * @param list a list of pointers to data elements encrypted using this key.
     */
    void setReferenceList(ReferenceList list);

    /**
     * Returns a user readable name with the key value. This may then be used to
     * reference the key using the <code>ds:KeyName</code> element within
     * <code>ds:KeyInfo</code>. The same <code>CarriedKeyName</code> label,
     * unlike an ID type, may occur multiple times within a single document. The
     * value of the key is to be the same in all <code>EncryptedKey</code>
     * elements identified with the same <code>CarriedKeyName</code> label
     * within a single XML document.
     * <br>
     * <b>Note</b> that because whitespace is significant in the value of
     * the <code>ds:KeyName</code> element, whitespace is also significant in
     * the value of the <code>CarriedKeyName</code> element.
     *
     * @param an <code>Iterator</code> over all the carried names contained in
     *   this <code>EncryptedKey</code>.
     */
    String getCarriedName();

    /**
     * Sets the carried name.
     *
     * @param name the carried name.
     */
    void setCarriedName(String name);
}

