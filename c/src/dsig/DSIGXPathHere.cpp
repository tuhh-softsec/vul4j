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
 * DSIGXPathHere := Implementation of the "here()" XPath function.
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
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
