/*
 * Copyright 2004 The Apache Software Foundation.
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
 * XKMSKeyBindingAbstractType := Interface for base schema of XKMS messages
 *
 * $Id$
 *
 */

#ifndef XKMSKEYBINDINGABSTRACTTYPE_INCLUDE
#define XKMSKEYBINDINGABSTRACTTYPE_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

class DSIGKeyInfoList;

XSEC_DECLARE_XERCES_CLASS(DOMElement);

/**
 * @ingroup xkms
 * @{
 */

/**
 * @brief Interface definition for the KeyBindingAbstractType
 *
 * The \<KeyBindingAbstractType\> is an abstract type on which all
 * KeyBinding components are build.
 *
 * The schema definition for KeyBindingAbstractType is as follows :
 *
 * \verbatim
   <!-- KeyBindingAbstractType-->
   <complexType name="KeyBindingAbstractType" abstract="true">
      <sequence>
         <element ref="ds:KeyInfo" minOccurs="0"/>
         <element ref="xkms:KeyUsage" minOccurs="0" maxOccurs="3"/>
         <element ref="xkms:UseKeyWith" minOccurs="0" 
               maxOccurs="unbounded"/>
      </sequence>
      <attribute name="Id" type="ID" use="optional"/>
   </complexType>
   <!-- /KeyBindingAbstractType-->
\endverbatim
 */


class XKMSKeyBindingAbstractType {

	/** @name Constructors and Destructors */
	//@{

protected:

	XKMSKeyBindingAbstractType() {};

public:

	virtual ~XKMSKeyBindingAbstractType() {};

	/** @name Getter Interface Methods */
	//@{

	/*
	 * \brief Obtain the base Element for this structure
	 *
	 * @returns the Element node at the head of the DOM structure
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const = 0;

	/**
	 * \brief Get the Id for the KeyBinding
	 *
	 * All KeyBinding elements MAY have an Id attribute that 
	 * identifies the binding.  This method is used to retrieve a pointer
	 * to the Id string.
	 *
	 * @returns a pointer to the Id string (owned by the library)
	 */

	virtual const XMLCh * getId(void) const = 0;

	/**
	 * \brief Obtain the KeyInfo elements for this KeyBinding
	 *
	 * KeyBinding elements may provide information about the keys that are being
	 * bound.
	 *
	 * @returns A KeyInfoList object containing all the KeyInfo elements
	 */

	virtual DSIGKeyInfoList * getKeyInfoList(void) const = 0;

	/**
	 * \brief Determine if an Encryption key usage is set
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.
	 *
	 * @returns whether the Encryption KeyUsage element is set
	 */

	virtual bool getEncryptionKeyUsage(void) const = 0;

	/**
	 * \brief Determine if an Signature key usage is set
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.
	 *
	 * @returns whether the Signature KeyUsage element is set
	 */

	virtual bool getSignatureKeyUsage(void) const = 0;

	/**
	 * \brief Determine if an Exchange key usage is set
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.
	 *
	 * @returns whether the Exchange KeyUsage element is set
	 */

	virtual bool getExchangeKeyUsage(void) const = 0;

	//@}

	/** @name Setter interface methods */
	//@{

	/**
	 * \brief Set the Id URI for the Message
	 *
	 * Allows a calling application to set a new Id for the
	 * KeyBinding
	 *
	 * @param id The Id to set
	 */

	virtual void setId(const XMLCh * id) = 0;

	/**
	 * \brief Set Encryption key usage on
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.  When first created, all these elements
	 * are off (which indicates that all are permitted).
	 *
	 * This call activates the Encryption key usage for this KeyBinding
	 */

	virtual void setEncryptionKeyUsage(void) = 0;

	/**
	 * \brief Set Signature key usage on
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.  When first created, all these elements
	 * are off (which indicates that all are permitted).
	 *
	 * This call activates the Signature key usage for this KeyBinding
	 */

	virtual void setSignatureKeyUsage(void) = 0;

	/**
	 * \brief Set Exchange key usage on
	 *
	 * KeyBinding elements may define Encryption, Signature of Exchange as being
	 * permitted for a particular key.  When first created, all these elements
	 * are off (which indicates that all are permitted).
	 *
	 * This call activates the Exchange key usage for this KeyBinding
	 */

	virtual void setExchangeKeyUsage(void) = 0;

	//@}

private:

	// Unimplemented
	XKMSKeyBindingAbstractType(const XKMSKeyBindingAbstractType &);
	XKMSKeyBindingAbstractType & operator = (const XKMSKeyBindingAbstractType &);

};

#endif /* XKMSKEYBINDINGABSTRACTTYPE_INCLUDE */
