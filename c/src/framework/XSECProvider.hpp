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
 * XSECProvider.hpp := The main interface for users wishing to gain access
 *                     to signature objects
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */
#ifndef XSECPROVIDER_INCLUDE
#define XSECPROVIDER_INCLUDE

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/xenc/XENCCipher.hpp>

#include <xercesc/util/Mutexes.hpp>

#include <vector>

/**
 * @addtogroup pubsig
 * @{
 */

/**
 * @brief The main provider class for XML Digital Signatures and Encryption objects.
 *
 * <p>The XSECProvider class is used to create and destroy signature objects and
 * encryption objects.  It provides a number of methods to create signature 
 * and encryption objects for a variety of situations - in particular creating an 
 * empty signature or cipher with which to create the DOM structure or creating a 
 * security object based on an already existing DOM structure.</p>
 *
 */


class DSIG_EXPORT XSECProvider {

#if defined(XALAN_NO_NAMESPACES)
	typedef vector<DSIGSignature *>			SignatureListVectorType;
#else
	typedef std::vector<DSIGSignature *>	SignatureListVectorType;
#endif

#if defined(XALAN_NO_NAMESPACES)
	typedef vector<XENCCipher *>			CipherListVectorType;
#else
	typedef std::vector<XENCCipher *>		CipherListVectorType;
#endif

public:

    /** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Default constructor.
	 *
	 * <p>The provider class requires no parameters for construction</p>
	 *
	 */


	XSECProvider();
	~XSECProvider();

	//@}

    /** @name Signature Creation Classes */
    //@{
	
    /**
	 * \brief DSIGSignature creator for use with existing XML signatures or templates.
	 *
	 * <p>Create a DSIGSignature object based on an already existing
	 * DSIG Signature XML node.  It is assumed that the underlying
	 * DOM structure is in place and works correctly.</p>
	 *
	 * <p>In this case, the caller can pass in the signature DOM Node for cases
	 * where there may be more than one signature in a document.  The caller
	 * needs to specify which signature tree is to be used.</p>
	 *
	 * @param doc The DOM document node in which the signature is embedded.
	 * @param sigNode The DOM node (within doc) that is to be used as the 
	 *	base of the signature.
	 * @see DSIGSignature#load
	 */

	DSIGSignature * newSignatureFromDOM(
		XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc, 
		XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *sigNode
	);

    /**
	 * \brief DSIGSignature creator for use with existing XML signatures or templates.
	 *
	 * <p>Create a DSIGSignature object based on an already existing
	 * DSIG Signature XML node.  It is assumed that the underlying
	 * DOM structure is in place and works correctly.</p>
	 *
	 * <p>In this case, the XML-Security libraries will find the signature
	 * node.</p>
	 *
	 * @note The library will <em>only</em> find and use the first signature node
	 * in the document.  If there are more, they will not be validated
	 * @param doc The DOM document node in which the signature is embedded.
	 * @see DSIGSignature#load
	 */
	
	DSIGSignature * newSignatureFromDOM(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc);

    /**
	 * \brief DSIGSignature creator for creating new XML signatures.
	 *
	 * <p>Create an empty DSIGSignature object that can be used to create new
	 * signature values.  The returned signature object needs to be initialised
	 * with a document so a blank signature DOM structure can be created</p>
	 *
	 * @see DSIGSignature#createBlankSignature
	 */
	
	DSIGSignature * newSignature(void);

    /**
	 * \brief Method for destroying DSIGSignature objects created via this provider.
	 *
	 * <p>The provider keeps track of all signature objects created during the lifetime
	 * of the provider.  This method can be called to delete a signature whilst the 
	 * provider is still in scope.  Otherwise the objects will be automatically
	 * deleted when the provider object goes out of scope.</p>
	 * 
	 * <p>In cases where the DSIGSignature has been used to create a new DOM structure,
	 * it can be safely deleted once the signature operations have been completed without
	 * impacting the underlying DOM structure.</p>
	 *
	 * @param toRelease The DSIGSignature object to be deleted.
	 * @todo The DSIGSignature objects are fairly bulky in terms of creation and deletion.
	 * There should be a capability to store "released" objects in a re-use stack.  At the
	 * moment the Provider class simply deletes the objects.
	 * @see DSIGSignature#createBlankSignature
	 */

	void releaseSignature(DSIGSignature * toRelease);

	//@}

	/** @name Encryption Creation Functions */
	//@{

	/**
	 * \brief Create an XENCCipher object based on a particular DOM Document
	 *
	 * XENCCipher is an engine class that is used to wrap encryption/decryption
	 * functions.  Unlike the Signature functions, only a XENCCipher object attached
	 * to a particular document is required.  Arbitrary objects within this document
	 * can then be encrypted/decrypted using this class.
	 *
	 * @param doc Document to attach the XENCCipher to.
	 * @returns An implementation object for XENCCipher
	 */

	XENCCipher * newCipher(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc);

	/**
	 * \brief Method to delete XENCCipher objects created via this provider
	 *
	 * <p>The provider keeps track of all objects by it.  This method can be used
	 * to delete any previously created XENCCipher objects prior to the provider
	 * being deleted.  Any XENCCipher objects not released using this function will
	 * automatically be deleted when the provider goes out of scope (or is itself
	 * deleted).
	 *
	 * @param toRelease The XENCCipher object to be deleted
	 */

	void releaseCipher(XENCCipher * toRelease);

	//@}

	/** @name Environmental Options */
	//@{

	/**
	 * \brief Set the default URIResolver.
	 *
	 * DSIGSignature objects require a URIResolver to allow them to de-reference
	 * URIs in reference elements.
	 *
	 * This function sets the resolver that will be used for all
	 * signatures created after this is set.  The resolver is
	 * cloned, so the object passed in can be safely deleted once the
	 * function has been completed.
	 */

	void setDefaultURIResolver(XSECURIResolver * resolver);

	//@}

private:

	// Internal functions

	void setup(DSIGSignature *sig);
	void setup(XENCCipher *cipher);

	SignatureListVectorType						m_activeSignatures;
	CipherListVectorType						m_activeCiphers;
	XSECURIResolver								* mp_URIResolver;
	XERCES_CPP_NAMESPACE_QUALIFIER XMLMutex		m_providerMutex;
};

/** @} */

#endif /* XSECPROVIDER_INCLUDE */
