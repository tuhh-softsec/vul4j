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
 * XSECDefs := File for general XSEC definitions
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

// Use Xerces to do the "hard work in determining compilers etc." for us

#ifndef XSECDEFS_HEADER
#define XSECDEFS_HEADER

// Include the generated include file

#if defined (_WIN32)
#	if defined (_DEBUG)
#		include <afx.h>
#		include <crtdbg.h>
#	endif
#	include <xsec/framework/XSECW32Config.hpp>
#else
#	include <xsec/framework/XSECConfig.hpp>
#endif

// General includes

#include <assert.h>
#include <stdlib.h>

// Xalan

//#include <Include/PlatformDefinitions.hpp>

// Xerces

#include <xercesc/util/XercesDefs.hpp>

// --------------------------------------------------------------------------------
//           Namespace Handling
// --------------------------------------------------------------------------------

// Use an approach similar to that used in Xalan to process Xerces namespaces

#if defined(XERCES_HAS_CPP_NAMESPACE)
#	define XSEC_USING_XERCES(NAME) using XERCES_CPP_NAMESPACE :: NAME
#	define XSEC_DECLARE_XERCES_CLASS(NAME) namespace XERCES_CPP_NAMESPACE { class NAME; } \
										   using XERCES_CPP_NAMESPACE::NAME
#	define XSEC_DECLARE_XERCES_STRUCT(NAME) namespace XERCES_CPP_NAMESPACE { struct NAME; }
#else
#	define XERCES_CPP_NAMESPACE_QUALIFIER
#	define XERCES_CPP_NAMESPACE_BEGIN
#	define XERCES_CPP_NAMESPACE_END
#	define XERCES_CPP_NAMESPACE_USE
#	define XSEC_USING_XERCES(NAME)
#	define XSEC_DECLARE_XERCES_CLASS(NAME) class NAME;
#	define XSEC_DECLARE_XERCES_STRUCT(NAME) struct NAME;
#endif


// --------------------------------------------------------------------------------
//           Project Library Handling
// --------------------------------------------------------------------------------

#if defined (PROJ_CANON)

#define CANON_EXPORT PLATFORM_EXPORT
#else
#define CANON_EXPORT PLATFORM_IMPORT
#endif

#if defined (PROJ_DSIG)

#define DSIG_EXPORT PLATFORM_EXPORT
#else
#define DSIG_EXPORT PLATFORM_IMPORT
#endif

// Platform includes.  Much of this is taken from Xalan

#if defined(_MSC_VER)

// Microsoft VC++

#	pragma warning(disable: 4127 4251 4511 4512 4514 4702 4710 4711 4786 4097; error: 4150 4172 4238 4239 4715)
#	define XSEC_NO_COVARIANT_RETURN_TYPE

#elif defined(__GNUC__)
#elif defined(__INTEL_COMPILER)
#else
#error Unknown compiler.
#endif


// Configuration includes

// We want to use XPath calculated transforms

//#define XSEC_USE_XPATH_ENVELOPE


// Given the configuration - what should we set?

#ifdef XSEC_NO_XALAN

// Xalan is not available!

#	define XSEC_NO_XPATH
#	define XSEC_NO_XSLT

#endif	/* XSEC_NO_XALAN */

#ifdef XSEC_NO_XPATH

#	ifdef XSEC_USE_XPATH_ENVELOPE
#		undef XSEC_USE_XPATH_ENVELOPE
#	endif

#endif

#endif /* XSECDEFS_HEADER */
