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
 * XSECKeyInfoResolverDefault := Default (very basic) class for applications
 *						 to map KeyInfo to keys
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/enc/XSECKeyInfoResolverDefault.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoValue.hpp>
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/Janitor.hpp>

XSEC_USING_XERCES(Janitor);

// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------
XSECKeyInfoResolverDefault::XSECKeyInfoResolverDefault() {

	// Create a UTF-8 formatter
	XSECnew(mp_formatter, XSECSafeBufferFormatter("UTF-8",XMLFormatter::NoEscapes, 
												XMLFormatter::UnRep_CharRef));
}


XSECKeyInfoResolverDefault::~XSECKeyInfoResolverDefault() {

	if (mp_formatter != NULL)
		delete mp_formatter;

}


// --------------------------------------------------------------------------------
//           Interface Methods
// --------------------------------------------------------------------------------


XSECCryptoKey * XSECKeyInfoResolverDefault::resolveKey(DSIGKeyInfoList * lst) {

	// Try to find a key from the KeyInfo list as best we can
	// NOTE: No validation is performed (i.e. no cert/CRL checks etc.)

	XSECCryptoKey * ret = NULL;

	DSIGKeyInfoList::size_type sz = lst->getSize();

	for (DSIGKeyInfoList::size_type i = 0; i < sz; ++i) {

		switch (lst->item(i)->getKeyInfoType()) {

		case (DSIGKeyInfo::KEYINFO_X509) :
		{
			ret = NULL;
			const XMLCh * x509Str;
			XSECCryptoX509 * x509 = XSECPlatformUtils::g_cryptoProvider->X509();
			Janitor<XSECCryptoX509> j_x509(x509);

			x509Str = ((DSIGKeyInfoX509 *) lst->item(i))->getCertificateItem(0);
			
			if (x509Str != 0) {

				// The crypto interface classes work UTF-8
				safeBuffer transX509;

				transX509 << (*mp_formatter << x509Str);
				x509->loadX509Base64Bin(transX509.rawCharBuffer(), strlen(transX509.rawCharBuffer()));
				ret = x509->clonePublicKey();
			}

			if (ret != NULL)
				return ret;
		
		}
			break;

		case (DSIGKeyInfo::KEYINFO_VALUE_DSA) :
		{

			XSECCryptoKeyDSA * dsa = XSECPlatformUtils::g_cryptoProvider->keyDSA();
			Janitor<XSECCryptoKeyDSA> j_dsa(dsa);

			safeBuffer value;

			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getDSAP());
			dsa->loadPBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));
			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getDSAQ());
			dsa->loadQBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));
			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getDSAG());
			dsa->loadGBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));
			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getDSAY());
			dsa->loadYBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));

			j_dsa.release();
			return dsa;
		}
			break;

		case (DSIGKeyInfo::KEYINFO_VALUE_RSA) :
		{

			XSECCryptoKeyRSA * rsa = XSECPlatformUtils::g_cryptoProvider->keyRSA();
			Janitor<XSECCryptoKeyRSA> j_rsa(rsa);

			safeBuffer value;

			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getRSAModulus());
			rsa->loadPublicModulusBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));
			value << (*mp_formatter << ((DSIGKeyInfoValue *) lst->item(i))->getRSAExponent());
			rsa->loadPublicExponentBase64BigNums(value.rawCharBuffer(), strlen(value.rawCharBuffer()));

			j_rsa.release();
			return rsa;

		}

		default :
			break;

		}
	}

	return NULL;

}


XSECKeyInfoResolver * XSECKeyInfoResolverDefault::clone(void) {

	return new XSECKeyInfoResolverDefault();

}
