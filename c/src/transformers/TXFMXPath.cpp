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
 * TXFMXPath := Class that performs XPath transforms
 *
 */


#include <xsec/transformers/TXFMXPath.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/framework/XSECError.hpp>

#ifndef XSEC_NO_XALAN

#include <XPath/XObjectFactoryDefault.hpp>
#include <XPath/XPathExecutionContextDefault.hpp>

// Xalan namespace usage
XALAN_USING_XALAN(XPathProcessorImpl)
XALAN_USING_XALAN(XercesDOMSupport)
XALAN_USING_XALAN(XercesParserLiaison)
XALAN_USING_XALAN(XercesDocumentWrapper)
XALAN_USING_XALAN(XercesWrapperNavigator)
XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XPathFactoryDefault)
XALAN_USING_XALAN(XPathConstructionContextDefault)
XALAN_USING_XALAN(XalanDocument)
XALAN_USING_XALAN(XalanNode)
XALAN_USING_XALAN(XalanDOMChar)
XALAN_USING_XALAN(XPathEnvSupportDefault)
XALAN_USING_XALAN(XObjectFactoryDefault)
XALAN_USING_XALAN(XPathExecutionContextDefault)
XALAN_USING_XALAN(ElementPrefixResolverProxy)
XALAN_USING_XALAN(XPath)
XALAN_USING_XALAN(NodeRefListBase)
XALAN_USING_XALAN(XSLTResultTarget)

#endif

#ifndef XSEC_NO_XPATH
#include <ostream.h>

#define KLUDGE_PREFIX "berindsig"

// Helper function

void setAndClearNS(DOMDocument *d, 
				   DOMNamedNodeMap *xAtts, 
				   bool set, 
				   XSECSafeBufferFormatter *formatter,
				   XSECNameSpaceExpander * nse) {

	// if set then set the name spaces in the attribute list else clear them

	DOMElement * e = d->getDocumentElement();

	if (e == NULL) {

		throw XSECException(XSECException::XPathError, "Element node not found in Document");

	}

	if (xAtts != 0) {

		int xAttsCount = xAtts->getLength();	

		// Check all is OK with the Xalan Document and first element

		if (d == NULL) {

			throw XSECException(XSECException::XPathError, "Attempt to define XPath Name Space before setInput called");

		}

		// Run through each attribute looking for name spaces

		safeBuffer xpName, xpValue;

		for (int xCounter = 0; xCounter < xAttsCount; ++xCounter) {

			if (nse == NULL || !nse->nodeWasAdded(xAtts->item(xCounter))) {

				xpName << (*formatter << xAtts->item(xCounter)->getNodeName());
				
				if (xpName.sbStrncmp("xmlns", 5) == 0) {

					xpValue << (*formatter << xAtts->item(xCounter)->getNodeValue());

					if (set)
						e->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, xpName.sbStrToXMLCh(), xpValue.sbStrToXMLCh());
					else
						e->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
									MAKE_UNICODE_STRING((char *) (&xpName.rawBuffer()[6])));
				}

			}

		}

	}

	// Insert the kludge namespace
	safeBuffer k("xmlns:");
	k.sbStrcatIn(KLUDGE_PREFIX);

	if (set)
		e->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
						 MAKE_UNICODE_STRING(k.rawCharBuffer()),
						 DSIGConstants::s_unicodeStrURIDSIG);
	else
		e->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS,
						 MAKE_UNICODE_STRING(KLUDGE_PREFIX));

}

TXFMXPath::TXFMXPath(DOMDocument *doc) : 
	TXFMBase(doc) {

	document = NULL;
	XPathAtts = NULL;

	// Formatter is used for handling attribute name space inputs

	XSECnew(formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));

}

TXFMXPath::~TXFMXPath() {

	delete formatter;
	
}

void TXFMXPath::setNameSpace(DOMNamedNodeMap *xpAtts) {

	// A name space needs to be set on the document

	XPathAtts = xpAtts;

}

// Methods to set the inputs

void TXFMXPath::setInput(TXFMBase *newInput) {

	if (newInput->getOutputType() != TXFMBase::DOM_NODES) {

		throw XSECException(XSECException::TransformInputOutputFail, "XPath requires DOM_NODES input type");

	}

	input = newInput;

	// Expand if necessary
	this->expandNameSpaces();

	keepComments = input->getCommentsStatus();

	// Set up for the new document
	document = input->getDocument();

}

bool separator(unsigned char c) {

	if (c >= 'a' && c <= 'z')
		return false;

	if (c >= 'A' && c <= 'Z')
		return false;

	return true;

}

XalanNode * findHereNodeFromXalan(XercesWrapperNavigator * xwn, XalanNode * n, DOMNode *h) {
	
	const DOMNode * m = xwn->mapNode(n);
	const XalanNode * ret;

	if (m == h)
		return n;

	// Not this one - check the children

	XalanNode * c = n->getFirstChild();

	while (c != 0) {
		ret = findHereNodeFromXalan(xwn, c, h);
		if (ret != 0)
			return (XalanNode *) ret;
		c = c->getNextSibling();
	}

	return 0;
}
		


void TXFMXPath::evaluateExpr(DOMNode *h, safeBuffer expr) {

	// Temporarily add any necessary name spaces into the document

	setAndClearNS(document, XPathAtts, true, formatter, mp_nse);

	XPathProcessorImpl	xppi;					// The processor
	XercesDOMSupport	xds;
	XercesParserLiaison xpl;
	XPathEvaluator		xpe;
	XPathFactoryDefault xpf;
	XPathConstructionContextDefault xpcc;

	XalanDocument		* xd;
	XalanNode			* contextNode;

	
	// Map to Xalan
	xd = xpl.createDocument(document);

	// For performing mapping
	XercesDocumentWrapper *xdw = xpl.mapDocumentToWrapper(xd);
	XercesWrapperNavigator xwn(xdw);

	// Map the "here" node

	XalanNode * hereNode = xwn.mapNode(h);

	if (hereNode == NULL) {

		hereNode = findHereNodeFromXalan(&xwn, xd, h);

		if (hereNode == NULL) {

			throw XSECException(XSECException::XPathError,
			   "Unable to find here node in Xalan Wrapper map");
		}

	}

	// Now work out what we have to set up in the new processing

	TXFMBase::nodeType inputType = input->getNodeType();

	XalanDOMString cd;		// For the moment assume the root is the context

	const XalanDOMChar * cexpr;

	safeBuffer contextExpr;

	switch (inputType) {

	case DOM_NODE_DOCUMENT :

		cd = XalanDOMString("/");		// Root node
		cexpr = cd.c_str();

		// The context node is the "root" node
		contextNode =
			xpe.selectSingleNode(
			xds,
			xd,
			cexpr,
			xd->getDocumentElement());

		break;

	case DOM_NODE_DOCUMENT_FRAGMENT :
		{

			// Need to map the DOM_Node that we are given from the input to the appropriate XalanNode

			// Create the XPath expression to find the node

			if (input->getFragmentId() != NULL) {

				contextExpr.sbTranscodeIn("//descendant-or-self::node()[attribute::Id='");
				contextExpr.sbXMLChCat(input->getFragmentId());
				contextExpr.sbXMLChCat("']");

				// Map the node

				contextNode = 
					xpe.selectSingleNode(
					xds,
					xd,
					contextExpr.rawXMLChBuffer(), //XalanDOMString((char *) contextExpr.rawBuffer()).c_str(), 
					xd->getDocumentElement());


				if (contextNode == NULL) {
					// Last Ditch
					contextNode = xwn.mapNode(input->getFragmentNode());

				}

			}
			else
				contextNode = xwn.mapNode(input->getFragmentNode());

			if (contextNode == NULL) {

				// Something wrong
				throw XSECException(XSECException::XPathError, "Error mapping context node");

			}

			break;
		}

	default :

		throw XSECException(XSECException::XPathError);	// Should never get here

	}

	safeBuffer str;
	XPathEnvSupportDefault xpesd;
	XObjectFactoryDefault			xof;
	XPathExecutionContextDefault	xpec(xpesd, xds, xof);

	ElementPrefixResolverProxy pr(xd->getDocumentElement(), xpesd, xds);

	// Work around the fact that the XPath implementation is designed for XSLT, so does
	// not allow here() as a NCName.

	// THIS IS A KLUDGE AND SHOULD BE DONE BETTER

	int offset = 0;
	safeBuffer k(KLUDGE_PREFIX);
	k.sbStrcatIn(":");

	offset = expr.sbStrstr("here()");

	while (offset >= 0) {

		if (offset == 0 || offset == 1 || 
			(!(expr[offset - 1] == ':' && expr[offset - 2] != ':') &&
			separator(expr[offset - 1]))) {

			expr.sbStrinsIn(k.rawCharBuffer(), offset);

		}

		offset = expr.sbOffsetStrstr("here()", offset + 11);

	}

	// Install the External function in the Environment handler

	xpesd.installExternalFunctionLocal(XalanDOMString(URI_ID_DSIG), XalanDOMString("here"), DSIGXPathHere(hereNode));

	str.sbStrcpyIn("(descendant-or-self::node() | descendant-or-self::node()/attribute::* | descendant-or-self::node()/namespace::*)[");
	str.sbStrcatIn(expr);
	str.sbStrcatIn("]");

	XPath * xp = xpf.create();

	XalanDOMString Xexpr((char *) str.rawBuffer());
	xppi.initXPath(*xp, xpcc, Xexpr, pr);
	
	// Now resolve

	XObjectPtr xObj = xp->execute(contextNode, pr, xpec);

	// Now map to a list that others can use (naieve list at this time)

	const NodeRefListBase&	lst = xObj->nodeset();
	
	int size = lst.getLength();
	const DOMNode *item;
	
	for (int i = 0; i < size; ++ i) {

		if (lst.item(i) == xd)
			m_XPathMap.addNode(document);
		else {
			item = xwn.mapNode(lst.item(i));
			m_XPathMap.addNode(item);
		}
	}

	xpesd.uninstallExternalFunctionGlobal(XalanDOMString(URI_ID_DSIG), XalanDOMString("here"));

	setAndClearNS(document, XPathAtts, false, formatter, mp_nse);

}

void TXFMXPath::evaluateEnvelope(DOMNode *t) {

	// A special case where the XPath expression is already known

	if (document == NULL) {

		throw XSECException(XSECException::XPathError, 
		   "Attempt to define XPath Name Space before setInput called");

	}

	DOMElement * e = document->getDocumentElement();

	if (e == NULL) {

		throw XSECException(XSECException::XPathError, 
              "Element node not found in Document");

	}

	// Set the xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"

	e->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, MAKE_UNICODE_STRING("xmlns:dsig"), DSIGConstants::s_unicodeStrURIDSIG);
	
	
	// Evaluate

	evaluateExpr(t, XPATH_EXPR_ENVELOPE);

	// Now we are done, remove the namespace
	
	e->removeAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, MAKE_UNICODE_STRING("dsig"));

}
	
// Methods to get tranform output type and input requirement

TXFMBase::ioType TXFMXPath::getInputType(void) {

	return TXFMBase::DOM_NODES;

}
TXFMBase::ioType TXFMXPath::getOutputType(void) {

	return TXFMBase::DOM_NODES;

}

TXFMBase::nodeType TXFMXPath::getNodeType(void) {

	return TXFMBase::DOM_NODE_XPATH_NODESET;

}

// Methods to get output data

unsigned int TXFMXPath::readBytes(XMLByte * const toFill, unsigned int maxToFill) {

	return 0;

}

DOMDocument *TXFMXPath::getDocument() {

	return document;

}

DOMNode *TXFMXPath::getFragmentNode() {

	return NULL;

}

const XMLCh * TXFMXPath::getFragmentId() {

	return NULL;	// Empty string

}

XSECXPathNodeList	& TXFMXPath::getXPathNodeList() {

	return m_XPathMap;

}

#endif /* NO_XPATH */
