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
 * DSIGXPathFilterExpr := Class that holds an XPath Filter expression
 *
 * $Id$
 *
 */

#ifndef DSIGXPATHFILTEREXPR_INCLUDE
#define DSIGXPATHFILTEREXPR_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>


XSEC_DECLARE_XERCES_CLASS(DOMNamedNodeMap);
XSEC_DECLARE_XERCES_CLASS(DOMNode);
XSEC_DECLARE_XERCES_CLASS(DOMElement);

class XSECEnv;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Class used to hold (and manipulate) individual expressions
 *        in an XPathFilter transform
 *
 * @see TXFMXPathFilter
 * @see DSIGTransformXpathFilter
 *
 */

class DSIG_EXPORT DSIGXPathFilterExpr {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor used for existing XML signatures
	 *
	 * Node already exists and is part of an existing XPathFilter tree
	 *
	 * @param env The operating environment
	 * @param node The node that will be used to read the expression in
	 */

	DSIGXPathFilterExpr(const XSECEnv * env, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node);

	/**
	 * \brief Builder constructor
	 *
	 * Used to create the DOM structure and DSIGSignature elements
	 *
	 * @param env The operating Environment
	 */

	DSIGXPathFilterExpr(const XSECEnv * env);

	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	~DSIGXPathFilterExpr();
	
	//@}

	/** @name Set and get Information */

	//@{

	/**
	 * \brief Read in existing structure
	 *
	 * Reads DOM structure of the XPath expression
	 */

	void load(void);

	/**
	 * \brief Get the filter type
	 *
	 * Returns the type of this particular XPath filter
	 *
	 * @returns The filter type of this expression
	 */

	xpathFilterType getFilterType(void);

	/**
	 * \brief create from blank
	 *
	 * Given the filter type and XPath expression, setup the
	 * DOMNodes and variables to allow signing and validation
	 *
	 * @param filterType Type of this filter to add
	 * @param filterExpr The XPath expression
	 */

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * setFilter(xpathFilterType filterType,
						const XMLCh * filterExpr);

	/**
	 * \brief Get the filter expression
	 *
	 * Returns an XMLCh string containing the filter expression
	 *
	 * @returns The filter expression
	 */

	const XMLCh * getFilter(void) {return m_expr.rawXMLChBuffer();}

	/**
	 * \brief Add a new namespace to the list to be used
	 *
	 * Add a new namespace to the XPath Element.
	 *
	 * @param prefix NCName of the Namespace to set
	 * @param value The string with the URI to set
	 */

	void setNamespace(const XMLCh * prefix, const XMLCh * value);

	/**
	 * \brief Get the list of namespaces.
	 *
	 * Returns the DOMNamedNodeMap of the attributes of the XPath transform
	 * node.  
	 *
	 * @note This will also contain the Filter attribute
	 *
	 * @returns A pointer to the NamedNodeMap
	 */

	XERCES_CPP_NAMESPACE_QUALIFIER DOMNamedNodeMap * getNamespaces(void) {
		return mp_NSMap;
	}

	/**
	 * \brief Delete a namespace to the list to be used
	 *
	 * Delete a namespace from the XPath Element.
	 *
	 * @param prefix NCName of the Namespace to delete
	 * @throws XSECException if the NCName does not exist
	 *
	 */

	void deleteNamespace(const XMLCh * prefix);

	//@}
	
private:

	// Just let the TXFM read directly

	friend class TXFMXPathFilter;

	DSIGXPathFilterExpr();
	DSIGXPathFilterExpr(const DSIGXPathFilterExpr& theOther);

	const XSECEnv				* mp_env;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode						
								* mp_xpathFilterNode;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode						
								* mp_exprTextNode;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNamedNodeMap				
								* mp_NSMap;
	safeBuffer					m_expr;
	xpathFilterType				m_filterType;
	bool						m_loaded;


};

#endif /* DSIGXPATHFILTEREXPR_INCLUDE */
