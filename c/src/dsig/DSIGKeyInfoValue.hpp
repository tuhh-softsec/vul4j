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

/*
 * XSEC
 *
 * DSIGKeyInfoValue := A value setting
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGKEYINFOVALUE_INCLUDE
#define DSIGKEYINFOVALUE_INCLUDE

#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMElement);

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief The class for \<KeyValue\> nodes in a KeyInfo list.
 *
 * Class for holding information on a KeyValue node as well as setting
 * such a node in a signature.
 *
 * Two types of Value are understood - RSA and DSA.
 *
 * RSA values are fully implemented as per XML-DSig.  They have two parameters
 *
 * <ul>
 * <li><em>Modulus</em> - holds the modulus of this public key; and</li>
 * <li><em>Exponent</em> - holds the exponent.</li>
 * </ul>
 *
 * DSA values have all mandatory parts implemented  - P, Q, G and Y.
 *
 * J, Seed and PgenCounter are not currently implemented.
 */


class DSIG_EXPORT DSIGKeyInfoValue : public DSIGKeyInfo {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor for an existing signature *
	 *
	 * Constructor used when loading a KeyValue node that already exists
	 * in an XML document.
	 *
	 * @param sig Owning signature
	 * @param valueNode DOMNode at head of XML structure
	 */

	DSIGKeyInfoValue(DSIGSignature * sig, DOMNode *valueNode);

	/**
	 * \brief Constructor for creating from scratch
	 *
	 * Constructor used when creating a new KeyValue node to append
	 * to a signature structure.
	 *
	 * @param sig Owning signature
	 */

	DSIGKeyInfoValue(DSIGSignature * sig);

	virtual ~DSIGKeyInfoValue();

	//@}

	/** @name Loading and Get functions */
	//@{

	/**
	 * \brief Load an existing XML structure
	 *
	 * Called by the Signature class when it is reading in
	 * a DOM structure 
	 */

	virtual void load(void);

	/**
	 * \brief Get P value
	 *
	 * @returns a pointer to the DSA P string value.
	 */

	const XMLCh * getDSAP(void) {return mp_PTextNode->getNodeValue();}

	/**
	 * \brief Get Q value
	 *
	 * @returns a pointer to the DSA Q string value.
	 */

	const XMLCh * getDSAQ(void) {return mp_QTextNode->getNodeValue();}

	/**
	 * \brief Get G value
	 *
	 * @returns a pointer to the DSA G string value.
	 */

	const XMLCh * getDSAG(void) {return mp_GTextNode->getNodeValue();}

	/**
	 * \brief Get Y value
	 *
	 * @returns a pointer to the DSA Y string value.
	 */

	const XMLCh * getDSAY(void) {return mp_YTextNode->getNodeValue();}

	/**
	 * \brief Get Modulus
	 *
	 * @returns A pointer to the RSA Modulus
	 */

	const XMLCh * getRSAModulus(void);

	/**
	 * \brief Get Exponent
	 *
	 * @returns A pointer to the buffer containing the RSA Modulus string
	 */

	const XMLCh * getRSAExponent(void);

	//@}

	/** @name DSA Create and set functions */
	//@{

	/**
	 * \brief Create a blank KeyValue (and DOM structure)
	 *
	 * Create a blank KeyValue structure with the passed parameters
	 * and create the required DOM structure as well.
	 *
	 * @param P The P value (base64 encoded in unicode format)
	 * @param Q The Q value (base64 encoded in unicode format)
	 * @param G The G value (base64 encoded in unicode format)
	 * @param Y The Y value (base64 encoded in unicode format)
	 * @returns the DOMElement at the head of the DOM structure
	 */
	
	DOMElement * createBlankDSAKeyValue(const XMLCh * P,
		const XMLCh * Q,
		const XMLCh * G,
		const XMLCh * Y);

	/**
	 * \brief Set P value.
	 *
	 * Take the provided string and use it to set the P parameter
	 * in the KeyValue
	 */

	void setDSAP(const XMLCh * P);

	/**
	 * \brief Set Q value.
	 *
	 * Take the provided string and use it to set the Q parameter
	 * in the KeyValue
	 */

	void setDSAQ(const XMLCh * Q);

	/**
	 * \brief Set G value.
	 *
	 * Take the provided string and use it to set the G parameter
	 * in the KeyValue
	 */

	void setDSAG(const XMLCh * G);

	/**
	 * \brief Set Y value.
	 *
	 * Take the provided string and use it to set the Y parameter
	 * in the KeyValue
	 */

	void setDSAY(const XMLCh * Y);

	//@}

	/** @name RSA Create and Set functions */
	//@{

	/**
	 * \brief Create a blank RSA KeyValue
	 *
	 * Create a new RSA Value object and associated DOM structures.
	 *
	 * @param modulus Base64 encoded value of Modulus to set
	 * @param exponent Base64 encoded value of the Exponent to set
	 * @returns The DOM structure of the RSAValue that has been created
	 */
	
	DOMElement * DSIGKeyInfoValue::createBlankRSAKeyValue(const XMLCh * modulus,
		const XMLCh * exponent);

	/**
	 * \brief Set the Modulus
	 *
	 * Set the base64 encoded string of the Modulus value within the element.
	 *
	 * @param modulus Base64 encoded value to set
	 */

	void DSIGKeyInfoValue::setRSAModulus(const XMLCh * modulus);

	/**
	 * \brief Set the exponent
	 *
	 * Set the base64 encoded string of the exponent value within the element
	 *
	 * @param exponent Base64 encoded value to set
	 */

	void DSIGKeyInfoValue::setRSAExponent(const XMLCh * exponent);

	//@}

	/** @name Information Functions */
	//@{

	/**
	 * \brief Interface call to return type
	 *
	 * @returns the type of this keyInfo node
	 */

	virtual keyInfoType getKeyInfoType(void);

	/**
	 * \brief Get the name of this key (irrelevant for a KeyValue)
	 *
	 * @returns NULL
	 */

	virtual const XMLCh * getKeyName(void);

	//@}
private:

	// Structures to hold ALL the required information
	// Not the most efficient of methods, but simple.

	// DSA

	DOMNode				* mp_PTextNode;	// Nodes where strings are stored
	DOMNode				* mp_QTextNode;
	DOMNode				* mp_GTextNode;
	DOMNode				* mp_YTextNode;

	// RSA
	
	DOMNode				* mp_modulusTextNode;
	DOMNode				* mp_exponentTextNode;

	// General

	DOMNode				* mp_valueNode;
	keyInfoType			m_keyInfoType;
};

#endif /* #define DSIGKEYVALUE_INCLUDE */
