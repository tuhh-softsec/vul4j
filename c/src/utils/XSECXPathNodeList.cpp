/*
 * Copyright 2002-2004 The Apache Software Foundation.
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
 * XSECXPathNodeList := A structure to hold node lists from XPath 
 * evaluations
 *
 * $Id$
 *
 */

// XSEC

#include <xsec/utils/XSECXPathNodeList.hpp>
#include <xsec/framework/XSECError.hpp>

#include <string.h>

XERCES_CPP_NAMESPACE_USE

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
		delete[] mp_elts;
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

		delete[] mp_elts;
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

