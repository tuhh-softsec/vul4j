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
 * XENCCipherReference := Interface definition for CipherReference element
 *
 * $Id$
 *
 */

#ifndef XENCCIPHERREFERENCE_INCLUDE
#define XENCCIPHERREFERENCE_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGConstants.hpp>

class DSIGTransformList;
class DSIGTransformBase64;
class DSIGTransformXPath;
class DSIGTransformXPathFilter;
class DSIGTransformXSL;
class DSIGTransformC14n;

XSEC_DECLARE_XERCES_CLASS(DOMNode);

/**
 * @ingroup xenc
 * @{
 */

/**
 * @brief Interface definition for the CipherValue object
 *
 * The \<CipherValue\> element holds the base64 encoded, encrypted data.
 * This is a very simple class that acts purely as a holder of data.
 *
 */


class XENCCipherReference {

	/** @name Constructors and Destructors */
	//@{

protected:

	XENCCipherReference() {};

public:

	virtual ~XENCCipherReference() {};

	/** @name Get Interface Methods */
	//@{

	/**
	 * \brief Obtain the transforms for this CipherReference
	 *
	 * Get the DSIGTransformList object for this CipherReference.  Can be used to
	 * obtain information about the transforms and also change the the transforms
	 */

	virtual DSIGTransformList * getTransforms(void) = 0;

	/**
	 * \brief Obtain the URI for this CipherReference
	 *
	 * @returns A pointer to the URI string for this CipherReference
	 */

	virtual const XMLCh * getURI (void) = 0;
	
	/**
	 * \brief Get the DOM Node of this structure
	 *
	 * @returns the DOM Node representing the <CipherValue> element
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * getDOMNode(void) = 0;

	//@}

	/** @name Set Interface Methods */
	//@{
	
	/**
	 * \brief Append a Base64 Transform to the Reference.
	 *
	 * @returns The newly created Base64 transform.
	 * @todo Move to DSIGTransformList rather than re-implement in both DSIGReference
	 * and XENCCipherReference
	 */

	virtual DSIGTransformBase64 * appendBase64Transform() = 0;
	
	/**
	 * \brief Append an XPath Transform to the Reference.
	 *
	 * <p> Append an XPath transform.  Namespaces can be added to the 
	 * transform directly using the returned <em>DSIGTransformXPath</em>
	 * structure</p>
	 *
	 * @param expr The XPath expression to be placed in the transform.
	 * @returns The newly created XPath transform
	 * @todo Move to DSIGTransformList rather than re-implement in both DSIGReference
	 * and XENCCipherReference
	 */

	virtual DSIGTransformXPath * appendXPathTransform(const char * expr) = 0;
	
	/**
	 * \brief Append an XPath-Filter2 Transform to the Reference.
	 *
	 * The returned DSIGTransformXPathFilter will have no actual filter
	 * expressions loaded, but calls can be made to
	 * DSIGTransformXPathFilter::appendTransform to add them.
	 *
	 * @returns The newly created XPath Filter transform
	 * @todo Move to DSIGTransformList rather than re-implement in both DSIGReference
	 * and XENCCipherReference
	 */

	virtual DSIGTransformXPathFilter * appendXPathFilterTransform(void) = 0;

	/**
	 * \brief Append an XSLT Transform to the Reference.
	 *
	 * <p>The caller must have already create the stylesheet and turned it into
	 * a DOM structure that is passed in as the stylesheet parameter.</p>
	 *
	 * @param stylesheet The stylesheet DOM structure to be placed in the reference.
	 * @returns The newly create XSLT transform
	 * @todo Move to DSIGTransformList rather than re-implement in both DSIGReference
	 * and XENCCipherReference
	 */

	virtual DSIGTransformXSL * appendXSLTransform(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *stylesheet) = 0;
	
	/**
	 * \brief Append a Canonicalization Transform to the Reference.
	 *
	 * @param cm The type of canonicalisation to be added.
	 * @returns The newly create canonicalisation transform
	 * @todo Move to DSIGTransformList rather than re-implement in both DSIGReference
	 * and XENCCipherReference
	 */

	virtual DSIGTransformC14n * appendCanonicalizationTransform(canonicalizationMethod cm) = 0;

	//@}

private:

	// Unimplemented
	XENCCipherReference(const XENCCipherReference &);
	XENCCipherReference & operator = (const XENCCipherReference &);

};

#endif /* XENCCIPHERREFERENCE_INCLUDE */

