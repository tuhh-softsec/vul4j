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
 * DSIGTransformList := List that holds all the transforms in the Signature.
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#ifndef DSIGTRANSFORMLIST_INCLUDE
#define DSIGTRANSFORMLIST_INCLUDE


// XSEC Includes
#include <xsec/framework/XSECDefs.hpp>

// General includes
#include <vector>

class DSIGTransform;

/**
 * @ingroup internal
 * @{
 */

/**
 * @brief The class used for holding Transform Elements within a signature.
 *
 * This class is the container for the <Transforms> list in a Reference or
 * KeyInfo list.  It holds a list of Transform elements that can be
 * manipulated by the caller, or asked to provide the appropriate
 * TXFM* class to actually perform a transform.
 *
 */


class DSIGTransformList {

public:

#if defined(XSEC_NO_NAMESPACES)
	typedef vector<DSIGTransform *>			TransformListVectorType;
#else
	typedef std::vector<DSIGTransform *>	TransformListVectorType;
#endif

#if defined(XSEC_SIZE_T_IN_NAMESPACE_STD)
	typedef std::size_t		size_type;
#else
	typedef size_t			size_type;
#endif

	/** name Constructors and Destructors */
	//@{

	/**
	 * \brief Construct the list
	 *
	 */

	DSIGTransformList();

	/**
	 * \brief Destroy all Transform resources
	 *
	 * Destroys the list - including the contained DSIGTransform* elements.
	 * Does not destroy the underlying DOM structure.
	 *
	 */

	~DSIGTransformList();

	//@}

	/** name Manipulate existing structures */
	//@{

	/**
	 * \brief Add a transform to the list
	 *
	 * Should never be called directly - will add a pre-built
	 * transform to the list.
	 *
	 * @note Will not add any DOM structures
	 * @param ref The transform structure to add
	 */

	void addTransform(DSIGTransform * ref);

	/**
	 * \brief Remove a transform from the list.
	 *
	 * Should never be called directly - will simply remove the element
	 * without deleting
	 */

	void removeTransform(size_type index);

	/**
	 * \brief Delete the transform at the indicated position.
	 *
	 * @param index The position to delete from.
	 */

	DSIGTransform * item(size_type index);

	/** 
	 * \brief Get the number of items.
	 *
	 */

	size_type	getSize();

	/**
	 * \brief Remove all elements - but delete none.
	 */

	bool empty();


	// Get information

private:

	TransformListVectorType					m_transformList;
};


#endif /* DSIGTRANSFORMLIST_INCLUDE */
