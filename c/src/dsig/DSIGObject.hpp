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

#ifndef DSIGOBJECT_INCLUDE
#define DSIGOBJECT_INCLUDE

// XSEC Includes

#include <xsec/framework/XSECDefs.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMNode);

class XSECEnv;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Base class for \<Object\> nodes in a \<Signature\> element.
 *
 * The DSIG spec allows for enveloping signatures, in which the signature holds
 * the information it is signing.  For these types of signatures, the data being
 * signed can be held in an \<Object\> container.
 *
 * This class allows callers to and manipulate Object containers.
 *
 */


class DSIG_EXPORT DSIGObject {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Construct from an owning signature
	 *
	 * Called by the library when an Object needs to be created from an Object
	 * in a DOM tree.
	 *
	 * @param env The environment that the Object is operating within
	 * @param dom The DOM node that will be loaded
	 */

	DSIGObject(const XSECEnv * env, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode *dom);

	/**
	 * \brief Construct a new object
	 *
	 * Called by the library to create an Object from scratch
	 *
	 * @param env The environment the Object is operating within
	 */

	DSIGObject(const XSECEnv * env);


	/**
	 * \brief Destructor
	 */

	~DSIGObject();

	//@}

	/** @name Library functions */
	//@{

	/**
	 * \brief Load the object from DOM
	 *
	 * Called by the library to load a constructed object
	 */

	void load(void);

	/**
	 * \brief Create a new Object
	 *
	 * Create a new Object from scratch (will generate the DOM)
	 */

	XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankObject(void);

	//@}

	/** @name Get functions */
	//@{

	/**
	 * \brief Get the Id for this object
	 *
	 * @returns the URI attribute string for this object
	 */

	const XMLCh * getId(void);

	/**
	 * \brief Returns the MimeType string of this object
	 *
	 * @returns a pointer to the buffer containing the Mime Type string
	 */

	const XMLCh * getMimeType(void);

	/**
	 * \brief Returns the Encoding string of this object
	 *
	 * @returns a pointer to the buffer containing the Encoding string
	 */

	const XMLCh * getEncoding(void);

	/**
	 * \brief Returns the Element node for this object
	 *
	 * @returns the Element node at the head of this object
	 */

	const XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * getElement(void);

	//@}

	/** @name Setter functions */
	//@{
	
	/**
	 * \brief Set the Id attribute for this Object
	 *
	 * @id String to use for the Id attribute
	 */

	void setId(const XMLCh * id);

	/**
	 * \brief Set the Id attribute for this Object
	 *
	 * @type String to use for the MimeType attribute
	 */

	void setMimeType(const XMLCh * type);

	/**
	 * \brief Set the Encoding attribute for this Object
	 *
	 * @encoding String to use for the Encoding attribute
	 */

	void setEncoding(const XMLCh * encoding);

	/**
	 * \brief Add a child node to the Object
	 *
	 * This is a "ease of use" function to allow users to add a DOM structure
	 * that has been built previously into the Object element
	 */

	void appendChild(XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * child);

	//@}


private:

	const XSECEnv		* mp_env;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
						* mp_objectNode;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
						* mp_idAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
						* mp_mimeTypeAttr;
	XERCES_CPP_NAMESPACE_QUALIFIER DOMNode
						* mp_encodingAttr;

};

#endif /* DSIGOBJECT_INCLUDE */

