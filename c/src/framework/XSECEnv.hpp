/*
 * Copyright 2003-2004 The Apache Software Foundation.
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
	 * \brief Set prefix for XENC nodes
	 *
	 * Set the namespace prefix the library will use when creating
	 * nodes in the XENC namespace
	 */

	void setXENCNSPrefix(const XMLCh * prefix);

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

	/**
	 * \brief Get namespace prefix for XENC nodes
	 *
	 * Find the string being used by the library to prefix nodes in the 
	 * xenc: namespace.
	 *
	 * @returns XENC namespace prefix
	 */

	const XMLCh * getXENCNSPrefix(void) const {return mp_xencPrefixNS;}

	//@}

	/** @name Pretty Printing Functions */
	//@{

	/**
	 * \brief Set Pretty Print flag 
	 *
	 * The pretty print flag controls whether the library will output
	 * CR/LF after the elements it adds to a document
	 *
	 * By default the library will do pretty printing (flag is true)
	 *
	 * @param flag Value to set the flag (true = do pretty printing)
	 */

	void setPrettyPrintFlag(bool flag) {m_prettyPrintFlag = flag;}

	/**
	 * \brief Return the current value of the PrettyPrint flag
	 *
	 * @returns The value of the pretty print flag
	 */

	bool getPrettyPrintFlag(void) const {return m_prettyPrintFlag;}

	/**
	 * \brief Do a pretty print output
	 *
	 * The library calls this function to perform CR/LF outputting
	 *
	 * At the moment htis is really redundant, but it is more a holder
	 * so that we can set up something in the library to allow users
	 * to install a pretty print function.
	 * 
	 * @param node Node to append pretty print content to
	 */

	void doPrettyPrint(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node) const;

	//@}
	
	/** @name General information functions */
	//@{

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

	XSECURIResolver * getURIResolver(void) const;


	//@}

	/** @name Formatters */
	//@{

	/** 
	 * \brief Get a safeBufferFormatter
	 *
	 * Return a UTF-8 safeBuffer formatter
	 *
	 * @returns A pointer to a safeBuffer formatter
	 */

	XSECSafeBufferFormatter * getSBFormatter(void) const {return mp_formatter;}


private:

	// Internal functions

	XSECSafeBufferFormatter		* mp_formatter;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument					
								* mp_doc;
	// For creating functions
	XMLCh 						* mp_prefixNS;
	XMLCh						* mp_ecPrefixNS;
	XMLCh						* mp_xpfPrefixNS;
	XMLCh						* mp_xencPrefixNS;

	// Resolvers
	XSECURIResolver				* mp_URIResolver;

	// Flags
	bool						m_prettyPrintFlag;

	XSECEnv();

	/*\@}*/
};

#endif /* XSECENV_INCLUDE */
