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
 * XSECC14n20010315 := Canonicaliser object to process XML document in line with
 *					     RFC 3076
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

#ifndef XSECC14n20010315_INCLUDE
#define XSECC14n20010315_INCLUDE

//XSEC includes
#include <xsec/framework/XSECDefs.hpp>
#include <xsec/utils/XSECSafeBuffer.hpp>
#include <xsec/utils/XSECXPathNodeList.hpp>
#include <xsec/canon/XSECCanon.hpp>

#include <xercesc/framework/XMLFormatter.hpp>

// General includes
#include <memory.h>
#include <vector>

XSEC_USING_XERCES(XMLFormatter);
XSEC_USING_XERCES(XMLFormatTarget);

// --------------------------------------------------------------------------------
//           Object definitions needed for formatting Xerces objects
// --------------------------------------------------------------------------------


class c14nFormatTarget : public XMLFormatTarget
{
public:
    
	safeBuffer * buffer;		// Buffer to write to

	c14nFormatTarget()  {};
    ~c14nFormatTarget() {};

	void setBuffer (safeBuffer * toSet) {buffer = toSet;};


    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const   unsigned int    count,
                            XMLFormatter * const formatter)
    {
        // Surprisingly, Solaris was the only platform on which
        // required the char* cast to print out the string correctly.
        // Without the cast, it was pinting the pointer value in hex.
        // Quite annoying, considering every other platform printed
        // the string with the explicit cast to char* below.
        buffer->sbMemcpyIn((char *) toWrite, (int) count);
		(*buffer)[count] = '\0';
		buffer->setBufferType(safeBuffer::BUFFER_CHAR);
    };

private:
    // -----------------------------------------------------------------------
    //  Unimplemented methods.
    // -----------------------------------------------------------------------
    c14nFormatTarget(const c14nFormatTarget& other);
    void operator=(const c14nFormatTarget& rhs);

	
};

// --------------------------------------------------------------------------------
//           Simple structure for holding a list of nodes
// --------------------------------------------------------------------------------

// NOTE: We don't use NamedNodeMap or DOMNodeList as we are unsure what might happen
// to them in the future.  Also, to add items we would have to delve into the inards
// of Xerces (and use the "...impl" classes).  Such an approach might not be supported
// in the future.

struct XSECNodeListElt {

	DOMNode		*element;			// Element referred to
	safeBuffer	sortString;			// The string that is used to sort the nodes

	XSECNodeListElt		*next,
						*last;		// For the list

};

// Used for the sorting function

#define XMLNS_PREFIX		"a"
#define ATTRIBUTE_PREFIX	"b"

// --------------------------------------------------------------------------------
//           XSECC14n20010315 Object definition
// --------------------------------------------------------------------------------

class CANON_EXPORT XSECC14n20010315 : public XSECCanon {

#if defined(XALAN_NO_NAMESPACES)
	typedef vector<char *>				CharListVectorType;
#else
	typedef std::vector<char *>			CharListVectorType;
#endif

#if defined(XALAN_SIZE_T_IN_NAMESPACE_STD)
	typedef std::size_t		size_type;
#else
	typedef size_t			size_type;
#endif


public:

	// Constructors
	XSECC14n20010315();
	XSECC14n20010315(DOMDocument *newDoc);
	XSECC14n20010315(DOMDocument *newDoc, DOMNode *newStartNode);
	virtual ~XSECC14n20010315();

	// XPath processor

	int XPathSelectNodes(const char * XPathExpr);
	void setXPathMap(const XSECXPathNodeList & map);

	// Comments processing
	void setCommentsProcessing(bool onoff);
	bool getCommentsProcessing(void);

	// Exclusive processing
	void setExclusive(void);
	void setExclusive(char * xmlnsList);

protected:

	// Implementation of virtual function
	int processNextNode();

	// Test whether a name space is in the non-exclusive list
	bool inNonExclNSList(safeBuffer &ns);

private:

	void XSECC14n20010315::init();
	bool checkRenderNameSpaceNode(DOMNode *e, DOMNode *a);

	// For formatting the buffers
	c14nFormatTarget *c14ntarget;
	XMLFormatter *formatter;
	safeBuffer formatBuffer;

	// For holding state whilst walking the DOM tree
	XSECNodeListElt	* mp_attributes,				// Start of list
					* mp_currentAttribute,			// Where we currently are in list
					* mp_firstNonNsAttribute;		// First NON XMLNS element in list
	DOMNode			* mp_attributeParent;			// To return up the tree
	bool m_returnedFromChild;						// Did we get to this node from below?
	DOMNode			* mp_firstElementNode;			// The root element of the document
	bool			m_firstElementProcessed;		// Has the first node been handled?
	unsigned char	* mp_charBuffer;

	// For XPath evaluation
	bool			  m_XPathSelection;				// Are we doing an XPath?
	XSECXPathNodeList m_XPathMap;					// The elements in the XPath

	// For comment processing
	bool			m_processComments;				// Whether comments are in or out (in by default)

	// For exclusive canonicalisation
	CharListVectorType		m_exclNSList;
	bool					m_exclusive;
	bool					m_exclusiveDefault;



};


#endif /* XSECC14n20010315_INCLUDE */


