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
 * DSIGTransformEnvelope := Class that holds an Envelope transform
 *
 * $Id$
 *
 */

#include <xsec/dsig/DSIGTransform.hpp>
 
/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief Transform holder for Base 64.
 *
 * The DSIGTransformEnvelope class is used to hold base64 \<Transform\> elements
 * within a document.
 *
 * This is a very simple transform with no real options.
 *
 * @see TXFMBase
 * @see DSIGTransform
 *
 */



class DSIG_EXPORT DSIGTransformEnvelope : public DSIGTransform {

public:

	/** @name Constructors and Destructors */
    //@{
	
    /**
	 * \brief Contructor used for existing XML signatures.
	 *
	 * The Node structure already exists, so read the nodes in.
	 *
	 * @param env The operating environment
	 * @param node The DOM node (within doc) that is to be used as the base of the Transform.
	 * @see #load
	 */

	DSIGTransformEnvelope(const XSECEnv * env, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * node);

    /**
	 * \brief Contructor used for new signatures.
	 *
	 * The Node structure will have to be created.
	 *
	 * @param env The operating environment
	 * @see createBlankTransform
	 */

	DSIGTransformEnvelope(const XSECEnv * env);
		  
	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	virtual ~DSIGTransformEnvelope();
	
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
	 * \brief Create the Enevelope transformer element.
	 *
	 * Implemented by each Transform class and used by the DSIGSignature
	 * to construct a complete Transform list.
	 */

	virtual void appendTransformer(TXFMChain * input);

	/**
	 * \brief Construct blank Envelope Transform element.
	 *
	 * Instruct the implementation to create the required
	 * transform and return the newly constructed DOMNode structure
	 */

	virtual XERCES_CPP_NAMESPACE_QUALIFIER DOMElement * 
		createBlankTransform(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument * parentDoc);

	/**
	 * \brief Load a DOM structure
	 *
	 * For a Envelope transform this does almost nothing.
	 *
	 */

	virtual void load(void);

	//@}

	
private:

	DSIGTransformEnvelope();
	DSIGTransformEnvelope(const DSIGTransformEnvelope & theOther);

};
