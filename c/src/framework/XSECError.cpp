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
 * XSECError := General class for handling errors
 *
 */

#include <xsec/framework/XSECError.hpp>
#include <xsec/framework/XSECDefs.hpp>
// Real definition of strings

char * XSECExceptionStrings [] = {

	"No Error",
	"Error allocating memory",
	"No TEXT child found under <DigestValue> element",
	"Unknown Attribute found in DSIG element",
	"Did not find expected DSIG child element",
	"Unknown algorithm found in <Transform> element",
	"Transform input/output mismatch",
	"Referenced ID is not in DOM Document",
	"Unsupported Xpointer expression found",
	"An error occured during an XPath evalaution",
	"An error occured during an XSLT transformation",
	"The called feature is unsupported (general error)",
	"Attempted to load an empty signature node",
	"Attempted to load a non signature DOM Node as a <Signature>",
	"Unknown canonicalization algorithm referenced",
	"Unknown signature and hashing algorithms referenced",
	"Attempted to load an empty X509Data Node",
	"Attempted to load a non X509Data node as a <X509Data>",
	"Error occurred in OpenSSL routine",
	"Error occured when attempting to Verify a Signature",
	"Attempted to load an empty SignedInfo node",
	"Attempted to load a non SignedInfo node as a <SignedInfo>",
	"Expected URI attribute in <REFERENCE> node",
	"A method has been called without load() being called first",
	"An error occurred when interacting with the Crypto Provider",
	"An error occurred during processing of <KeyInfo> list",
	"An error occurred during a signing operation",
	"Attempted to load an empty KeyInfoName node",
	"Attempted to load a non <KeyName> node as a KeyName",
	"Unknown key type found in <KeyValue> element",
	"An error occurred during the creation of a DSIGSignature object",
	"An error occurred when trying to open a URI input stream",
	"An error occurred in the XSEC Provider",
	"CATASTROPHE - An error has been found in internal state",
	"An error occurred in the Envelope Transform handler",
	"A function has been called which is not supported in the compiled library",
	"An error occured in a DSIGTransform holder",
	"An error occured in a safe buffer",
	"Unknown Error type",

};
//const char ** XSECExceptionStrings = XSECExceptionStringsArray;








