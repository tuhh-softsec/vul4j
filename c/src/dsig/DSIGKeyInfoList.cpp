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
 * DSIGKeyInfoList := Class for Loading and storing a list of KeyInfo elements
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// XSEC Includes
#include <xsec/dsig/DSIGKeyInfoList.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoName.hpp>
#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/dsig/DSIGKeyInfoPGPData.hpp>
#include <xsec/dsig/DSIGKeyInfoSPKIData.hpp>
#include <xsec/dsig/DSIGKeyInfoMgmtData.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/dsig/DSIGTransformList.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/transformers/TXFMBase.hpp>

#include <xercesc/util/Janitor.hpp>

XERCES_CPP_NAMESPACE_USE

DSIGKeyInfoList::DSIGKeyInfoList(const XSECEnv * env) :
mp_env(env) {}

DSIGKeyInfoList::~DSIGKeyInfoList() {

	empty();

}

// Actions

void DSIGKeyInfoList::addKeyInfo(DSIGKeyInfo * ref) {

	m_keyInfoList.push_back(ref);

}

DSIGKeyInfo * DSIGKeyInfoList::removeKeyInfo(size_type index) {

	if (index < m_keyInfoList.size())
		return m_keyInfoList[index];

	return NULL;

}

size_t DSIGKeyInfoList::getSize() {

	return m_keyInfoList.size();

}


DSIGKeyInfo * DSIGKeyInfoList::item(size_type index) {

	if (index < m_keyInfoList.size())
		return m_keyInfoList[index];
	
	return NULL;

}

void DSIGKeyInfoList::empty() {

	size_type i, s;
	s = getSize();

	for (i = 0; i < s; ++i)
		delete m_keyInfoList[i];

	m_keyInfoList.clear();

}

bool DSIGKeyInfoList::isEmpty() {

		return (m_keyInfoList.size() == 0);

}

// --------------------------------------------------------------------------------
//           Add a KeyInfo based on XML DomNode source
// --------------------------------------------------------------------------------


bool DSIGKeyInfoList::addXMLKeyInfo(DOMNode *ki) {

	// return true if successful - does not throw if the node type is unknown

	if (ki == 0)
		return false;

	DSIGKeyInfo * k;

	if (strEquals(getDSIGLocalName(ki), "X509Data")) {

		// Have a certificate!
		XSECnew(k, DSIGKeyInfoX509(mp_env, ki));
	}

	else if (strEquals(getDSIGLocalName(ki), "KeyName")) {

		XSECnew(k, DSIGKeyInfoName(mp_env, ki));
	}

	else if (strEquals(getDSIGLocalName(ki), "KeyValue")) {

		XSECnew(k, DSIGKeyInfoValue(mp_env, ki));
	}

	else if (strEquals(getDSIGLocalName(ki), "PGPData")) {

		XSECnew(k, DSIGKeyInfoPGPData(mp_env, ki));
	}

	else if (strEquals(getDSIGLocalName(ki), "SPKIData")) {

		XSECnew(k, DSIGKeyInfoSPKIData(mp_env, ki));
		
	}

	else if (strEquals(getDSIGLocalName(ki), "MgmtData")) {

		XSECnew(k, DSIGKeyInfoMgmtData(mp_env, ki));
		
	}

	else {

		return false;

	}

	// Now we know what the element type is - do the load and save

	try {
		k->load();
	}
	catch (...) {
		delete k;
		throw;
	}

	// Add
	this->addKeyInfo(k);

	return true;

}

// --------------------------------------------------------------------------------
//           Retrieve a complete KeyInfo list
// --------------------------------------------------------------------------------


bool DSIGKeyInfoList::loadListFromXML(DOMNode * node) {

	if (node == NULL || !strEquals(getDSIGLocalName(node), "KeyInfo")) {
		throw XSECException(XSECException::ExpectedDSIGChildNotFound,
			"DSIGKeyInfoList::loadListFromXML - expected KeyInfo node");
	}

	DOMNode *tmpKI = findFirstChildOfType(node, DOMNode::ELEMENT_NODE);
	
	while (tmpKI != 0) {

		// Find out what kind of KeyInfo child it is

		if (tmpKI != 0 && strEquals(getDSIGLocalName(tmpKI), "RetrievalMethod")) {

			// A reference to key information held elsewhere

			const XMLCh * URI = NULL;
			TXFMBase * currentTxfm;
			bool isRawX509 = false;

			DOMNamedNodeMap *atts = tmpKI->getAttributes();
			const XMLCh * name;
			unsigned int size;

			if (atts == 0 || (size = atts->getLength()) == 0)
				return true;

			for (unsigned int i = 0; i < size; ++i) {

				name = atts->item(i)->getNodeName();

				if (strEquals(name, "URI")) {
					URI  = atts->item(i)->getNodeValue();
				}

				else if (strEquals(name, "Type")) {

					// Check if this is a raw X509 cert
					if (strEquals(atts->item(i)->getNodeValue(), DSIGConstants::s_unicodeStrURIRawX509)) {
						isRawX509 = true;
					}

				}

				else if (strEquals(name, "Id")) {

					// For now ignore

				}

				else {
					safeBuffer tmp, error;

					error << (*mp_env->getSBFormatter() << name);
					tmp.sbStrcpyIn("Unknown attribute in <RetrievalMethod> Element : ");
					tmp.sbStrcatIn(error);

					throw XSECException(XSECException::UnknownDSIGAttribute, tmp.rawCharBuffer());

				}

			}

			if (isRawX509 == true) {

				if (URI == NULL) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound,
						"Expected to find a URI attribute in a rawX509RetrievalMethod KeyInfo");

				}

				DSIGKeyInfoX509 * x509;
				XSECnew(x509, DSIGKeyInfoX509(mp_env));
				x509->setRawRetrievalURI(URI);

				addKeyInfo(x509);

			}

			else {

				// Find base transform using the base URI
				currentTxfm = DSIGReference::getURIBaseTXFM(mp_env->getParentDocument(), URI, mp_env->getURIResolver());
				TXFMChain * chain;
				XSECnew(chain, TXFMChain(currentTxfm));
				Janitor<TXFMChain> j_chain(chain);

				// Now check for transforms
				tmpKI = tmpKI->getFirstChild();

				while (tmpKI != 0 && (tmpKI->getNodeType() != DOMNode::ELEMENT_NODE))
					// Skip text and comments
					tmpKI = tmpKI->getNextSibling();

				if (tmpKI == 0) {

					throw XSECException(XSECException::ExpectedDSIGChildNotFound, 
							"Expected <Transforms> within <KeyInfo>");

				}

				if (strEquals(getDSIGLocalName(tmpKI), "Transforms")) {


					// Process the transforms using the static function.
					// For the moment we don't really support remote KeyInfos, so
					// Just built the transform list, process it and then destroy it.

					DSIGTransformList * l = DSIGReference::loadTransforms(
						tmpKI,
						mp_env->getSBFormatter(),
						mp_env);

					DSIGTransformList::TransformListVectorType::size_type size, i;
					size = l->getSize();
					for (i = 0; i < size; ++ i) {
						try {
							l->item(i)->appendTransformer(chain);
						}
						catch (...) {
							delete l;
							throw;
						}
					}

					delete l;

				}

				// Find out the type of the final transform and process accordingly
				
				TXFMBase::nodeType type = chain->getLastTxfm()->getNodeType();

				XSECXPathNodeList lst;
				const DOMNode * element;

				switch (type) {

				case TXFMBase::DOM_NODE_DOCUMENT :

					break;

				case TXFMBase::DOM_NODE_DOCUMENT_FRAGMENT :

					break;

				case TXFMBase::DOM_NODE_XPATH_NODESET :

					lst = chain->getLastTxfm()->getXPathNodeList();
					element = lst.getFirstNode();

					while (element != NULL) {

						// Try to add each element - just call KeyInfoList add as it will
						// do the check to see if it is a valud KeyInfo

						addXMLKeyInfo((DOMNode *) element);
						element = lst.getNextNode();

					}

					break;

				default :

					throw XSECException(XSECException::XPathError);

				}

				// Delete the transform chain
				chain->getLastTxfm()->deleteExpandedNameSpaces();

				// Janitor will clean up chain
			}

		} /* if getNodeName == Retrieval Method */

		// Now just run through each node type in turn to process "local" KeyInfos

		else if (!addXMLKeyInfo(tmpKI)) {

			throw XSECException(XSECException::KeyInfoError,
				"Unknown KeyInfo element found");

		}

		tmpKI = tmpKI->getNextSibling();

		while (tmpKI != 0 && (tmpKI->getNodeType() != DOMNode::ELEMENT_NODE))
			tmpKI = tmpKI->getNextSibling();

	}

	return true;
}