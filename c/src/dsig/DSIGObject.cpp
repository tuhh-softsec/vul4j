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
 * DSIGObject := Defines the container class used by dsig to hold objects
 *				 inside a signture
 *
 * $Id$
 *
 */

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/framework/XSECEnv.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/dsig/DSIGObject.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLUniDefs.hpp>

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//			String Constants
// --------------------------------------------------------------------------------

static XMLCh s_Object[] = {

	chLatin_O,
	chLatin_b,
	chLatin_j,
	chLatin_e,
	chLatin_c,
	chLatin_t,
	chNull
};

static XMLCh s_Id[] = {

	chLatin_I,
	chLatin_d,
	chNull
};

static XMLCh s_MimeType[] = {

	chLatin_M,
	chLatin_i,
	chLatin_m,
	chLatin_e,
	chLatin_T,
	chLatin_y,
	chLatin_p,
	chLatin_e,
	chNull
};

static XMLCh s_Encoding[] = {

	chLatin_E,
	chLatin_n,
	chLatin_c,
	chLatin_o,
	chLatin_d,
	chLatin_i,
	chLatin_n,
	chLatin_g,
	chNull
};

// --------------------------------------------------------------------------------
//           Constructors/Destructor
// --------------------------------------------------------------------------------

DSIGObject::DSIGObject(const XSECEnv * env, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *dom) {

	mp_env = env;
	mp_objectNode = dom;
	mp_idAttr = NULL;
	mp_mimeTypeAttr = NULL;
	mp_encodingAttr = NULL;

}

DSIGObject::DSIGObject(const XSECEnv * env) {

	mp_env = env;
	mp_objectNode = NULL;
	mp_idAttr = NULL;
	mp_mimeTypeAttr = NULL;
	mp_encodingAttr = NULL;

}



DSIGObject::~DSIGObject() {}

// --------------------------------------------------------------------------------
//           Library only
// --------------------------------------------------------------------------------


void DSIGObject::load(void) {

	if (mp_objectNode == NULL || 
		mp_objectNode->getNodeType() != DOMNode::ELEMENT_NODE || 
		!strEquals(getDSIGLocalName(mp_objectNode), s_Object)) {

		throw XSECException(XSECException::ObjectError,
			"Expected <Object> Node in DSIGObject::load");

	}


	mp_idAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_Id);
	mp_mimeTypeAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_MimeType);
	mp_encodingAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_Encoding);

}


DOMElement * DSIGObject::createBlankObject(void) {

	safeBuffer str;
	const XMLCh * prefix;
	DOMDocument *doc = mp_env->getParentDocument();

	prefix = mp_env->getDSIGNSPrefix();
	
	// Create the transform node
	makeQName(str, prefix, s_Object);
	mp_objectNode = doc->createElementNS(DSIGConstants::s_unicodeStrURIDSIG, str.rawXMLChBuffer());

	mp_idAttr = NULL;
	mp_mimeTypeAttr = NULL;
	mp_encodingAttr = NULL;

	return (DOMElement *) mp_objectNode;

}

// --------------------------------------------------------------------------------
//           Get functions
// --------------------------------------------------------------------------------


const XMLCh * DSIGObject::getId(void) {

	if (mp_idAttr != NULL)
		return mp_idAttr->getNodeValue();

	return NULL;

}

const XMLCh * DSIGObject::getMimeType(void) {

	if (mp_mimeTypeAttr != NULL)
		return mp_mimeTypeAttr->getNodeValue();

	return NULL;

}


const XMLCh * DSIGObject::getEncoding(void) {

	if (mp_encodingAttr != NULL)
		return mp_encodingAttr->getNodeValue();

	return NULL;

}

const DOMElement * DSIGObject::getElement(void) {

	return (DOMElement *) mp_objectNode;

}

// --------------------------------------------------------------------------------
//           Set Functions
// --------------------------------------------------------------------------------


void DSIGObject::setId(const XMLCh * id) {

	if (mp_idAttr != NULL) {

		mp_idAttr->setNodeValue(id);

	}

	else {

		((DOMElement *) mp_objectNode)->setAttributeNS(NULL, s_Id, id);
		mp_idAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_Id);

	}

}


void DSIGObject::setMimeType(const XMLCh * type) {

	if (mp_mimeTypeAttr != NULL) {

		mp_mimeTypeAttr->setNodeValue(type);

	}

	else {

		((DOMElement *) mp_objectNode)->setAttributeNS(NULL, s_MimeType, type);
		mp_mimeTypeAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_MimeType);

	}

}

void DSIGObject::setEncoding(const XMLCh * encoding) {

	if (mp_encodingAttr != NULL) {

		mp_encodingAttr->setNodeValue(encoding);

	}

	else {

		((DOMElement *) mp_objectNode)->setAttributeNS(NULL, s_Encoding, encoding);
		mp_encodingAttr = ((DOMElement *) mp_objectNode)->getAttributeNodeNS(NULL, s_Encoding);

	}

}


void DSIGObject::appendChild(DOMNode * child) {

	if (mp_objectNode == NULL) {

		throw XSECException(XSECException::ObjectError,
			"DSIGObject::appendChild - Object node has not been created");

	}

	mp_objectNode->appendChild(child);

}

