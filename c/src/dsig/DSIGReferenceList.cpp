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
 * DSIGReferenceList := Class for Loading and storing a list of references
 *
 * $Id$
 *
 */

// XSEC Includes
#include <xsec/dsig/DSIGReferenceList.hpp>
#include <xsec/dsig/DSIGReference.hpp>

XERCES_CPP_NAMESPACE_USE

DSIGReferenceList::DSIGReferenceList() {}

DSIGReferenceList::~DSIGReferenceList() {

	// Delete all the references contained in the list

	ReferenceListVectorType::iterator iterator = m_referenceList.begin();

	while (iterator != m_referenceList.end()) {
		
		delete *iterator;
		iterator++;

	}

}


void DSIGReferenceList::addReference(DSIGReference * ref) {

	m_referenceList.push_back(ref);

}

DSIGReferenceList::size_type DSIGReferenceList::getSize() {

	return m_referenceList.size();

}

DSIGReference * DSIGReferenceList::removeReference(size_type index) {

	DSIGReference * ret = NULL;
	if (index < m_referenceList.size()) {

		ret = m_referenceList[index];
		m_referenceList.erase(m_referenceList.begin() + index - 1);
	
	}

	return ret;

}

DSIGReference * DSIGReferenceList::item(ReferenceListVectorType::size_type index) {

	if (index < m_referenceList.size())
		return m_referenceList[index];

	return NULL;

}
bool DSIGReferenceList::empty() {

	// Clear out the list - note we do NOT delete the reference elements

	return m_referenceList.empty();

}


