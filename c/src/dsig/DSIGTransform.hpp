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
 * DSIGTransform := Base (virtual) class that defines a DSIG Transform
 *
 */

#ifndef DSIGTRANSFORM_INCLUDE
#define DSIGTRANSFORM_INCLUDE

#include <xsec/dsig/DSIGConstants.hpp>
#include <xsec/framework/XSECDefs.hpp>

#include <xercesc/dom/DOM.hpp>

#include <stdio.h>

class DSIGSignature;
class TXFMBase;

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief The base class for transforms.
 *
 * The DSIGTransform class is the base class used to hold <Transform> elements
 * within a document.
 *
 * It does not in actually perform any transformations - only hold the information
 * about a transform in a <Signature> structure.
 *
 * @see TXFMBase
 *
 */


class DSIG_EXPORT DSIGTransform {

public:

	/** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor used for existing XML signatures.
	 *
	 * The Node structure already exists, so this type of Transform constructor
	 * will generally read the nodes in.
	 *
	 * @note DSIGTransform structures should only ever be created via calls to a
	 * DSIGTransformList object.
	 *
	 * @param sig The Signature structure in which this transform exists.
	 * @param node The DOM node (within doc) that is to be used as the base of the Transform.
	 * @see #load
	 */

	DSIGTransform(DSIGSignature *sig, DOMNode * node) : 
		  mp_txfmNode(node),
		  mp_parentSignature(sig) {};

    /**
	 * \brief Contructor used for new signatures.
	 *
	 * The Node structure will have to be created by the implementation class
	 *
	 * @note DSIGTransform structures should only ever be created via calls to a
	 * DSIGTransformList object.
	 *
	 * @param sig The Signature structure in which this transform exists.
	 *
	 */

	DSIGTransform(DSIGSignature *sig) : 
		  mp_txfmNode(NULL),
		  mp_parentSignature(sig) {};

		  
	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	virtual ~DSIGTransform() {};
	
	//@}

	/** @name Interface Methods */

	//@{

	/**
	 * \brief Determine the transform type.
	 *
	 * Used to determine what the type of the transform is.
	 *
	 */

	virtual transformType getTransformType() = 0;
	
	/**
	 * \brief Create the transformer element.
	 *
	 * Implemented by each Transform class and used by the DSIGSignature
	 * to construct a complete Transform list.
	 */

	virtual TXFMBase * createTransformer(TXFMBase * input) = 0;

	/**
	 * \brief Construct a new transform.
	 *
	 * Instruct the implementation to create the required
	 * transform and return the newly constructed DOMNode structure
	 */

	virtual DOMElement * createBlankTransform(DOMDocument * parentDoc) = 0;

	/**
	 * \brief Load a DOM structure
	 *
	 * Take the original node and load any sub nodes in the transform
	 * (if necessary)
	 */

	virtual void load(void) = 0;

	//@}

protected:

	/** @name Utility Functions */

	//@{

	/**
	 * \brief Create the basic node structure of a transform
	 *
	 */

	DOMElement * createTransformNode();


	DOMNode					* mp_txfmNode;			// The node that we read from
	DSIGSignature			* mp_parentSignature;	// Owning signature

private:

	DSIGTransform();
	DSIGTransform(const DSIGTransform &theOther);

};



#endif /* #define DSIGTRANSFORM_INCLUDE */
