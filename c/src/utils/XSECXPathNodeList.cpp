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
 */

// XSEC

#include <xsec/utils/XSECXPathNodeList.hpp>
#include <xsec/framework/XSECError.hpp>





// --------------------------------------------------------------------------------
//           Constructors and Destructors.
// --------------------------------------------------------------------------------

XSECXPathNodeList::XSECXPathNodeList() {

	mp_first = mp_last = NULL;

}

XSECXPathNodeList::XSECXPathNodeList(const XSECXPathNodeList &other) {

	// Copy Constructor

	// For now simply delete the old list and set with the new
	// Large overhead as we call other functions, but simplest way to
	// implement for now

	mp_first = mp_last = NULL;

	XSECXPathNodeListElt *cpyTmp;

	cpyTmp = other.mp_first;

	while (cpyTmp != NULL) {

		addNode(cpyTmp->element);
		cpyTmp = cpyTmp->next;

	}

}

XSECXPathNodeList::~XSECXPathNodeList() {

	// Delete all the elements in the node list

	clear();

}

XSECXPathNodeList & XSECXPathNodeList::operator= (const XSECXPathNodeList & toCopy) {

	// For now simply delete the old list and set with the new
	// Large overhead as we call other functions, but simplest way to
	// implement for now

	clear();

	XSECXPathNodeListElt *cpyTmp;

	cpyTmp = toCopy.mp_first;

	while (cpyTmp != NULL) {

		addNode(cpyTmp->element);
		cpyTmp = cpyTmp->next;

	}

	return *this;

}

// --------------------------------------------------------------------------------
//           Utility Functions.
// --------------------------------------------------------------------------------

XSECXPathNodeList::XSECXPathNodeListElt * XSECXPathNodeList::findNode(const DOMNode *n) {

	XSECXPathNodeListElt * tmp;

	tmp = mp_first;

	while (tmp != NULL) {

		if (tmp->element == n)
			return tmp;

		tmp = tmp->next;

	}

	return NULL;

}

// --------------------------------------------------------------------------------
//           Adding and Deleting Nodes.
// --------------------------------------------------------------------------------


void XSECXPathNodeList::addNode(const DOMNode *n) {

	XSECXPathNodeListElt *tmp;
	
	if (findNode(n) != NULL)
		return;			// Allready exists

	XSECnew(tmp, XSECXPathNodeListElt);
	tmp->element = n;

	if (mp_first == NULL) {

		tmp->next = NULL;
		tmp->last = NULL;

		mp_first = tmp;
		mp_last = tmp;

	}

	else if (mp_last == NULL) {

		throw XSECException(XSECException::InternalError,
			"XSECXPathNodeList has an element that is incorrectly linked");

	}

	else {

		mp_last->next = tmp;
		tmp->last = mp_last;
		tmp->next = NULL;
		mp_last = tmp;

	}

}

void XSECXPathNodeList::removeNode(const DOMNode *n) {

	XSECXPathNodeListElt * tmp;

	tmp = findNode(n);

	if (tmp == NULL)
		return;

	if (tmp->last != NULL) {

		tmp->last->next = tmp->next;

	}

	if (tmp->next != NULL) {

		tmp->next->last = tmp->last;

	}

	if (mp_first == tmp)
		mp_first = tmp->next;
	if (mp_last == tmp)
		mp_last = tmp->last;

	delete tmp;

}

void XSECXPathNodeList::clear() {

	XSECXPathNodeListElt * tmp;

	tmp = mp_first;

	while (tmp != NULL) {

		mp_first = tmp->next;
		delete tmp;

		tmp = mp_first;

	}

	mp_last = NULL;

}

// --------------------------------------------------------------------------------
//           Information functions.
// --------------------------------------------------------------------------------


bool XSECXPathNodeList::hasNode(const DOMNode *n) {

	return (findNode(n) != NULL);

}

const DOMNode *XSECXPathNodeList::getFirstNode(void) {

	if (mp_first == NULL)
		return NULL;

	mp_search = mp_first->next;

	return mp_first->element;

}

const DOMNode *XSECXPathNodeList::getNextNode(void) {

	if (mp_search == NULL)
		return NULL;

	XSECXPathNodeListElt * tmp;

	tmp = mp_search;
	mp_search = mp_search->next;
	
	return tmp->element;

}


