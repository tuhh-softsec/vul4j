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
 * XSECCryptoProvider := Base virtual class to define a hash algorithm
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#ifndef XSECCRYPTOHASH_INCLUDE
#define XSECCRYPTOHASH_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>

/**
 * @ingroup crypto
 * @{
 */

/**
 * \brief Interface definition for Hash functions.
 *
 * The XSECCryptoHash function is used by the library to manipulate
 * Hashing (Digest) functions.
 *
 * It is also used as the base class for HMAC functions, and thus has
 * a #setKey() method.
 *
 * @todo bring the interface here in-line with that provided for
 * XSECCryptoBase64 - really should have only one way of calling these
 * kinds of objects.
 *
 */

class DSIG_EXPORT XSECCryptoHash {


public :

	/**
	 * \brief Enumeration of Hash (Digest) types
	 *
	 * The hash types known to XSEC
	 */

	enum HashType {

		HASH_NONE			= 0,
		HASH_SHA1			= 1,
		HASH_MD5			= 2

	};

	// Constructors/Destructors
	
	XSECCryptoHash() {};
	virtual ~XSECCryptoHash() {};

	/** @name Digest/Hash functions */
	//@{

	/**
	 * \brief Rest the hash function
	 *
	 * XSEC will call the #reset() function prior to re-using a CryptoHash
	 * object.
	 */

	virtual void		reset(void) = 0;					// Reset the hash

	/**
	 * \brief Hash some data.
	 *
	 * Take length bytes of data from the data buffer and update the hash
	 * that already exists.  This function may (and normally will) be called
	 * many times for large blocks of data.
	 *
	 * @param data The buffer containing the data to be hashed.
	 * @param length The number of bytes to be read from data
	 */

	virtual void		hash(unsigned char * data, 
							 unsigned int length) = 0;

	/**
	 * \brief Finish up a Digest operation and read the result.
	 *
	 * This call tells the CryptoHash object that the input is complete and
	 * to finalise the Digest.  The output of the digest is read into the 
	 * hash buffer (at most maxLength bytes)
	 *
	 * @param hash The buffer the hash should be read into.
	 * @param maxLength The maximum number of bytes to be read into hash
	 * @returns The number of bytes copied into the hash buffer
	 */

	virtual unsigned int finish(unsigned char * hash,
								unsigned int maxLength) = 0;// Finish and get hash

	//@}

	/** @name Information functions */
	//@{

	/**
	 *\brief
	 *
	 * Determine the hash type of this object
	 *
	 * @returns The hash type
	 */

	virtual HashType getHashType(void) = 0;

	//@}

	/** @name HMAC Functions */
	//@{
	
	/**
	 *\brief
	 *
	 * The HMAC classes are treated in the library as standard hash
	 * objects that just happen to take a key.  Thus all hash functions
	 * implement this function (potentially just to throw an exception)
	 *
	 * Sets the key - which needs to have a base class of 
	 * XSECCryptoKeyHMAC.
	 *
	 * @param key The key the HMAC function should use.
	 */

	virtual void		setKey(XSECCryptoKey * key) = 0;

	//@}

};

#endif /* XSECCRYPTOHASH_INCLUDE */
