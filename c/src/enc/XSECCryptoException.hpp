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
 * XSECCryptoException:= How we throw exceptions in XSEC
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */
#ifndef XSECCRYPTOEXCEPTION_INCLUDE
#define XSECCRYPTOEXCEPTION_INCLUDE

#include <xsec/utils/XSECSafeBuffer.hpp>

#include <stdlib.h>

/**
 * @ingroup crypto
 */

/**
 *\brief Exception strings
 *
 * Global array of strings that can be looked up using the #type element
 * of an XSECCryptoException to determine a default string for the erro
 */


extern const char * XSECCryptoExceptionStrings[];

/**
 * @ingroup crypto
 */

/**
 * \brief Exception class used by the cryptographic modules
 *
 * This exception class should be used by the Cryptographic providers
 * for reporting errors. It can be used directly or by classes which
 * inherit from it.
 *
 * It's a fairly standard exception class, with a type parameter
 * (and associated enumerated type) and a string that can be used to
 * provide more information to the caller.
 */

class DSIG_EXPORT XSECCryptoException {

public:

	/**
	 * \brief Defines the error type
	 *
	 * Enumerated type that can be looked up by the receiver of the
	 * exception to determine what the error was.
	 *
	 * Can also be used as a lookup into the XSECCryptoExceptionStrings
	 * global array for a default error description
	 */

	enum XSECCryptoExceptionType {

		None						= 0,
		GeneralError				= 1,
		MDError						= 2,		// Error in Message Digest
		Base64Error					= 3,		// Error in a Base64 operation
		MemoryError					= 4,		// Memory allocation error
		X509Error					= 5,		// X509 problem
		DSAError					= 6,		// DSA Error
		RSAError					= 7,		// RSA Error
		UnknownError				= 8			// Must be last!

	};

	
public:

	/** @name Contructors and Destructors */
	//@{

	/**
	 * \brief Common constructur
	 *
	 * Construct an exception with the given type and (possibly blank)
	 * message.
	 *
	 * @param eNum The error type
	 * @param inMsg The message to be passed to the caller.  If NULL
	 * this will be set to the associated value in 
	 * XSECCryptoExceptionStrings
	 */

	XSECCryptoException(XSECCryptoExceptionType eNum, char * inMsg = NULL);

	/**
	 * \brief safeBuffer constructor
	 *
	 * As for the common constructor, but passes in a safeBuffer message
	 *
	 * @param eNum The error type
	 * @param inMsg The message string to be passed to the caller.
	 */

	XSECCryptoException(XSECCryptoExceptionType eNum, safeBuffer &inMsg);

	/**
	 * \brief Copy Constructor.
	 *
	 * @param toCopy The exception to be copied into the new exception
	 */

	XSECCryptoException(const XSECCryptoException &toCopy);
	~XSECCryptoException();

	//@}

	/** @name Get Methods */
	//@{

	/**
	 * \brief Get the message
	 *
	 * Allows the receiver of the exception to get the error message.
	 *
	 * @returns A pointer to the char buffer holding the error string
	 */

	const char * getMsg(void);

	/**
	 * \brief Get the error type
	 *
	 * @returns The error type of the error that caused the exception
	 */

	//@} 

	XSECCryptoExceptionType getType(void);

private:

	char				* msg;				// Message to the caller
	XSECCryptoExceptionType	type;				// Type of exception
	XSECCryptoException();

};


#endif /* XSECCryptoEXCEPTION_HEADER */
