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
 * DSIGTransformC14n := Class that holds information about C14n transforms
 *
 * Author(s): Berin Lautenbach
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
 * @brief Transform holder for C14n based transforms.
 *
 * The DSIGTransformC14n class is used to hold C14n <Transform> elements
 * within a document.  This includes Exclusive and Comment options
 *
 *
 * @see TXFMC14n
 * @see DSIGTransform
 *
 */

class DSIG_EXPORT DSIGTransformC14n : public DSIGTransform {

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

	DSIGTransformC14n(DSIGSignature *sig, DOMNode * node);

    /**
	 * \brief Contructor used for new signatures.
	 *
	 * The Node structure will have to be created.
	 *
	 * @note DSIGTransform structures should only ever be created via calls to a
	 * DSIGTransformList object.
	 *
	 * @param sig The Signature structure in which this transform exists.
	 * @see createBlankTransform
	 */

	DSIGTransformC14n(DSIGSignature *sig);
		  
	/**
	 * \brief Destructor.
	 *
	 * Destroy the DSIGSignature elements.
	 *
	 * Does not destroy any associated DOM Nodes
	 */
		  
	virtual ~DSIGTransformC14n();
	
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
	 * \brief Create the Canonicalising transformer element.
	 *
	 * Implemented by each Transform class and used by the DSIGSignature
	 * when consructing a TXFM List that includes canonicalisation (nearly always)
	 */

	virtual void appendTransformer(TXFMChain * input);

	/**
	 * \brief Construct blank Canonicalisation Transform element.
	 *
	 * Instruct the implementation to create the required
	 * transform and return the newly constructed DOMNode structure
	 *
	 * @note By default creates a non-comment non-exclusive C14n transform.
	 */

	virtual DOMElement * createBlankTransform(DOMDocument * parentDoc);

	/**
	 * \brief Load a DOM structure
	 *
	 * (Re)read the DOM structure into the Signature structures
	 *
	 */

	virtual void load(void);

	//@}

	/** @name C14n Specific Methods */

	//@{
	
	/**
	 * \brief Change canonicalisation method.
	 *
	 * Set the canonicalisation method to the type indicated.  If this changes
	 * the transform from Exclusive to Standard C14n, any associated
	 * InclusiveNamespaces children will be removed.
	 *
	 * If this is moving from one form of Exclusive to another, any InclusiveNamespace
	 * children will remain.
	 *
	 * @param method Type of canicaliser required.
	 *
	 * @see canonicalizationMethod
	 */

	void setCanonicalizationMethod(canonicalizationMethod method);

	/**
	 * \brief Get canonicalisation type.
	 *
	 * Return the type of canonicalisation to the caller.
	 *
	 * @returns Canonicalisation type.
	 * @see canonicalizationMethod
	 */

	canonicalizationMethod getCanonicalizationMethod(void);

	/**
	 * \brief Add a namespace prefix to the InclusiveNamespaces list
	 *
	 * Exclusive canonicalisation includes the ability to define a PrefixList of
	 * namespace prefixes that will not be treated exclusively, rather they will
	 * be handled as per normal C14n.
	 *
	 * This function allows a caller to add a prefix to this list
	 * @param ns The prefix to add.
	 */

	void addInclusiveNamespace(const char * ns);

	/**
	 * \brief Get the string containing the inclusive namespaces.
	 *
	 * Get the string containing a list of (space separated) prefixes that will
	 * be handled non-exclusively in an exclusive C14n transform.
	 *
	 * @returns A pointer to the string held in the node (NULL if no namespaces defined).
	 * @note The pointer returned is owned by the Transform structure - do not delete.
	 */

	 const XMLCh * getPrefixList(void);

	 /**
	  * \brief Delete all inclusive namespaces.
	  *
	  * Deletes the structures (including the DOM nodes) that hold the inclusive
	  * namespaces list.
	  */

	 void clearInclusiveNamespaces(void);

	//@}

	
private:

	DSIGTransformC14n();
	DSIGTransformC14n(const DSIGTransformC14n & theOther);

	canonicalizationMethod			m_cMethod;			// The method
	DOMElement						* mp_inclNSNode;	// Node holding the inclusive Namespaces
	const XMLCh						* mp_inclNSStr;		// String holding the namespaces							
};