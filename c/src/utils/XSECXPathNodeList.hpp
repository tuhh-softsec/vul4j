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
 * XSECXPathNodeList := A structure to hold node lists from XPath 
 * evaluations
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XSECXPATHNODELIST_INCLUDE
#define XSECXPATHNODELIST_INCLUDE

// XSEC
#include <xsec/framework/XSECDefs.hpp>

// Xerces

XSEC_DECLARE_XERCES_CLASS(DOMNode);

/**
 * @ingroup internal
 */

/**
 * \brief Class for holding lists of DOMNodes.
 *
 * This class is used primarily for holding lists of nodes found during XPath
 * processing.  It is also used for xpath-filter which requires multiple list
 * comparisons.
 *
 * It is not implemented using one of the container classes as it has the
 * potential to become a real bottleneck.  It could potentially be implemented
 * as a hash list based on names of nodes (or even pointers).
 *
 */

class DSIG_EXPORT XSECXPathNodeList {


private:

	/**
	 * \brief Element holder.
	 *
	 * Currently the list is implemented as a simple doubly linked list.
	 */

	struct XSECXPathNodeListElt {

		const DOMNode			* element;	// Element referred to

		XSECXPathNodeListElt	* next,
								* last;		// For the list

	};

public:

	/** @name Constructors, Destructors and operators */
	//@{

	XSECXPathNodeList();

	/**
	 * \brief Copy Constructor
	 *
	 * @note The XSEC Library generally passes NodeLists around as pointers.
	 * There is a LARGE overhead associated with re-creating lists, so we
	 * try to avoid doing it unless necessary.
	 */

	XSECXPathNodeList(const XSECXPathNodeList &other);

	~XSECXPathNodeList();

	/**
	 * \brief Assignment Operator.
	 *
	 * Set one node list equal to another.
	 *
	 * @note Do not do this frequently, particularly for large lists
	 * as it will replicate the entire list and is a fairly expensive
	 * operation
	 *
	 * @param toCopy The list to be copied from
	 */

	XSECXPathNodeList & operator= (const XSECXPathNodeList & toCopy);


	//@}


	/** @name Adding and Deleting nodes */
	//@{

	/**
	 *\brief Add a node to the list.
	 *
	 * Checks to see whether the node is already in the list, and if not
	 * adds it.
	 *
	 * @param n The node to add.
	 */

	void addNode(const DOMNode *n);

	/**
	 * \brief Remove a node from the list.
	 *
	 * Given a node, find it in the list and (if it exists) delete it from the list.
	 *
	 * @param n The node to be removed.
	 */

	void removeNode(const DOMNode *n);

	/**
	 * \brief Clear out the entire list, deleting all entries.
	 *
	 */

	void clear(void);

	//@}

	/** @name Reading List Functions */
	//@{

	/**
	 * \brief Check if a node exists in the list.
	 *
	 * @param n The node to find in the list.
	 */

	bool hasNode(const DOMNode *n);

	/**
	 * \brief Get the first node in the list.
	 *
	 * Returns the first node in the list of nodes and resets the search list.
	 *
	 * @returns The first node in the list or NULL if none exist
	 */

	const DOMNode * getFirstNode(void);

	/**
	 * \brief Get the next node in the list
	 *
	 * Returns the next node in the list.
	 *
	 * @returns The next node in the list of NULL if none exist
	 */

	const DOMNode *getNextNode(void);

	//@}

private:

	// Internal functions
	XSECXPathNodeListElt * findNode(const DOMNode * n);

	XSECXPathNodeListElt			* mp_first;			// First node in list
	XSECXPathNodeListElt			* mp_last;			// Last node in list
	XSECXPathNodeListElt			* mp_search;		// Where we are in the return

};



#endif /* XSECXPATHNODELIST_INCLUDE */

