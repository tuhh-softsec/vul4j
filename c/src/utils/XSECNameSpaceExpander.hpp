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
 * XSECNameSpaceHolder := Container class for holding and managing the name space stack
 *						  Used when running through a DOM document
 *
 * $Id$
 *
 */

#ifndef XSECNAMESPACEEXPANDER_HEADER
#define XSECNAMESPACEEXPANDER_HEADER

// XSEC Includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/utils/XSECSafeBufferFormatter.hpp>

// Xerces Includes
XSEC_DECLARE_XERCES_CLASS(DOMDocument);
XSEC_DECLARE_XERCES_CLASS(DOMNode);
XSEC_DECLARE_XERCES_CLASS(DOMElement);

#include <vector>

// --------------------------------------------------------------------------------
//           Structure Definition for the nodes within the list of nodes
// --------------------------------------------------------------------------------

struct XSECNameSpaceEntry {

	// Variables
	safeBuffer									m_name;			// The name for this name space
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement	* mp_node;		// The Element Node owner
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode		* mp_att;		// The added attribute node
			
};

// --------------------------------------------------------------------------------
//           Class definition for the list
// --------------------------------------------------------------------------------

/**
 * @ingroup pubsig
 */
/*\@{*/

/**
 * @brief Class to "expand" name spaces
 *
 * For most things, a DOM model interoperates well with XPath.  Unfortunately,
 * name-spaces are the one main problem.  In particular, the XPath spec
 * states that every element node has an attribute node for its own 
 * namespaces, and one for namespaces above that are in scope.
 *
 * In the DOM scheme of things, a namespace is only available in the node in
 * which it is defined.  Normally this is not a problem, you can just just
 * refer backwards until you find the namespace you need.  However, for XPath
 * expressions that select namespace nodes, we need to actually promulgate
 * the name-spaces down to every node where they are visible so that the XPath
 * selection will work properly.
 *
 * This is important for Canonicalisation of the found nodes, but we cannot
 * do this only in the canonicaliser as it does not internally understand how
 * to do DSIG style XPath.  So the XPath is done externally, and the 
 * resultant node set (including any selected "Expanded" attribute nodes).
 * are passed in.
 *
 * The expander therefore handles the propogation of the namespace nodes, and
 * removes the propogated nodes when it goes out of scope (or when
 * deleteAddedNamespaces() is called).
 *
 */


class CANON_EXPORT XSECNameSpaceExpander {


#if defined(XALAN_NO_NAMESPACES)
	typedef vector<XSECNameSpaceEntry *>			NameSpaceEntryListVectorType;
#else
	typedef std::vector<XSECNameSpaceEntry *>		NameSpaceEntryListVectorType;
#endif


public:

    /** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Main constructure
	 *
	 * Use this constructor to expand namespaces through an entire document.
	 *
	 * @param d The DOM document to be expanded.
	 */

	XSECNameSpaceExpander(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *d);			// Constructor

    /**
	 * \brief Fragment constructor
	 *
	 * Use this constructor to expand namespaces in a given fragment only.
	 * @note The fragment does not need to be rooted in an actual document.
	 *
	 * @param f The starting element of the fragment to be expanded.
	 */

	XSECNameSpaceExpander(XERCES_CPP_NAMESPACE_QUALIFIER DOMElement *f);		    // frag Constructor

	~XSECNameSpaceExpander();						// Default destructor

	//@}

	// Operate 

	/**
	 * \brief Expand namespaces.
	 *
	 * Perform the expansion operation and create a list of all added nodes.
	 */

	void expandNameSpaces(void);

	/**
	 * \brief Collapse name-spaces
	 *
	 * Delete all namespaces added in exandNameSpaces() (using the list that
	 * was created at that time
	 */

	void deleteAddedNamespaces(void);

	// Check if a node is an added node
	bool nodeWasAdded(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *n);

private:  // Functions

	XSECNameSpaceExpander(void);					// No default constructor
	void recurse(XERCES_CPP_NAMESPACE_QUALIFIER DOMElement *n);

	// data
	
	NameSpaceEntryListVectorType	m_lst;			// List of added name spaces
	XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument						
									* mp_doc;		// The owner document
	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement                      
									* mp_fragment;  // If we are doing a fragment
	bool							m_expanded;		// Have we expanded already?
	XSECSafeBufferFormatter			* mp_formatter;

};

#endif /* XSECNAMESPACEEXPANDER_HEADER */

