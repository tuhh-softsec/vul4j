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
 * XKMSMessageFactory := Interface for the factory used to produce XKMS msgs
 *
 * $Id$
 *
 */

#ifndef XKMSMESSAGEFACTORY_INCLUDE
#define XKMSMESSAGEFACTORY_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/xkms/XKMSMessageAbstractType.hpp>

class DSIGSignature;

XSEC_DECLARE_XERCES_CLASS(DOMElement);

/**
 * @defgroup xkms XML Key Management System Implementation
 *
 * <p>The classes in this group implement the W3C XKMS 2.0
 * specification.  Users should interact with these classes
 * via the XKMSMessageFactory class (for consuming and producing
 * XKMS messages) and the XKMSClient class (for actually
 * performing XKMS calls to a server)</p>
 *
 *\@{*/

/**
 * @brief Interface definition for the XKMSMessageFactory class
 *
 * The XKMSMessageFactory classes are used to provide an interface
 * to applicataions to produce and consume XKMS messages.  No logic
 * is defined within the class, other than that necessary to (for
 * example) generate a response message using a request message as
 * a base.
 *
 * Client apps that wish to make use of XKMS should generally use
 * the XKMSClient class.
 */


class XKMSMessageFactory {

	/** @name Constructors and Destructors */
	//@{

protected:

	XKMSMessageFactory() {};

public:

	virtual ~XKMSMessageFactory() {};

	/** @name Methods to build message objects from existing XML  */
	//@{

	/**
	 * \brief Load a message from an existing XML document
	 *
	 * Reads in the XML document and produces the corresponding XKMS
	 * message object.  Callers should make use of the ::getMessageType
	 * method to determine what type of message they are actually
	 * working with.
	 *
	 * @param elt The element at the head of the XKMS structure
	 */

	virtual XKMSMessageAbstractType * newMessageFromDOM(
		XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * elt) = 0;


	//@}

	/** @name Environment Manipulation Functions */
	//@{

	/**
	  * \brief Set the prefix be used for the DSIG namespace.
	  *
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Digital Signature nodes
	  */

	virtual void setDSIGNSPrefix(const XMLCh * prefix) = 0;

	/**
	  * \brief Set the prefix be used for the Exclusive Canonicalisation namespace.
	  *
	  * The Exclusive Canonicalisation specification defines a new namespace for the
	  * InclusiveNamespaces node.  This function can be used to set the prefix
	  * that the library will use when creating nodes within this namespace.
	  *
	  * <p>xmlns:ds="http://www.w3.org/2001/10/xml-exc-c14n#"</p>
	  *
	  * If no prefix is set, the default namespace will be used
	  *
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Exclusive Canonicalisation nodes
	  */

	virtual void setECNSPrefix(const XMLCh * prefix) = 0;

	/**
	  * \brief Set the prefix be used for the XPath-Filter2 namespace.
	  *
	  * The XPathFilter definition uses its own namespace.  This
	  * method can be used to set the prefix that the library will use
	  * when creating elements in this namespace
	  *
	  * <p>xmlns:ds="http://www.w3.org/2002/06/xmldsig-filter2"</p>
	  *
	  * If no prefix is set, the default namespace will be used
	  *
	  * @param prefix The UTF-16 encoided NS prefix to use for the XPath
	  * filter nodes
	  */

	virtual void setXPFNSPrefix(const XMLCh * prefix) = 0;

	/**
	  * \brief Set the prefix be used for the XML Encryption namespace.
	  *
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML
	  * Encryption nodes
	  */

	virtual void setXENCNSPrefix(const XMLCh * prefix) = 0;

	/**
	  * \brief Set the prefix be used for the XKMS Namespace
	  *
	  * @param prefix The UTF-16 encoided NS prefix to use for the XKMS
	  * nodes
	  */

	virtual void setXKMSNSPrefix(const XMLCh * prefix) = 0;

	//@}

private:

	// Unimplemented
	XKMSMessageFactory(const XKMSMessageFactory &);
	XKMSMessageFactory & operator = (const XKMSMessageFactory &);

};

#endif /* XKMSMESSAGEFACTORY_INCLUDE */
