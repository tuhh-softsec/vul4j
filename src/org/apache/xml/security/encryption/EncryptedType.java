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


import org.apache.xml.security.keys.KeyInfo;


/**
 * EncryptedType is the abstract type from which <code>EncryptedData</code> and
 * <code>EncryptedKey</code> are derived. While these two latter element types
 * are very similar with respect to their content models, a syntactical
 * distinction is useful to processing.
 * <p>
 * Its schema definition is as follows:
 * <xmp>
 * <complexType name='EncryptedType' abstract='true'>
 *     <sequence>
 *         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
 *             minOccurs='0'/>
 *         <element ref='ds:KeyInfo' minOccurs='0'/>
 *         <element ref='xenc:CipherData'/>
 *         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
 *     </sequence>
 *     <attribute name='Id' type='ID' use='optional'/>
 *     <attribute name='Type' type='anyURI' use='optional'/>
 *     <attribute name='MimeType' type='string' use='optional'/>
 *     <attribute name='Encoding' type='anyURI' use='optional'/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface EncryptedType {
    /**
     * Returns a <code>String</code> providing for the standard method of
     * assigning an id to the element within the document context.
     *
     * @return the id for the <code>EncryptedType</code>.
     */
    String getId();

    /**
     * Sets the id.
     *
     * @param id.
     */
    void setId(String id);

    /**
     * Returns an <code>URI</code> identifying type information about the
     * plaintext form of the encrypted content. While optional, this
     * specification takes advantage of it for mandatory processing described in
     * Processing Rules: Decryption (section 4.2). If the
     * <code>EncryptedData</code> element contains data of Type 'element' or
     * element 'content', and replaces that data in an XML document context, it
     * is strongly recommended the Type attribute be provided. Without this
     * information, the decryptor will be unable to automatically restore the
     * XML document to its original cleartext form.
     *
     * @return the identifier for the type of information in plaintext form of
     *   encrypted content.
     */
    String getType();

    /**
     * Sets the type.
     *
     * @param type an <code>URI</code> identifying type information about the
     *   plaintext form of the encrypted content.
     */
    void setType(String type);

    /**
     * Returns a <code>String</code> which describes the media type of the data
     * which has been encrypted. The value of this attribute has values defined
     * by [MIME]. For example, if the data that is encrypted is a base64 encoded
     * PNG, the transfer Encoding may be specified as
     * 'http://www.w3.org/2000/09/xmldsig#base64' and the MimeType as
     * 'image/png'.
     * <br>
     * This attribute is purely advisory; no validation of the MimeType
     * information is required and it does not indicate the encryption
     * application must do any additional processing. Note, this information may
     * not be necessary if it is already bound to the identifier in the Type
     * attribute. For example, the Element and Content types defined in this
     * specification are always UTF-8 encoded text.
     *
     * @return the media type of the data which was encrypted.
     */
    String getMimeType();

    /**
     * Sets the mime type.
     *
     * @param type a <code>String</code> which describes the media type of the
     *   data which has been encrypted.
     */
    void setMimeType(String type);

    /**
     * Retusn an <code>URI</code> representing the encoding of the
     * <code>EncryptedType</code>.
     *
     * @return the encoding of this <code>EncryptedType</code>.
     */
    String getEncoding();

    /**
     * Sets the <code>URI</code> representing the encoding of the
     * <code>EncryptedType</code>.
     *
     * @param encoding.
     */
    void setEncoding(String encoding);

    /**
     * Returns an <code>EncryptionMethod</code> that describes the encryption
     * algorithm applied to the cipher data. If the element is absent, the
     * encryption algorithm must be known by the recipient or the decryption
     * will fail.
     *
     * @return the method used to encrypt the cipher data.
     */
    EncryptionMethod getEncryptionMethod();

    /**
     * Sets the <code>EncryptionMethod</code> used to encrypt the cipher data.
     *
     * @param method the <code>EncryptionMethod</code>.
     */
    void setEncryptionMethod(EncryptionMethod method);

    /**
     * Returns the <code>ds:KeyInfo</code>, that carries information about the
     * key used to encrypt the data. Subsequent sections of this specification
     * define new elements that may appear as children of
     * <code>ds:KeyInfo</code>.
     *
     * @return information about the key that encrypted the cipher data.
     */
    KeyInfo getKeyInfo();

    /**
     * Sets the encryption key information.
     *
     * @param info the <code>ds:KeyInfo</code>, that carries information about
     *   the key used to encrypt the data.
     */
    void setKeyInfo(KeyInfo info);

    /**
     * Returns the <code>CipherReference</code> that contains the
     * <code>CipherValue</code> or <code>CipherReference</code> with the
     * encrypted data.
     *
     * @return the cipher data for the encrypted type.
     */
    CipherData getCipherData();

    /**
     * Returns additional information concerning the generation of the
     * <code>EncryptedType</code>.
     *
     * @return information relating to the generation of the
     *   <code>EncryptedType</code>.
     */
    EncryptionProperties getEncryptionProperties();

    /**
     * Sets the <code>EncryptionProperties</code> that supplies additional
     * information about the generation of the <code>EncryptedType</code>.
     *
     * @param properties.
     */
    void setEncryptionProperties(EncryptionProperties properties);
}

