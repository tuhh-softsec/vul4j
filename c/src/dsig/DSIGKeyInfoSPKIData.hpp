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
 * DSIGKeyInfoSPKIData := SPKI Information
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef DSIGKEYINFOSPKIDATA_INCLUDE
#define DSIGKEYINFOSPKIDATA_INCLUDE

#include <xsec/dsig/DSIGKeyInfo.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>

XSEC_DECLARE_XERCES_CLASS(DOMElement);

#include <vector>

/**
 * @ingroup pubsig
 * @{
 */

/**
 * @brief The class for \<SPKIData\> nodes in a KeyInfo list.
 *
 * Class for holding information on a SPKIData node as well as setting
 * such a node in a signature.
 *
 */



class DSIG_EXPORT DSIGKeyInfoSPKIData : public DSIGKeyInfo {

public:

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Constructor used when XML exists.
	 *
	 * This constructor is used by DSIGSignature objects to load
	 * an existing DOM structure into the Name element.
	 *
	 * @param sig Calling signature object.
	 * @param nameNode DOMNode to load information from
	 */

	DSIGKeyInfoSPKIData(DSIGSignature * sig, DOMNode *nameNode);

	/**
	 * \brief Constructor used when XML needs to be created.
	 *
	 * This constructor is used by DSIGSignature objects to
	 * create a DSIGKeyInfoSPKIData object that can then be used
	 * to create the required XML.
	 *
	 * @param sig Calling signature object.
	 */

	DSIGKeyInfoSPKIData(DSIGSignature * sig);

	/**
	 * \brief Destructor
	 */

	virtual ~DSIGKeyInfoSPKIData();

	//@}

	/** @name Load and Get functions */
	//@{

	/**
	 * \brief Load an existing XML structure into this object.
	 */

	void load(void);

	/**
	 * \brief Get the number of S-expressions 
	 *
	 * Returns the number of S-expressions held for this SPKIData element
	 *
	 * @returns The number of S-expressions
	 */

	unsigned int getSexpSize(void);

	/**
	 * \brief returns the indicated SExpression
	 *
	 * Returns a pointer to a XMLCh buffer holding the required SExpression.
	 *
	 * @param index The number of the SExpression to return
	 * @returns A pointer to the char buffer containing the base64 encoded 
	 * S-expression
	 */

	virtual const XMLCh * getSexp(unsigned int index);

	/**
	 * \brief Get key name - unimplemented for SPKI packets
	 */

	virtual const XMLCh * getKeyName(void) {return NULL;}

	//@}

	/**@name Create and set functions */
	//@{
	
	
	/**
	 * \brief Create a new SPKIData element in the document.
	 *
	 * Creates a new SPKIData element and sets the first S-expression
	 * with the string passed in.
	 *
	 * @param Sexp Value (base64 encoded string) to set the first S-expression
	 * @returns The newly created DOMElement with the structure underneath.
	 */

	DOMElement * createBlankSPKIData(const XMLCh * Sexp);

	/**
	 * \brief Append a new SPKISexp element to the SPKIData nodes
	 *
	 * Append a new SPKISexp element to the list of S-expressions that
	 * already exists.
	 *
	 * @param Sexp Value (base64 encoded string) to set the new S-expression
	 */

	void appendSexp(const XMLCh * Sexp);

	//@}

	/** @name Information Functions */
	//@{
	
	/**
	 * \brief Return type of this KeyInfo element
	 */
	
	virtual keyInfoType getKeyInfoType(void) {return DSIGKeyInfo::KEYINFO_SPKIDATA;}

	//@}

private:

	DSIGKeyInfoSPKIData();						// Non-implemented constructor

	struct SexpNode {
		const XMLCh		* mp_expr;
		DOMNode			* mp_exprTextNode;
	};

	typedef std::vector<SexpNode *> sexpVectorType;

	sexpVectorType		m_sexpList;

};



#endif /* #define DSIGKEYSPKIDATA_INCLUDE */
