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

