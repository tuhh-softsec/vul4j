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
 * XSECTXFMInputSource := Transfomer InputSource for Xerces Parser.
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 * $Log$
 * Revision 1.4  2003/07/28 12:52:46  blautenb
 * Fixed a bug with DEBUG_NEW when compiling with Xalan 1.6
 *
 * Revision 1.3  2003/07/05 10:30:37  blautenb
 * Copyright update
 *
 * Revision 1.2  2003/02/21 11:53:09  blautenb
 * TXFMChain to prevent memory leaks
 *
 * Revision 1.1  2003/02/17 11:19:12  blautenb
 * Class to use a transform as an InputSource to Xerces
 *
 *					 
 */


#include "XSECTXFMInputSource.hpp"
#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/utils/XSECBinTXFMInputStream.hpp>
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/BinInputStream.hpp>

XSEC_USING_XERCES(BinInputStream);


// --------------------------------------------------------------------------------
//           Construct/Destruct
// --------------------------------------------------------------------------------

XSECTXFMInputSource::XSECTXFMInputSource(TXFMChain * chain, bool deleteWhenDone) :

mp_chain(chain),
m_deleteWhenDone(deleteWhenDone) {
}


XSECTXFMInputSource::~XSECTXFMInputSource() {
}

// --------------------------------------------------------------------------------
//           Create the stream
// --------------------------------------------------------------------------------


BinInputStream* XSECTXFMInputSource::makeStream() const {

	XSECBinTXFMInputStream * ret;

	// Have to do direct due to strange issues with MSVC++ and DEBUG_NEW
	ret = new XSECBinTXFMInputStream(mp_chain, m_deleteWhenDone);

	return ret;

}

