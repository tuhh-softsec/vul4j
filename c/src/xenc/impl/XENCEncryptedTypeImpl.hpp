/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

/*
 * XSEC
 *
 * XENCEncryptedTypeImpl := Implementation of the EncryptedType interface
 * element
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTEDTYPEIMPL_INCLUDE
#define XENCENCRYPTEDTYPEIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xenc/XENCEncryptedType.hpp>
#include <xsec/dsig/DSIGKeyInfoList.hpp>

// Forward declarations

XSEC_DECLARE_XERCES_CLASS(DOMNode);

class XENCCipherDataImpl;
class XENCEncryptionMethodImpl;
class TXFMChain;
class XSECEnv;

class XENCEncryptedTypeImpl : public XENCEncryptedType {

public:

	XENCEncryptedTypeImpl(const XSECEnv * env);
	XENCEncryptedTypeImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);

	virtual ~XENCEncryptedTypeImpl();

	// Load elements
	void load();

	// Create from scratch.  LocalName is the name of the owning element

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * createBlankEncryptedType(
						XMLCh * localName,
						XENCCipherData::XENCCipherDataType type, 
						const XMLCh * algorithm,
						const XMLCh * value);

	// Interface Methods
	virtual XENCCipherData * getCipherData(void) const;
	virtual DSIGKeyInfoList * getKeyInfoList(void) {return &m_keyInfoList;}
	virtual XENCEncryptionMethod * getEncryptionMethod(void) const;
	virtual void clearKeyInfo(void);
	virtual DSIGKeyInfoName * appendKeyName(const XMLCh * name, bool isDName = false);
	virtual void appendEncryptedKey(XENCEncryptedKey * encryptedKey);

	// Get methods
	virtual const XMLCh * getType(void) const;
	virtual const XMLCh * getMimeType(void) const;
	virtual const XMLCh * getEncoding(void) const;
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const
		{return mp_encryptedTypeElement;};

	// Set methods
	virtual void setType(const XMLCh * uri);
	virtual void setMimeType(const XMLCh * mimeType);
	virtual void setEncoding(const XMLCh * uri);

protected:

	// Create the txfm list - gives as output a TXFM chain with
	// the output being the raw encrypted data

	TXFMChain * createCipherTXFMChain(void);

	// Worker function to start building the KeyInfo list
	void createKeyInfoElement(void);

	const XSECEnv				* mp_env;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement					
								* mp_encryptedTypeElement;	// Node at head of structure
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement
								* mp_keyInfoElement;		// Any underlying KeyInfo
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_cipherDataElement;		// CipherData structure
	XENCCipherDataImpl			* mp_cipherData;
	XENCEncryptionMethodImpl	* mp_encryptionMethod;

	// Hold the XML Digital Signature KeyInfo list
	DSIGKeyInfoList			m_keyInfoList;

	// Type URI
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_typeAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_mimeTypeAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
								* mp_encodingAttr;

	friend class XENCCipherImpl;

private:

	// Un-implemented

	XENCEncryptedTypeImpl();
	XENCEncryptedTypeImpl(const XENCEncryptedTypeImpl &);
	XENCEncryptedTypeImpl & operator = (const XENCEncryptedTypeImpl &);
};

#endif /* XENCENCRYPTEDTYPEIMPL_INCLUDE */
