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
 * XSECEnv := Configuration class - used by the other classes to retrieve
 *            information on the environment they are working under
 *
 * $Id$
 *
 */

#ifndef XSECENV_INCLUDE
#define XSECENV_INCLUDE

// XSEC Includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSafeBufferFormatter.hpp>

// Xerces Includes

#include <xercesc/dom/DOM.hpp>

class XSECURIResolver;

/**
 * @ingroup internal
 */
/*\@{*/

/**
 * @brief Holds environmental information
 *
 * The various XSEC classes need to be able to retrieve information
 * about the environment they are operating in (namespace prefixes,
 * owning document etc.) - this class is used to provide and hold
 * this info.
 *
 */

class DSIG_EXPORT XSECEnv {

public:
    
	/** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor.
	 *
	 */

	XSECEnv(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc);
	virtual ~XSECEnv();

    //@}
	
	/** @name Prefix handling. */
	//@{

	/**
	  * \brief Set the prefix be used for the DSIG namespace.
	  *
	  * <p>When the XSEC library creates XML Element nodes, it uses the prefix here
	  * for all nodes created.  By default, the library assumes that the default
	  * namespace is used.</p>
	  *
	  * <p>The #createBlankSignature function will use this prefix to setup the
	  * dsig namespace.  E.g. (assuming a call has been made to set the prefix to "ds")
	  * the \<Signature\> element will have a namespace attribute added of</p>
	  *
	  * <p>xmlns:ds="http://www.w3.org/2000/09/xmldsig#"</p>
	  *
	  * <p>If no prefix has been set, this attribute will be set as the default namespace</p>
	  *
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Digital Signature nodes
	  */

	void setDSIGNSPrefix(const XMLCh * prefix);

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
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XML 
	  * Exclusive Canonicalisation nodes
	  */

	void setECNSPrefix(const XMLCh * prefix);

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
	  * @see #createBlankSignature
	  * @param prefix The UTF-16 encoided NS prefix to use for the XPath
	  * filter nodes
	  */

	void setXPFNSPrefix(const XMLCh * prefix);

	/**
	 * \brief Get the NS Prefix being used for DSIG elements.
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setDSIGNSPrefix
	 *
	 */

	const XMLCh * getDSIGNSPrefix() const {return mp_prefixNS;}

	/**
	 * \brief Get the NS being used for EC nodes
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setECNSPrefix
	 */

	const XMLCh * getECNSPrefix() const {return mp_ecPrefixNS;}

	/**
	 * \brief Get the NS being used for XPath Filter2 nodes
	 *
	 * @returns A pointer to the buffer holding the prefix
	 * @see #setXPFNSPrefix
	 */

	const XMLCh * getXPFNSPrefix() const {return mp_xpfPrefixNS;}

	//@}

	/** @name General information functions */

	/**
	 * \brief
	 *
	 * Get the DOMDocument that the super class is operating within.
	 *
	 * Mainly used by the library itself.
	 *
	 * @returns The DOM_Document node.
	 */

	XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * getParentDocument() const
		{return mp_doc;}

	/**
	 * \brief
	 *
	 * Set the DOMDocument that the super class is operating within.
	 *
	 * Mainly used by the library itself.
	 *
	 * @param doc The Document node.
	 */

	void setParentDocument(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * doc)
		{mp_doc = doc;}

	//@}

	/** @name Resolver manipulation */
	//@{

	/**
	 * \brief Register a URIResolver 
	 *
	 * Registers a URIResolver to be used by the Signature when dereferencing
	 * a URI in a Reference element
	 *
	 */

	void setURIResolver(XSECURIResolver * resolver);

	/**
	 * \brief Return a pointer to the resolver being used
	 *
	 * @returns A pointer to the URIResolver registered in this signature
	 */

	XSECURIResolver * getURIResolver(void);


	//@}


private:

	// Internal functions

	XSECSafeBufferFormatter		* mp_formatter;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument					
								* mp_doc;
	// For creating functions
	XMLCh 						* mp_prefixNS;
	XMLCh						* mp_ecPrefixNS;
	XMLCh						* mp_xpfPrefixNS;

	// Resolvers
	XSECURIResolver				* mp_URIResolver;

	XSECEnv();

	/*\@}*/
};

#endif /* XSECENV_INCLUDE */
