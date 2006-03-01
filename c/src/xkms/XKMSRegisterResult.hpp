/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 * limitations under the License.
 */

/*
 * XSEC
 *
 * XKMSRegisterResult := Interface for RegisterResult Messages
 *
 * $Id$
 *
 */

#ifndef XKMSREGISTERRESULT_INCLUDE
#define XKMSREGISTERRESULT_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSResultType.hpp>
#include <xsec/xkms/XKMSStatus.hpp>

class XKMSKeyBinding;
class XKMSUnverifiedKeyBinding;
class XKMSRSAKeyPair;

/**
 * @ingroup xkms
 */

/**
 * @brief Interface definition for the RegisterResult elements
 *
 * The \<RegisterResult\> is used by the X-KRSS service to response to
 * a user request to register a new key with the service.
 *
 * The schema definition for RegisterResult is as follows :
 *
 * \verbatim
   <!-- RegisterResult -->
   <element name="RegisterResult" type="xkms:RegisterResultType"/>
   <complexType name="RegisterResultType">
      <complexContent>
         <extension base="xkms:ResultType">
            <sequence>
               <element ref="xkms:KeyBinding" minOccurs="0" 
                     maxOccurs="unbounded"/>
               <element ref="xkms:PrivateKey" minOccurs="0"/>
            </sequence>
         </extension>
      </complexContent>
   </complexType>
   <!-- /RegisterResult -->
\endverbatim
 */

class XKMSRegisterResult : public XKMSResultType {

	/** @name Constructors and Destructors */
	//@{

protected:

	XKMSRegisterResult() {};

public:

	virtual ~XKMSRegisterResult() {};

	/** @name Getter Interface Methods */
	//@{

	/**
	 * \brief Return the element at the base of the message
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void) const = 0;

	//@}

	/** @name KeyBinding handling  */
	//@{

	/**
	 * \brief Find number of KeyBinding elements
	 *
	 * @returns the number of KeyBinding items within the RegisterResult
	 */

	virtual int getKeyBindingSize(void) const = 0;

	/*
	 * \brief Obtain an KeyBinding item
	 *
	 * @param item Index of the item to return (0 = first element)
	 * @returns the KeyBinding referenced by "item"
	 */

	virtual XKMSKeyBinding * getKeyBindingItem(int item) const = 0;

	/*
	 * \brief Append a new KeyBinding element
	 *
	 * Allows callers to append a new KeyBinding item.
	 * The item is initially empty of KeyInfo elements - these must be added
	 * by the caller.
	 *
	 * @param status The status (Valid, Invalid or Indeterminate) of this
	 * key
	 * @returns the newly created KeyBinding object (already inserted
	 * in the RegisterResult
	 */

	virtual XKMSKeyBinding * appendKeyBindingItem(XKMSStatus::StatusValue status) = 0;

	//@}

	/** @name PrivateKey handling  */
	//@{

	/**
	 * \brief Return the RSAKeyPair
	 *
	 * This call requires the passphrase used to encrypt the private key.
	 * The implementation decrypts the PrivateKey element in the
	 * RegisterResult and returns the resulting RSAKeyPair.
	 *
	 * @note The decryption is performed *inside* the RegisterResult, so
	 * this actually modified the contents of the XML.  It should never
	 * be called twice!
	 *
	 * @param passPhrase The local code page version of the pass phrase
	 * @returns the number of KeyBinding items within the RegisterResult
	 */

	virtual XKMSRSAKeyPair * getRSAKeyPair(const char * passPhrase) = 0;

	//@}


private:

	// Unimplemented
	XKMSRegisterResult(const XKMSRegisterResult &);
	XKMSRegisterResult & operator = (const XKMSRegisterResult &);

};

#endif /* XKMSREGISTERRESULT_INCLUDE */
