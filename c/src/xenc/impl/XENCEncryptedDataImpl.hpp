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
 * XENCEncryptedDataImpl := Implementation for holder object for EncryptedData 
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTEDDATAIMPL_INCLUDE
#define XENCENCRYPTEDDATAIMPL_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xenc/XENCEncryptedData.hpp>

#include "XENCCipherImpl.hpp"
#include "XENCEncryptedTypeImpl.hpp"

XSEC_DECLARE_XERCES_CLASS(DOMNode);

class XENCEncryptedDataImpl : public XENCEncryptedData, public XENCEncryptedTypeImpl {

public:

	XENCEncryptedDataImpl(const XSECEnv * env);
	XENCEncryptedDataImpl(
		const XSECEnv * env, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * node
	);
	virtual ~XENCEncryptedDataImpl();

	void load(void);

	// Create a blank EncryptedData DOM structure

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankEncryptedData(XENCCipherData::XENCCipherDataType type, 
								 const XMLCh * algorithm,
								 const XMLCh * value);

	// Interface methods

	// Inherited from XENCEncryptedData - need to re-implement
	virtual XENCCipherData * getCipherData(void) const
		{return XENCEncryptedTypeImpl::getCipherData();}
	virtual DSIGKeyInfoList * getKeyInfoList(void)
		{return XENCEncryptedTypeImpl::getKeyInfoList();}
	virtual XENCEncryptionMethod * getEncryptionMethod(void) const
		{return XENCEncryptedTypeImpl::getEncryptionMethod();}
	virtual void clearKeyInfo(void)
		{XENCEncryptedTypeImpl::clearKeyInfo();}
	virtual DSIGKeyInfoName * appendKeyName(const XMLCh * name, bool isDName = false)
		{return XENCEncryptedTypeImpl::appendKeyName(name, isDName);}
	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const
		{return XENCEncryptedTypeImpl::getElement();}
	virtual void appendEncryptedKey(XENCEncryptedKey * encryptedKey)
		{XENCEncryptedTypeImpl::appendEncryptedKey(encryptedKey);}

	// Get methods
	virtual const XMLCh * getType(void) const
		{return XENCEncryptedTypeImpl::getType();}
	virtual const XMLCh * getMimeType(void) const
		{return XENCEncryptedTypeImpl::getMimeType();}
	virtual const XMLCh * getEncoding(void) const
		{return XENCEncryptedTypeImpl::getEncoding();}

	// Set methods
	virtual void setType(const XMLCh * uri)
		{XENCEncryptedTypeImpl::setType(uri);}
	virtual void setMimeType(const XMLCh * mimeType)
		{XENCEncryptedTypeImpl::setMimeType(mimeType);}
	virtual void setEncoding(const XMLCh * uri)
		{XENCEncryptedTypeImpl::setEncoding(uri);}

private:

	// Unimplemented
	XENCEncryptedDataImpl(void);
	XENCEncryptedDataImpl(const XENCEncryptedDataImpl &);
	XENCEncryptedDataImpl & operator = (const XENCEncryptedDataImpl &);

};

#endif /* XENCENCRYPTEDDATAIMPL_INCLUDE */
