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
import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.Element;


/**
 * A Key Agreement algorithm provides for the derivation of a shared secret key
 * based on a shared secret computed from certain types of compatible public
 * keys from both the sender and the recipient. Information from the originator
 * to determine the secret is indicated by an optional OriginatorKeyInfo
 * parameter child of an <code>AgreementMethod</code> element while that
 * associated with the recipient is indicated by an optional RecipientKeyInfo. A
 * shared key is derived from this shared secret by a method determined by the
 * Key Agreement algorithm.
 * <p>
 * <b>Note:</b> XML Encryption does not provide an on-line key agreement
 * negotiation protocol. The <code>AgreementMethod</code> element can be used by
 * the originator to identify the keys and computational procedure that were
 * used to obtain a shared encryption key. The method used to obtain or select
 * the keys or algorithm used for the agreement computation is beyond the scope
 * of this specification.
 * <p>
 * The <code>AgreementMethod</code> element appears as the content of a
 * <code>ds:KeyInfo</code> since, like other <code>ds:KeyInfo</code> children,
 * it yields a key. This <code>ds:KeyInfo</code> is in turn a child of an
 * <code>EncryptedData</code> or <code>EncryptedKey</code> element. The
 * Algorithm attribute and KeySize child of the <code>EncryptionMethod</code>
 * element under this <code>EncryptedData</code> or <code>EncryptedKey</code>
 * element are implicit parameters to the key agreement computation. In cases
 * where this <code>EncryptionMethod</code> algorithm <code>URI</code> is
 * insufficient to determine the key length, a KeySize MUST have been included.
 * In addition, the sender may place a KA-Nonce element under
 * <code>AgreementMethod</code> to assure that different keying material is
 * generated even for repeated agreements using the same sender and recipient
 * public keys.
 * <p>
 * If the agreed key is being used to wrap a key, then
 * <code>AgreementMethod</code> would appear inside a <code>ds:KeyInfo</code>
 * inside an <code>EncryptedKey</code> element.
 * <p>
 * The Schema for AgreementMethod is as follows:
 * <xmp>
 * <element name="AgreementMethod" type="xenc:AgreementMethodType"/>
 * <complexType name="AgreementMethodType" mixed="true">
 *     <sequence>
 *         <element name="KA-Nonce" minOccurs="0" type="base64Binary"/>
 *         <!-- <element ref="ds:DigestMethod" minOccurs="0"/> -->
 *         <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *         <element name="OriginatorKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
 *         <element name="RecipientKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
 *     </sequence>
 *     <attribute name="Algorithm" type="anyURI" use="required"/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface AgreementMethod {
    /**
     * Returns an <code>byte</code> array.
     */
    byte[] getKANonce();

    /**
     * Sets the KANonce.jj
     */
    void setKANonce(byte[] kanonce);

    /**
     * Returns aditional information regarding the <code>AgreementMethod</code>.
     */
    Iterator getAgreementMethodInformation();

    /**
     * Adds additional <code>AgreementMethod</code> information.
     *
     * @param info a <code>Element</code> that represents additional information
     * specified by
     *   <xmp>
     *     <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
     *   </xmp>
     */
    void addAgreementMethodInformation(Element info);

    /**
     * Removes additional <code>AgreementMethod</code> information.
     *
     * @param info a <code>Element</code> that represents additional information
     * specified by
     *   <xmp>
     *     <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
     *   </xmp>
     */
    void revoveAgreementMethodInformation(Element info);

    /**
     * Returns information relating to the originator's shared secret.
     *
     * @return information relating to the originator's shared secret.
     */
    KeyInfo getOriginatorKeyInfo();

    /**
     * Sets the information relating to the originator's shared secret.
     *
     * @param keyInfo information relating to the originator's shared secret.
     */
    void setOriginatorKeyInfo(KeyInfo keyInfo);

    /**
     * Retruns information relating to the recipient's shared secret.
     *
     * @return information relating to the recipient's shared secret.
     */
    KeyInfo getRecipientKeyInfo();

    /**
     * Sets the information relating to the recipient's shared secret.
     *
     * @param keyInfo information relating to the recipient's shared secret.
     */
    void setRecipientKeyInfo(KeyInfo keyInfo);

    /**
     * Returns the algorithm URI of this <code>CryptographicMethod</code>.
     *
     * @return the algorithm URI of this <code>CryptographicMethod</code>
     */
    String getAlgorithm();
}

