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
 * XSECBinTXFMInputStream := Implement the BinInputStream around Transforms.
 *					 
 *
 */


#ifndef XSECBINTXFMINPUTSTREAM_INCLUDE
#define XSECBINTXFMINPUTSTREAM_INCLUDE

#include <xsec/framework/XSECDefs.hpp>
#include <xercesc/util/BinInputStream.hpp>

class TXFMBase;

/**
 * @defgroup interfaces Classes that interface applications to XML Security C.
 * These classes and files provide interfaces to allow the XSEC library to link
 * to other libraries or to allow other libraries/applications to interface
 * to XSEC.
 * @{
 */

/**
 * @brief BinInputSource wrapper for a TXFMList.
 *
 * This class provides a wrapper for a TXFMList.  It can be used to either provide
 * a nice interface to applications wishing to read the BYTESTREAM output of a 
 * TXFM chain, or, as it is derived from BinInputStream, provide an input to the
 * Xerces Parser.
 *
 */


class DSIG_EXPORT XSECBinTXFMInputStream : public BinInputStream
{

public :

	/** @name Constructors and Destructors */
	//@{

	/**
	 * \brief Construct around an existing transform list
	 *
	 * @param lst The final TXFM element in the input chain.
	 * @param deleteWhenDone Flag to instruct the class to delete the chain when
	 * done.  By default set to true.
	 */

    XSECBinTXFMInputStream(TXFMBase * lst, bool deleteWhenDone = true);

	/**
	 * \brief Destructor
	 *
	 * Delete the object.  If deleteWhenDone was set during construction, will
	 * delete the chain if it has not already been done.
	 */

    virtual ~XSECBinTXFMInputStream();

	//@}

	/** @name Stream management methods */
	//@{

	/** 
	 * \brief Reset the stream.
	 *
	 * @note At this time simply deletes the TXFM chain - many TXFMs do not
	 * have an ability to reset.
	 */

    void reset();

	//@}

	/** @name Interface */
	//@{

	/**
	 * \brief Current position in stream.
	 *
	 * @returns Bytes already returned.
	 */

    virtual unsigned int curPos() const;

	/**
	 * \brief Retrieve the required number of bytes and return
	 *
	 * Retrieve up to the requested number of bytes.  Does not always
	 * retrieve as much as requested, but will always retrieve something
	 * until completed.
	 *
	 * @note When complete, will return 0 and delete the TXFM chain if
	 * requested to do so in the constructor.
	 * @param toFill The buffer that will be read into.
	 * @param maxToRead Maximum number of bytes to return
	 * @returns The number of bytes read or 0 if complete.
	 */

    virtual unsigned int readBytes(XMLByte* const  toFill, 
		const unsigned int maxToRead);


private :

	TXFMBase					* mp_txfm;			// End point of list
	bool						m_deleteWhenDone;	// Do we delete?
	bool						m_deleted;			// Have we deleted?
	bool						m_done;				// Are we done?
	unsigned int				m_currentIndex;		// How much read?

};



#endif
