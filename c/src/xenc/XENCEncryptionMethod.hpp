/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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

/*
 * XSEC
 *
 * XENCEncryptionMethod := Interface definition for EncryptionMethod element
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTIONMETHOD_INCLUDE
#define XENCENCRYPTIONMETHOD_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

/**
 * @ingroup xenc
 * @{
 */

/**
 * @brief Interface definition for the EncryptionMethod object
 *
 * The \<EncryptionMethod\> element holds information about the 
 * encryption algorithm being used.
 *
 * This element is optional within an EncryptedType derivative,
 * but applications not making use of this need to know the 
 * this information, otherwise the library will not be able to
 * decrypt the data.
 *
 * It is defined as :
 * \verbatim
  <complexType name='EncryptionMethodType' mixed='true'>
    <sequence>
      <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
      <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
      <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
    </sequence>
    <attribute name='Algorithm' type='anyURI' use='required'/>
  </complexType>
  \endverbatim
 */


class XENCEncryptionMethod {

public:

	XENCEncryptionMethod() {};

	virtual ~XENCEncryptionMethod() {};

	/** @name Getter Methods */
	//@{

	/**
	 * \brief Get the algorithm
	 *
	 * Return the Algorithm URI representing the encryption type for this
	 * encrypted data
	 *
	 * @returns the URI representing the algorithm
	 */

	virtual const XMLCh * getAlgorithm(void) const = 0;

	/**
	 * \brief Get the digest method URI
	 *
	 * Return the Algorithm URI represtenting the Digest Method for those
	 * encryption algorithms that require it (such as RSA with OAEP padding)
	 *
	 * @returns the URI representing the digest method algorithm
	 */

	virtual const XMLCh * getDigestMethod(void) const = 0;

	/**
	 * \brief Get the value of the OAEPparams string
	 *
	 * The OAEP RSA padding method allows a user to set an optional
	 * params string (that will be used as input to the Digest algorithm).
	 *
	 * @returns The string (base64 encoded value) representing the OAEP params
	 */

	virtual const XMLCh * getOAEPparams(void) const = 0;

	/**
	 * \brief Get the KeySize that was set in this EncryptionMethod.
	 *
	 * This field would not normally be used for the encryption algorithms
	 * explicitly referenced in the XML Encryption standard.  It is provided
	 * mainly for stream ciphers that have a variable key length
	 */

	virtual int getKeySize(void) const = 0;

	/**
	 * \brief Get the DOM Element Node of this structure
	 *
	 * @returns the DOM Element Node representing the \<EncryptionMethod\> element
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const = 0;


	//@}

	/** @name Setter Methods */
	//@{

	/**
	 * \brief Set the value of the DigestMethod
	 *
	 * Sets the DigestMethod element's Algorithm attribute to the passed in
	 * value - should be a URI string
	 *
	 * @param method String to set in the Algorithm attribute.  Will create a
	 * \<DigestMethod\> element if one does not already exist
	 */

	virtual void setDigestMethod(const XMLCh * method) = 0;

	/**
	 * \brief Set the value of the OAEPparams string
	 *
	 * Sets the OAEPparams element's Text node child to the passed in
	 * value - should be a base64 encoded value
	 *
	 * @param params String to set in the OAEPparams text node.  Will create a
	 * \<OAEPparams\> element if one does not already exist
	 */

	virtual void setOAEPparams(const XMLCh * params) = 0;

	/**
	 * \brief Set the KeySize that in this EncryptionMethod.
	 *
	 * This field would not normally be used for the encryption algorithms
	 * explicitly referenced in the XML Encryption standard.  It is provided
	 * mainly for stream ciphers that have a variable key length
	 */

	virtual void setKeySize(int size) = 0;

	//@}

private:

	// Unimplemented
	XENCEncryptionMethod(const XENCEncryptionMethod &);
	XENCEncryptionMethod & operator = (const XENCEncryptionMethod &);

};

#endif /* XENCENCRYPTIONMETHOD_INCLUDE */
