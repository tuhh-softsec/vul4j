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
 * XSECKeyInfoResolver := Virtual interface class for applications
 *						 to map KeyInfo to keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#ifndef XSECKEYINFORESOLVER_INCLUDE
#define XSECKEYINFORESOLVER_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xsec/dsig/DSIGKeyInfoList.hpp>
#include <xsec/enc/XSECCryptoKey.hpp>

/**
 * @ingroup interfaces
 */
/*\@{*/

/**
 * @brief Interface class for providing keys to the library.
 *
 * The XSEC library does not perform actual mapping of KeyInfo
 * elements to encryption keys.  (In some cases a KeyInfo might
 * not even be provided).
 *
 * This interface class allows the application to perform this
 * key mapping and provision function.  Applications can perform
 * whatever steps are necessary to determine the appropriate key
 * and what trust level it might have.
 *
 */

class DSIG_EXPORT XSECKeyInfoResolver {

public :

	/** @name Constructors and Destructors */
	//@{

	XSECKeyInfoResolver() {};
	virtual ~XSECKeyInfoResolver() {};

	//@}

	/** @name Interface Functions */
	//@{

	/**
	 * \brief Provide a key to the library
	 *
	 * The library will pass the KeyInfoList to the resolver
	 * which then needs to provide a key back to the library.
	 * The key may have absolutely no relationship to the KeyInfoList
	 * (which is only supposed to provide a hint).
	 *
	 * If no KeyInfo elements were provided in the Signature,
	 * NULL will be passed in.
	 *
	 * @param lst The list of KeyInfo elements from the signature
	 * @returns Either the appropriate key or NULL if none can be found
	 */

	virtual XSECCryptoKey * resolveKey(DSIGKeyInfoList * lst) = 0;

	/**
	 * \brief Clone the resolver to be installed in a new object.
	 *
	 * When KeyInfoResolvers are passed into signatures and other
	 * objects, they are cloned and control of the original object
	 * is left with the caller.
	 *
	 */

	virtual XSECKeyInfoResolver * clone(void) = 0;

	//@}

	/*\@}*/
};


#endif /* XSECKEYINFORESOLVER_INCLUDE */
