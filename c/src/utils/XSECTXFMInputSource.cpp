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
 * XSECTXFMInputSource := Transfomer InputSource for Xerces Parser.
 *
 * $Id$
 *
 *					 
 */


#include "XSECTXFMInputSource.hpp"
#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/utils/XSECBinTXFMInputStream.hpp>
#include <xsec/framework/XSECError.hpp>

#include <xercesc/util/BinInputStream.hpp>

XERCES_CPP_NAMESPACE_USE

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

