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
 * TXFMBase := Base (virtual) class that defines a DSIG Transformer
 * Given this is a pure virtual class, this file only implements friend functions
 *
 */

#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/framework/XSECError.hpp>

// -----------------------------------------------------------------------
//  Ensure name spaces are reset when this is destroyed
// -----------------------------------------------------------------------

TXFMBase::~TXFMBase() {

	if (mp_nse != NULL) {

		mp_nse->deleteAddedNamespaces();
		delete mp_nse;
		mp_nse = NULL;

	}

}


// -----------------------------------------------------------------------
//  For expanding name spaces when necessary
// -----------------------------------------------------------------------

bool TXFMBase::nameSpacesExpanded(void) {

	if (mp_nse != NULL)
		return true;

	if (input != NULL)
		return input->nameSpacesExpanded();

	return false;

}

void TXFMBase::expandNameSpaces(void) {

	if (mp_nse != NULL || (input != NULL && input->nameSpacesExpanded()))
		return;		// Already done
	
	XSECnew(mp_nse, XSECNameSpaceExpander(mp_expansionDoc));

	mp_nse->expandNameSpaces();

}

void TXFMBase::deleteExpandedNameSpaces(void) {

	if (mp_nse != NULL) {

		mp_nse->deleteAddedNamespaces();
		delete mp_nse;
		mp_nse = NULL;

	}

	if (input != NULL)
		input->deleteExpandedNameSpaces();

}



// -----------------------------------------------------------------------
//  deleteTransformChain = easy way to delete an entire chain of transforms
// -----------------------------------------------------------------------


void deleteTXFMChain(TXFMBase * toDelete) {

	if (toDelete != NULL) {
		deleteTXFMChain(toDelete->input);
		delete toDelete;
	}

}
		

void TXFMBase::activateComments(void) {

	if (input  != NULL) {

		// Only activate them if our input had them

		keepComments = input->keepComments;

	}

	else 
		
		keepComments = true;

}