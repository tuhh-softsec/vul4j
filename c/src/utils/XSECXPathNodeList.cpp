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
 * $ID$
 *
 * $LOG$
 *
 */

// XSEC

#include <xsec/utils/XSECXPathNodeList.hpp>
#include <xsec/framework/XSECError.hpp>





// --------------------------------------------------------------------------------
//           Constructors and Destructors.
// --------------------------------------------------------------------------------

XSECXPathNodeList::XSECXPathNodeList(unsigned int initialSize) {

	mp_elts = new const DOMNode *[initialSize];
	m_size = initialSize;
	m_current = 0;
	m_num = 0;

}

XSECXPathNodeList::XSECXPathNodeList(const XSECXPathNodeList &other) {

	// Copy Constructor

	mp_elts = new const DOMNode *[other.m_size];
	m_size = other.m_size;
	m_num = other.m_num;

	for (unsigned int i = 0; i < m_num; ++i) {

		mp_elts[i] = other.mp_elts[i];

	}

}

XSECXPathNodeList::~XSECXPathNodeList() {

	// Delete all the elements in the node list

	delete[] mp_elts;

}

XSECXPathNodeList & XSECXPathNodeList::operator= (const XSECXPathNodeList & toCopy) {

	// For now simply delete the old list and set with the new
	// Large overhead as we call other functions, but simplest way to
	// implement for now

	delete[] mp_elts;

	mp_elts = new const DOMNode *[toCopy.m_size];
	m_size = toCopy.m_size;
	m_num = toCopy.m_num;

	for (unsigned int i = 0; i < m_num; ++i) {

		mp_elts[i] = toCopy.mp_elts[i];

	}

	return *this;

}

// --------------------------------------------------------------------------------
//           Utility Functions.
// --------------------------------------------------------------------------------

unsigned int XSECXPathNodeList::findNodeIndex(const DOMNode *n) {

	// Check default values
	if (m_num == 0 || mp_elts[0] >= n)
		return 0;

	// Binary search through the list to find where this node should be

	unsigned int l = 0;				// Low
	unsigned int h = m_num;			// High

	unsigned int i;

	while (true) {

		i = l + ((h - l) / 2);

		if (l == i)
			return i + 1; // Insert point is the next element

		if (mp_elts[i] == n) {
			return i;		// Found and in list
		}

		if (n > mp_elts[i]) {

			// In top half of search space
			l = i;

		}

		else 
			// In bottom half of search space
			h = i;

	}

}

// --------------------------------------------------------------------------------
//           Adding and Deleting Nodes.
// --------------------------------------------------------------------------------


void XSECXPathNodeList::addNode(const DOMNode *n) {

	if (m_num == 0) {
		mp_elts[0] = n;
		m_num = 1;
		return;
	}

	unsigned int i = findNodeIndex(n);

	if (i != m_num && mp_elts[i] == n)
		return;
#if 0
	if (m_num == m_size) {

		// need to re-create the list with a bigger aray
		m_size *= 10;

		const DOMNode ** newElts = new const DOMNode*[m_size];
		for (unsigned j = 0; j < m_num; ++j) {
			newElts[j] = mp_elts[j];
		}
		delete mp_elts;
		mp_elts = newElts;

	}

	for (unsigned int j = m_num; j > i; --j) {

		mp_elts[j] = mp_elts[j - 1];
	
	}
#endif
	if (m_num == m_size) {

		// Need to create the list with a bigger array
		m_size *= 10;

		const DOMNode ** newElts = new const DOMNode*[m_size];
		memcpy(newElts, mp_elts, sizeof(DOMNode *) * m_num);

		delete mp_elts;
		mp_elts = newElts;

	}

	memmove(&(mp_elts[i+1]), &(mp_elts[i]), (m_num - i) * sizeof(DOMNode *));


	mp_elts[i] = n;
	++m_num;

}

void XSECXPathNodeList::removeNode(const DOMNode *n) {

	unsigned int i = findNodeIndex(n);

	if (i == m_num || mp_elts[i] != n)
		// not found
		return;

	for (unsigned int j = i; j < m_num; ++j)
		mp_elts[j] = mp_elts[j+1];

	m_num--;


}

void XSECXPathNodeList::clear() {

	m_num = 0;

}

// --------------------------------------------------------------------------------
//           Information functions.
// --------------------------------------------------------------------------------


bool XSECXPathNodeList::hasNode(const DOMNode *n) {

	unsigned int i = findNodeIndex(n);

	return (i != m_num && mp_elts[i] == n);

}

const DOMNode *XSECXPathNodeList::getFirstNode(void) {


	m_current = 0;
	return getNextNode();

}

const DOMNode *XSECXPathNodeList::getNextNode(void) {

	if (m_current == m_num)
		return NULL;

	return mp_elts[m_current++];

}
	
// --------------------------------------------------------------------------------
//           Intersect with another list
// --------------------------------------------------------------------------------

void XSECXPathNodeList::intersect(const XSECXPathNodeList &toIntersect) {

	const DOMNode ** newList = new const DOMNode *[m_size];

	unsigned int i = 0;
	unsigned int j = 0;
	unsigned int k = 0;

	while (true) {

		if (mp_elts[i] == toIntersect.mp_elts[j]) {

			newList[k++] = mp_elts[i++];
			j++;
		}

		else if (mp_elts[i] < toIntersect.mp_elts[j]) {
			++i;
		}
		else 
			++j;

		if (i == m_num || j == toIntersect.m_num)
			break;

	}

	m_num = k;
	delete[] mp_elts;
	mp_elts = newList;

}

