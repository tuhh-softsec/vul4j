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
 * XENCEncryptedKey := Definition for holder object for EncryptedKey 
 *
 * $Id$
 *
 */

#ifndef XENCENCRYPTEDKEY_INCLUDE
#define XENCENCRYPTEDKEY_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/xenc/XENCEncryptedType.hpp>
#include <xsec/xenc/XENCCipherData.hpp>

/**
 * @ingroup xenc
 * @{
 */

/**
 * @brief Interface definition for the EncryptedKey object
 *
 * The \<EncryptedKey\> element is an abstract type which builds
 * on the EncryptedType element for encrypted data (as opposed to
 * encrypted data).
 *
 * In general, this class should not be used directly.  For most
 * applications, callers will want to use the XENCCipher class
 * instead.
 *
 * The schema definition for EncryptedKey is as follows:
 *
 * \verbatim
  <element name='EncryptedKey' type='xenc:EncryptedKeyType'/>
  <complexType name='EncryptedKeyType'>
    <complexContent>
      <extension base='xenc:EncryptedType'>
        <sequence>
          <element ref='xenc:ReferenceList' minOccurs='0'/>
          <element name='CarriedKeyName' type='string' minOccurs='0'/>
        </sequence>
        <attribute name='Recipient' type='string' use='optional'/>
      </extension>
    </complexContent>   
  </complexType>
\endverbatim
 */


class XENCEncryptedKey : public XENCEncryptedType, public DSIGKeyInfo {

	/** @name Constructors and Destructors */
	//@{

protected:

	// Because we inherit from KeyInfo, we need to implement a slightly different 
	// constructor.

	XENCEncryptedKey(const XSECEnv * env) : DSIGKeyInfo(env) {};

public:

	virtual ~XENCEncryptedKey() {};

	/** @name EncryptedKey Specific Getter Methods */
	//@{

	/**
	 * \brief Get the CarriedKeyName
	 *
	 * EncryptedKey elements MAY have a CarriedKeyName element that links
	 * the EncryptedKey to a KeyName KeyInfo element in another EncryptedKey
	 * or EncryptedData element.
	 * 
	 * This method allows applications to retrieve the Carried Key Name for
	 * the particular EncryptedKey
	 *
	 * @returns A pointer (owned by the library) to the CarriedKeyName string 
	 * (or NULL if none)
	 */

	virtual const XMLCh * getCarriedKeyName(void) const = 0;

	/**
	 * \brief Get the Recipient name
	 *
	 * EncryptedKey elements MAY have a Recipient Attribute on the main
	 * EncryptedKey element that provide a hint to the application as to who
	 * the recipient of the key is.
	 *
	 * This method returns this string in cases where it has been provided
	 *
	 * @returns A pointer (owned by the library) to the Recipient string
	 * (or NULL if none provided).
	 */

	virtual const XMLCh * getRecipient(void) const = 0;

	//@}

	/** @name EncryptedKey Specific Setter Methods */
	//@{

	/**
	 * \brief Set the CarriedKeyName
	 *
	 * EncryptedKey elements MAY have a CarriedKeyName element that links
	 * the EncryptedKey to a KeyName KeyInfo element in another EncryptedKey
	 * or EncryptedData element.
	 * 
	 * This method allows applications to set the Carried Key Name for
	 * the particular EncryptedKey
	 *
	 * @param name String to set in the CarriedKeyName element
	 */

	virtual void setCarriedKeyName(const XMLCh * name) = 0;

	/**
	 * \brief Set the Recipient name
	 *
	 * EncryptedKey elements MAY have a Recipient Attribute on the main
	 * EncryptedKey element that provide a hint to the application as to who
	 * the recipient of the key is.
	 *
	 * This method sets the Recipient string
	 *
	 * @param recipient String to set in the Recipient attribute
	 */

	virtual void setRecipient(const XMLCh * recipient) = 0;

	//@}

private:

	// Unimplemented
	XENCEncryptedKey(const XENCEncryptedKey &);
	XENCEncryptedKey & operator = (const XENCEncryptedKey &);


};

#endif /* XENCENCRYPTEDKEY_INCLUDE */
