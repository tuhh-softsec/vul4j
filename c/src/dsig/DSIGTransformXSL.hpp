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
 * DSIGTransformXSL := Class that performs XML Stylesheet Language transforms
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGTransform.hpp>

// Xerces

#include <xercesc/dom/DOM.hpp>

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Transform holder for XSLT Transforms.
 *
 * The DSIGTransformXSL class is used to hold XSLT \<Transform\> elements
 * within a document.
 *
 *
 * @see TXFMXSL
 * @see DSIGTransform
 *
 */



class DSIG_EXPORT DSIGTransformXSL : public DSIGTransform {

public:

	/** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor used for existing XML signatures.
	 *
	 * The Node structure already exists, so read the nodes in.
	 *
	 * @param sig The Signature structure in which this transform exists.
	 * @param node The DOM node (within doc) that is to be used as the base of the Transform.
	 * @see #load
	 */

	DSIGTransformXSL(DSIGSignature *sig, DOMNode * node);

    /**
	 * \brief Contructor used for new signatures.
	 *
	 * The Node structure will have to be created.
	 *
	 * @param sig The Signature structure in which this transform exists.
	 * @see createBlankTransform
	 */

	DSIGTransformXSL(DSIGSignature *sig);
		  
	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	virtual ~DSIGTransformXSL();
	
	//@}

	/** @name Interface Methods */

	//@{

	/**
	 * \brief Determine the transform type.
	 *
	 * Used to determine what the type of the transform is.
	 *
	 */

	virtual transformType getTransformType();

	/**
	 * \brief Create the XSLT transformer element.
	 *
	 */

	virtual void appendTransformer(TXFMChain * input);

	/**
	 * \brief Construct blank XSLT Transform element.
	 *
	 * Instruct the implementation to create the required
	 * transform and return the newly constructed DOMNode structure
	 */

	virtual DOMElement * createBlankTransform(DOMDocument * parentDoc);

	/**
	 * \brief Load a DOM structure
	 *
	 * Find the start of the XSLT transform and load.
	 *
	 */

	virtual void load(void);

	//@}

	/** @name XSLT Specific Methods */

	//@{

	/**
	 * \brief Set the DOM tree nodes beneath for the actual Transform
	 *
	 * The caller needs to have already created the DOM tree with the 
	 * XSLT embedded.
	 *
	 * @returns The old transform Element node if it existed.
	 * @param stylesheet The new stylesheet to insert into the document
	 * @note Does not delete the old stylesheet if one existed.  This is returned
	 * to the caller who is expected to delete it.
	 */

	DOMNode * setStylesheet(DOMNode * stylesheet);

	/**
	 * \brief Get the stylesheet node.
	 *
	 * Obtain the DOMNode at the top of the stylesheet that will be used in this
	 * transform.
	 *
	 * @returns The top stylesheet node
	 */

	DOMNode * getStylesheet(void);

	//@}
	
private:

	DSIGTransformXSL();
	DSIGTransformXSL(const DSIGTransformXSL & theOther);

	DOMNode				* mp_stylesheetNode;

};
