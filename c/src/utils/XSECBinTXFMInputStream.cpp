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
 * XSECBinTXFMInputStream := Implement the BinInputStream around Transforms.
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *					 
 */


#include <xsec/utils/XSECBinTXFMInputStream.hpp>
#include <xsec/transformers/TXFMBase.hpp>
#include <xsec/transformers/TXFMChain.hpp>
#include <xsec/framework/XSECError.hpp>

// ---------------------------------------------------------------------------
//  Constructors/Destructors
// ---------------------------------------------------------------------------


XSECBinTXFMInputStream::XSECBinTXFMInputStream(TXFMChain * lst, bool deleteWhenDone) :
mp_txfm(lst->getLastTxfm()),
mp_chain(lst),
m_deleteWhenDone(deleteWhenDone),
m_deleted(false),
m_done(false),
m_currentIndex(0) {

	if (mp_txfm->getOutputType() != TXFMBase::BYTE_STREAM) {

		throw XSECException(XSECException::TransformError,
			"Cannot wrapper a non BYTE_STREAM TXFM with XSECBinTXFMInputStream");

	}

}

XSECBinTXFMInputStream::~XSECBinTXFMInputStream() {

	if (m_deleteWhenDone == true && m_deleted == false) {

		delete mp_chain;
		m_deleted = true;

	}

}

// ---------------------------------------------------------------------------
//  Stream methods
// ---------------------------------------------------------------------------

void XSECBinTXFMInputStream::reset(void) {}

unsigned int XSECBinTXFMInputStream::curPos() const {

	return m_currentIndex;

}

unsigned int XSECBinTXFMInputStream::readBytes(XMLByte* const  toFill, 
					   const unsigned int maxToRead) {

	if (m_done == true)
		return 0;

	unsigned int bytesRead;

	bytesRead = mp_txfm->readBytes(toFill, maxToRead);

	if (bytesRead == 0) {

		if (m_deleteWhenDone) {

			delete mp_chain;
			mp_txfm = 0;
			mp_chain = 0;
			m_deleted = true;

		}

		m_done = true;

	}

	m_currentIndex += bytesRead;

	return bytesRead;

}

