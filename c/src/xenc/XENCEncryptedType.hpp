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
 * XENCEncryptedType := Definition for holder object for EncryptedType 
 * element
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTEDTYPE_INCLUDE
#define XENCENCRYPTEDTYPE_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

class XENCCipherData;
class DSIGKeyInfoList;
class DSIGKeyInfoName;
class XENCEncryptionMethod;
class XENCEncryptedKey;

/**
 * @ingroup xenc
 * @{
 */

/**
 * @brief Interface definition for the EncryptedType object
 *
 * The \<EncryptedType\> element is an abstract type on which
 * EncryptedData and EncryptedKey objects are built.
 *
 * This is the base class on which most of the XML Encryption
 * standard is built.  Using classes derived from this, 
 * calling programs can decrypt the content, determine KeyInfo
 * references etc.
 *
 * In general derived objects should not be used directly.
 * The XENCCipher class should be used to operate on them.
 */


class XENCEncryptedType {

	/** @name Constructors and Destructors */
	//@{

protected:

	XENCEncryptedType() {};

public:

	virtual ~XENCEncryptedType() {};

	/** @name Basic Interface Methods */
	//@{

	/**
	 * \brief Retrieve the CipherData element
	 *
	 * CipherData elements are the sub part of the EncryptedData
	 * that hold the actual enciphered information.
	 *
	 * @returns The CipherData object
	 */

	virtual XENCCipherData * getCipherData(void) = 0;

	/**
	 * \brief Retrieve the EncryptionMethod element
	 *
	 * The EncryptionMethod element holds information about the 
	 * encryption algorithm to be used to encrypt/decrypt the data
	 *
	 * This method provides a means to extract the EncryptionMethod
	 * element from the EncryptedType
	 *
	 * @returns The EncryptionMethod element
	 */

	virtual XENCEncryptionMethod * getEncryptionMethod(void) = 0;

	/**
	 * \brief Retrieve the DOM Node that heads up the structure
	 *
	 * If this object has been fully created, this call will provide
	 * the element node that heads up this structure
	 *
	 * @returns the DOMNode that heads up this structure
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * getDOMNode() = 0;

	//@}

	/** @name KeyInfo Element Manipulation */
	
	//@{

	/**
	 * \brief Get the list of \<KeyInfo\> elements.
	 *
	 * <p>This function recovers list that contains the KeyInfo elements
	 * read in from the DOM document.</p>
	 *
	 * <p>This list should be used by calling applications to determine what key
	 * is appropriate for decrypting the document.</p>
	 *
	 * @note The list should never be modified directly.  If you need to
	 * add keyInfo elements, call the appropriate functions in EncryptedType
	 *
	 * @returns A pointer to the DSIGKeyInfoList object held by the XENCCipher
	 */
	
	virtual DSIGKeyInfoList * getKeyInfoList(void) = 0;

	/**
	 * \brief Clear out all KeyInfo elements in the signature.
	 *
	 * This function will delete all KeyInfo elements from both the EncryptedType
	 * object <em>and the associated DOM</em>.
	 *
	 */

	virtual void clearKeyInfo(void) = 0;

	/**
	 * \brief Append a KeyName element.
	 *
	 * Add a new KeyInfo element for a key name.
	 *
	 * @param name The name of the key to set in the XML
	 * @param isDName Treat the name as a Distinguished name and encode accordingly
	 * @returns A pointer to the created object
	 */

	virtual DSIGKeyInfoName * appendKeyName(const XMLCh * name, bool isDName = false) = 0;

	/**
	 * \brief Append an already created EncryptedKey.
	 *
	 * Add an already created EncryptedKey.
	 *
	 * @note The encryptedKey becomes the property of the owning EncryptedType
	 * object and will be deleted upon its destruction.
	 *
	 * @param encryptedKey A pointer to the encrypted Key
	 */

	virtual void appendEncryptedKey(XENCEncryptedKey * encryptedKey) = 0;
	//@}

private:

	// Unimplemented
	XENCEncryptedType(const XENCEncryptedType &);
	XENCEncryptedType & operator = (const XENCEncryptedType &);

};

#endif /* XENCENCRYPTEDTYPE_INCLUDE */
