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
 * XSECAlgorithmHandler := Interface class to define handling of
 *						   encryption and signature algorithms
 *
 * $Id$
 *
 */

#ifndef XSECALGHANDLER_INCLUDE
#define XSECALGHANDLER_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

class TXFMChain;
class XENCEncryptionMethod;
class XSECCryptoKey;
class safeBuffer;
class XSECBinTXFMInputStream;

XSEC_DECLARE_XERCES_CLASS(DOMDocument);

// Xerces

/**
 * @ingroup xenc
 *\@{*/



/**
 * @brief Interface class to provide handlers for processing different
 * encryption types.
 *
 * The XENCCipher class allows users and callers to register algorithm
 * handlers for different Type URIs, as defined in the EncryptionMethod
 * element within XML Encryption.
 *
 * A default handler (XENCAlgorithmHandlerDefault) is provided by the
 * library, and is used to process all algorithms defined as mandatory
 * (and many optional) within the standard.
 *
 * Users can extend this class to provide custom algorithm handlers
 * for their own classes.
 *
 * @note The library will use a single clone of any provided object for
 * a given algorithm.  So all implementation classes <b>must</b> be 
 * thread safe!
 */

class XSECAlgorithmHandler {

public:
	
	/** @name Constructors and Destructors */
	//@{
	
	virtual ~XSECAlgorithmHandler() {};

	//@}

	/** @name Encryption Methods */
	//@{

	/**
	 * \brief Encrypt an input Transform chain to a safeBuffer.
	 *
	 * This method takes a TXFMChain that will provide the plain
	 * text data, and places the encrypted and base64 encoded output 
	 * in a safeBuffer.
	 *
	 * The EncryptionMethod object is provided so that any algorithm
	 * specific parameters can be embedded by the processor.  Default
	 * parameters can be set directly (OAEParams and KeySize).  Anything
	 * additional will need to be set within the DOM directly.
	 *
	 * @param plainText Chain that will provide the plain bytes.  Ownership
	 * remains with the caller - do not delete.
	 * @param encryptionMethod Information about the algorithm to use.
	 * Can also be used to set the required encryption parameters
	 * @param key The key that has been provided by the calling 
	 * application to perform the encryption.
	 *
	 * @note This is not quite the symmetric opposite of decryptToSafeBuffer
	 * because of the way the library uses transformers.  It is expected
	 * that this method will create a safeBuffer with <b>base64</b> encoded
	 * data.  (It's easier to throw a TXFMBase64 txfmer on the end of the
	 * chain than to do the encryption and then separately base64 encode.)
	 */

	virtual bool encryptToSafeBuffer(
		TXFMChain * plainText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
	) = 0;

	//@}

	/** @name Decryption Methods */
	//@{

	/**
	 * \brief Decrypt an input Transform chain to a safeBuffer.
	 *
	 * This method takes a TXFMChain that will provide the cipher
	 * text data, and places the output in a safeBuffer.
	 *
	 * The EncryptionMethod object is provided so that any algorithm
	 * specific parameters can be found by the processor.  It also
	 * allows applications to embed multiple algorithms in a single
	 * processing object.  The Type value can then be read from the
	 * EncryptionMethod object to determine what to do.
	 *
	 * @param cipherText Chain that will provide the cipherText.
	 * Ownership remains with the caller - do not delete.
	 * @param encryptionMethod Information about the algorithm to use
	 * @param key The key that has been determined via a resolver or
	 * that has been provided by the calling application.
	 * @returns The number of plain bytes placed in the safeBuffer
	 */

	virtual unsigned int decryptToSafeBuffer(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc,
		safeBuffer & result
	) = 0;

	/**
	 * \brief Append an appropriate decrypt TXFMer to a cipher txfm chain.
	 *
	 * This method takes a TXFMChain that will provide the cipher
	 * text data, and appends the appropriate cipher transformer to
	 * decrypt the output.
	 *
	 * The EncryptionMethod object is provided so that any algorithm
	 * specific parameters can be found by the processor.  It also
	 * allows applications to embed multiple algorithms in a single
	 * processing object.  The Type value can then be read from the
	 * EncryptionMethod object to determine what to do.
	 *
	 * @param cipherText Chain that will provide the cipherText.
	 * Ownership remains with the caller - do not delete.
	 * @param encryptionMethod Information about the algorithm to use
	 * @param key The key that has been determined via a resolver or
	 * that has been provided by the calling application.
	 * @returns The resulting BinInputStream
	 */

	virtual bool appendDecryptCipherTXFM(
		TXFMChain * cipherText,
		XENCEncryptionMethod * encryptionMethod,
		XSECCryptoKey * key,
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc
	) = 0;


	//@}

	/** @name Key handling */
	//@{

	/**
	 * \brief Create a key that will support a given URI
	 *
	 * Given a URI string and a raw bit string, create the associated key
	 *
	 * @param uri URI string to match key to
	 * @param keyBuffer Raw bits to set in the key
	 * @param keyLen Number of bytes in the key
	 */

	virtual XSECCryptoKey * createKeyForURI(
		const XMLCh * uri,
		unsigned char * keyBuffer,
		unsigned int keyLen
	) = 0;

	//@}

	/** @name Miscellaneous Functions */
	//@{

	/**
	 * \brief Create a new instance of the handler
	 *
	 * Provides a means for the library to create a new instance
	 * of the object without knowing its type
	 */

	virtual XSECAlgorithmHandler * clone(void) const = 0;

	//@}

};

/*\@}*/

#endif /* XSECALGHANDLER_INCLUDE*/

