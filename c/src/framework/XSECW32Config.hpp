/*
 * Copyright 2002-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

/*
 * XSEC
 * 
 * Configuration file for Windows platform
 *
 * Needs to be modified by hand
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xercesc/util/XercesVersion.hpp>

#define XSEC_VERSION	     "1.1.0"
#define XSEC_VERSION_MAJOR   1
#define XSEC_VERSION_MEDIUM  1
#define XSEC_VERSION_MINOR   0

/*
 * Because we don't have a configure script, we need to rely on version
 * numbers to understand library idiosycracies
 */

#if (XERCES_VERSION_MAJOR >= 2) && (XERCES_VERSION_MINOR >= 3)
/* 
 * As of version 2.3, xerces requires a version parameter in XMLFormatter
 * constructors
 */
#	define XSEC_XERCES_FORMATTER_REQUIRES_VERSION 1

 /* 2.3 and above use a user defined Memory Manager.  In some cases, this
   actually needs to be passed to functions
*/

#	define XSEC_XERCES_REQUIRES_MEMMGR 1

#else
/*
 * In version 2.2, the XMLUri class was broken for relative URI de-referencing
 */
#	define XSEC_XERCES_BROKEN_XMLURI 1
#endif



/*
 * The following defines whether Xalan integration is required.
 *
 * Xalan is used for XSLT and complex XPath processing.
 * Activate this #define if Xalan is not required (or desired)
 */

/* #define XSEC_NO_XALAN */

/*
 * Define presence of cryptographic providers
 */

#define HAVE_OPENSSL 1

#define HAVE_WINCAPI 1


/*
 * Some settings for OpenSSL if we have it
 *
 */

#if defined (HAVE_OPENSSL)

#	include <openssl/opensslv.h>
#	if (OPENSSL_VERSION_NUMBER >= 0x00907000)

#		define XSEC_OPENSSL_CONST_BUFFERS
#		define XSEC_OPENSSL_HAVE_AES
#		define XSEC_OPENSSL_CANSET_PADDING
#	endif

#endif

/*
 * Macros used to determine what header files exist on this
 * system
 */

/* Posix unistd.h */
/* #define HAVE_UNISTD_H */

/* Windows direct.h */
#define HAVE_DIRECT_H 1


