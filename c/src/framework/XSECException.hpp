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
 * XSECException:= How we throw exceptions in XSEC
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XSECEXCEPTION_INCLUDE
#define XSECEXCEPTION_INCLUDE

#include <xsec/framework/XSECDefs.hpp>

/**
 * @addtogroup pubsig
 * @{
 */

/**
 * @brief Exception Class.
 *
 * <p>This class is used for all Exceptions raised by the XSEC library.
 * It holds a "type" number that can be used to find the generic type
 * of the exception, as well as a XMLCh string that holds a description of
 * the error</p>
 *
 */

class DSIG_EXPORT XSECException {

public:

	/**
	 * \brief Type of Exception thrown.
	 *
	 * This enumerated type is used to inform the recipient of the 
	 * exception what generic error caused it to be raised.
	 */

	enum XSECExceptionType {

		None						= 0,
		MemoryAllocationFail		= 1,
		NoHashFoundInDigestValue	= 2,
		UnknownDSIGAttribute		= 3,
		ExpectedDSIGChildNotFound	= 4,
		UnknownTransform			= 5,
		TransformInputOutputFail	= 6,
		IDNotFoundInDOMDoc			= 7,
		UnsupportedXpointerExpr		= 8,
		XPathError					= 9,
		XSLError					= 10,
		Unsupported					= 11,
		LoadEmptySignature			= 12,
		LoadNonSignature			= 13,
		UnknownCanonicalization		= 14,
		UnknownSignatureAlgorithm	= 15,
		LoadEmptyX509				= 16,
		LoadNonX509					= 17,
		OpenSSLError				= 18,
		SigVfyError					= 19,
		LoadEmptySignedInfo			= 20,
		LoadNonSignedInfo			= 21,
		ExpectedReferenceURI		= 22,
		NotLoaded					= 23,
		CryptoProviderError			= 24,
		KeyInfoError				= 25,
		SigningError				= 26,
		LoadEmptyInfoName			= 27,
		LoadNonInfoName				= 28,
		UnknownKeyValue				= 29,
		SignatureCreationError		= 30,
		ErrorOpeningURI				= 31,
		ProviderError				= 32,
		InternalError				= 33,
		EnvelopeError				= 34,
		UnsupportedFunction			= 35,
		TransformError				= 36,
		SafeBufferError				= 37,
		HTTPURIInputStreamError     = 38,
		LoadEmptyXPathFilter		= 39,
		XPathFilterError			= 40,
		UnknownError				= 41		// Must be last!

	};

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief General Constructor
	 *
	 * Generic constructor used within the library.  Where inMsg == NULL,
	 * the general string for this exception type will be used.
	 *
	 * @param eNum Exception type
	 * @param inMsg Msg to be used or NULL for general system message
	 */

	XSECException(XSECExceptionType eNum, const XMLCh * inMsg = NULL);

	/**
	 * \brief Local code page constructor
	 *
	 * Shortcut constructor to allow local code page strings to be used
	 * for the message.  Strings are converted to UTF-16.
	 *
	 * @param eNum Exception type
	 * @param inMsg Msg to be used or NULL for general system message
	 */

	XSECException(XSECExceptionType eNum, const char * inMsg);

	// XSECException(XSECExceptionType eNum, safeBuffer &inMsg);
	
	/**
	 * \brief Copy Constructor
	 */

	XSECException(const XSECException &toCopy);
	/**
	 * \brief Destructor
	 */

	~XSECException();
	
	//@}

	/** @name Information functions */
	//@{

	/**
	 * \brief Get message
	 *
	 * Return a pointer to the XMLCh buffer holding the error message
	 *
	 * @returns A pointer to the buffer within the exception that holds the
	 * error message */

	const XMLCh * getMsg(void);

	/**
	 * \brief Get error type
	 *
	 * Returns an XSECExceptionType coding of the generic error that raised
	 * this exception
	 *
	 * @returns The excetpion type
	 */

	XSECExceptionType getType(void);

private:

	XMLCh				* msg;				// Message to the caller
	XSECExceptionType	type;				// Type of exception
	
	/* Unimplemented Constructor */
	XSECException();


};

/** @}*/

#endif /* XSECEXCEPTION_INCLUDE */
