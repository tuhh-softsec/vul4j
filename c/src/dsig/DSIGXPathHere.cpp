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
 * DSIGXPathHere := Implementation of the "here()" XPath function.
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGXPathHere.hpp>

#ifndef XSEC_NO_XPATH

DSIGXPathHere::DSIGXPathHere() {

	XalanHereNode = NULL;

}

DSIGXPathHere::DSIGXPathHere(XalanNode * here) {

	XalanHereNode = here;

}

DSIGXPathHere::~DSIGXPathHere() {}

// These methods are inherited from Function ...

XObjectPtr DSIGXPathHere::execute(
			XPathExecutionContext&	executionContext,
			XalanNode*				context,
			// const XObjectPtr		arg1,
			const Locator*			locator) const {

	// Simple function - simply return the Xalan Node we already have
	
	typedef XPathExecutionContext::BorrowReturnMutableNodeRefList	BorrowReturnMutableNodeRefList;

	// This list will hold the nodes we find.

	BorrowReturnMutableNodeRefList	nl(executionContext);

	nl->addNodeInDocOrder(XalanHereNode, executionContext);

	return executionContext.getXObjectFactory().createNodeSet(nl);
}




#if defined(XSEC_NO_COVARIANT_RETURN_TYPE)
	Function*
#else
	DSIGXPathHere*
#endif
	DSIGXPathHere::clone() const {

		DSIGXPathHere *ret;

		ret = new DSIGXPathHere(*this);
		ret->XalanHereNode = XalanHereNode;
		return ret;
	}
		
	const XalanDOMString
		DSIGXPathHere::getError() const {

		return StaticStringToDOMString(XALAN_STATIC_UCODE_STRING("The here() function takes no arguments!"));

	}


#endif /* NO_XPATH */
