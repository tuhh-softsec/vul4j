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
 * DSIGTransformXPathFilter := Class that performs XPath Filter 
 *                             transforms
 *
 * $Id$
 *
 */

#ifndef DSIGTRANSFORMXPATHFILTER_INCLUDE
#define DSIGTRANSFORMXPATHFILTER_INCLUDE

#include <xsec/dsig/DSIGTransform.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>


XSEC_DECLARE_XERCES_CLASS(DOMNamedNodeMap);
XSEC_DECLARE_XERCES_CLASS(DOMNode);

#include <vector>

class DSIGXPathFilterExpr;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Transform holder for XPath Filter transforms.
 *
 * The DSIGTransformXPath class is used to hold XPath \<Transform\> elements
 * within a document.
 *
 * @see TXFMXPath
 * @see DSIGTransform
 *
 */

class DSIG_EXPORT DSIGTransformXPathFilter : public DSIGTransform {


public:

	/**
	 * \brief Vector used to pass the actual expressions into the transformer
	 */

	typedef std::vector<DSIGXPathFilterExpr *> exprVectorType;

	/** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor used for existing XML signatures.
	 *
	 * The Node structure already exists, so read the nodes in.
	 *
	 * @param env The operating environment
	 * @param node The DOM node (within doc) that is to be used as the base of the Transform.
	 * @see #load
	 */

	DSIGTransformXPathFilter(const XSECEnv * env, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node);

    /**
	 * \brief Contructor used for new signatures.
	 *
	 * The Node structure will have to be created.
	 *
	 * @note DSIGTransform structures should only ever be created via calls to a
	 * DSIGTransformList object.
	 *
	 * @param env The operating environment
	 * @see createBlankTransform
	 */

	DSIGTransformXPathFilter(const XSECEnv * env);
		  
	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	virtual ~DSIGTransformXPathFilter();
	
	//@}

	/** @name Interface Methods */

	//@{

	/**
	 * \brief Determine the transform type.
	 *
	 * Used to determine what the type of the transform is.
	 *
	 */

	virtual transformType getTransformType();

	/**
	 * \brief Create the XPath Filter Transformer class.
	 *
	 * Create the transformer associated with this XPath transform.
	 * Will set the expression and Namespaces as appropriate
	 *
	 * @returns The TXFMXPath transformer associated with this Transform
	 */

	virtual void appendTransformer(TXFMChain * input);

	/**
	 * \brief Construct blank XPath Transform element.
	 *
	 * Instruct the implementation to create the required
	 * transform and return the newly constructed DOMNode structure
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankTransform(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * parentDoc);

	/**
	 * \brief Load a DOM structure
	 *
	 * Load the expression and Namespaces.
	 *
	 */

	virtual void load(void);

	//@}

	/** @name XPathFilter specific methods */

	//@{

	/**
	 * \brief Append a new filter to the transform
	 *
	 * Appends the indicated filter (of given type) to the list of 
	 * filters in the transform.
	 *
	 * @param filterType The type of this filter
	 * @param filterExpr The XPath expression to use
	 * @returns The filter expression
	 */

	DSIGXPathFilterExpr * appendFilter(xpathFilterType filterType,
										const XMLCh * filterExpr);

	/**
	 * \brief Retrieve number of expressions
	 *
	 * Allows callers to determine how many expressions there are in
	 * this filter
	 *
	 * @returns The number of expressions
	 */

	unsigned int getExprNum(void);

	/**
	 * \brief Get expression.
	 *
	 * Allows the caller to retrieve the specified expression from the
	 * XPath Filter object
	 *
	 * @param n The index of the expression to retrieve
	 * @returns The indicated expression
	 */

	DSIGXPathFilterExpr * expr(unsigned int n);

	//@}
	
private:

	DSIGTransformXPathFilter();
	DSIGTransformXPathFilter(const DSIGTransformXPathFilter & theOther);

	bool						m_loaded;

	// The expressions
	exprVectorType				m_exprs;


};

#endif /* DSIGTRANSFORMXPATHFILTER_INCLUDE */
