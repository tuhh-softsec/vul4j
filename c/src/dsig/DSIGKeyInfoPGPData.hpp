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
 * DSIGKeyInfoPGPData := Contains a key pointer to a PGP key
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGKEYINFOPGPDATA_INCLUDE
#define DSIGKEYINFOPGPDATA_INCLUDE

#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMElement);

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief The class for <PGPData> nodes in a KeyInfo list.
 *
 * Class for holding information on a PGP Data node as well as setting
 * such a node in a signature.
 *
 */



class DSIG_EXPORT DSIGKeyInfoPGPData : public DSIGKeyInfo {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor used when XML exists.
	 *
	 * This constructor is used by DSIGSignature objects to load
	 * an existing DOM structure into the PGPData element.
	 *
	 * @param sig Calling signature object.
	 * @param nameNode DOMNode to load information from
	 */

	DSIGKeyInfoPGPData(DSIGSignature * sig, DOMNode *pgpDataNode);

	/**
	 * \brief Constructor used when XML needs to be created.
	 *
	 * This constructor is used by DSIGSignature objects to
	 * create a DSIGKeyInfoPGPData object that can then be used
	 * to create the required XML.
	 *
	 * @param sig Calling signature object.
	 */

	DSIGKeyInfoPGPData(DSIGSignature * sig);

	/**
	 * \brief Destructor
	 */

	virtual ~DSIGKeyInfoPGPData();

	//@}

	/** @name Load and Get functions */
	//@{

	/**
	 * \brief Load an existing XML structure into this object.
	 */

	virtual void load(void);

	/**
	 * \brief Get the PGP Key ID held in the structure
	 *
	 * Returns a pointer to the buffer containing the ID of the PGP key.
	 *
	 * @returns A pointer to the char buffer containing the (base 64 encoded)
	 * PGP Key ID
	 */

	virtual const XMLCh * getKeyID(void) {return mp_keyID;}

	/**
	 * \brief Get the PGP Key Packet
	 *
	 * Returns a pointer to the buffer containing the PGP Key packet.
	 *
	 * @returns A pointer to the XMLCh buffer containing the Key Packet in
	 * base64 encoded format
	 */

	virtual const XMLCh * getKeyPacket(void) {return mp_keyPacket;}

	/**
	 * \Get key name - unimplemented for PGP packets
	 */

	virtual const XMLCh * getKeyName(void) {return NULL;}

	//@}

	/**@name Create and set functions */
	//@{
	
	/**
	 * \brief Create a new PGPData element in the current document.
	 *
	 * Creates a new PGPData element and sets the KeyPacket and KeyID 
	* with the strings passed in.
	 *
	 * @param id Value to set the KeyID as (NULL if not to be set)
	 * @param packet Value to set the key packet to (NULL if not to be set)
	 * @returns The newly created DOMElement with the structure underneath.
	 */

	DOMElement * createBlankPGPData(const XMLCh * id, const XMLCh * packet);

	/**
	 * \brief Set the value of the KeyID to a new string.
	 *
	 * Uses the passed in string to set a new value in the DOM structure.
	 *
	 * @param id The value to set the KeyID to
	 */

	void setKeyID(const XMLCh * id);

	/**
	 * \brief Set the value of the KeyPacket to a new string.
	 *
	 * Uses the passed in string to set a new value in the DOM structure.
	 *
	 * @param packet The value to set the KeyPacket to
	 */

	void setKeyPacket(const XMLCh * packet);

	//@}

	/** @name Information Functions */
	//@{
	
	/**
	 * \brief Return type of this KeyInfo element
	 */
	
	virtual keyInfoType getKeyInfoType(void) {return DSIGKeyInfo::KEYINFO_PGPDATA;}

	//@}

private:

	DSIGKeyInfoPGPData();						// Non-implemented constructor

	const XMLCh			* mp_keyID;				// Key ID (from DOM)
	const XMLCh			* mp_keyPacket;			// key Packet (from DOM)
	DOMNode				* mp_keyIDTextNode;		// Text node containing the ID
	DOMNode				* mp_keyPacketTextNode;	// Text node contianing key packet
};



#endif /* #define DSIGKEYINFOPGPDATA_INCLUDE */
