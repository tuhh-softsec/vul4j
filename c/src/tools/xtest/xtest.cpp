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
 * xtest := basic test application to run through a series of tests of
 *			the XSEC library.
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xsec/framework/XSECDefs.hpp> 

#include <cassert>

#include <memory.h>
#include <iostream>
#include <stdlib.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLUniDefs.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/framework/XMLFormatter.hpp>

#include <xercesc/dom/DOM.hpp>
#include <xercesc/util/XMLException.hpp>

#include <xsec/transformers/TXFMOutputFile.hpp>
#include <xsec/dsig/DSIGTransformXPath.hpp>
#include <xsec/dsig/DSIGTransformC14n.hpp>

// XALAN

#ifndef XSEC_NO_XALAN

#include <XPath/XPathEvaluator.hpp>
#include <XalanTransformer/XalanTransformer.hpp>

XALAN_USING_XALAN(XPathEvaluator)
XALAN_USING_XALAN(XalanTransformer)

#endif

// XSEC

#include <xsec/utils/XSECPlatformUtils.hpp>
#include <xsec/framework/XSECProvider.hpp>
#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/dsig/DSIGReference.hpp>
#include <xsec/framework/XSECError.hpp>
#include <xsec/dsig/DSIGSignature.hpp>
#include <xsec/utils/XSECNameSpaceExpander.hpp>
#include <xsec/utils/XSECDOMUtils.hpp>
#include <xsec/enc/XSECCryptoException.hpp>
#include <xsec/dsig/DSIGKeyInfoX509.hpp>
#include <xsec/dsig/DSIGKeyInfoName.hpp>

#if defined (HAVE_OPENSSL)
#	include <xsec/enc/OpenSSL/OpenSSLCryptoKeyHMAC.hpp>
#endif
#if defined (HAVE_WINCAPI)
#	include <xsec/enc/WinCAPI/WinCAPICryptoKeyHMAC.hpp>
#endif

using std::ostream;
using std::cout;
using std::cerr;
using std::endl;
using std::flush;

/*
 * Because of all the characters, it's easiest to put the entire program
 * in the Xerces namespace
 */

XERCES_CPP_NAMESPACE_USE

// --------------------------------------------------------------------------------
//           Known "Good" Values
// --------------------------------------------------------------------------------

unsigned char createdDocRefs [8][20] = {
	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a,
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, },
	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a, 
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
	{ 0x5a, 0x14, 0x9c, 0x5a, 0x40, 0x34, 0x51, 0x4f, 0xef, 0x1d, 
	  0x85, 0x44, 0xc7, 0x2a, 0xd3, 0xd2, 0x2, 0xed, 0x67, 0xb4, },
	{ 0x88, 0xd1, 0x65, 0xed, 0x2a, 0xe7, 0xc0, 0xbd, 0xea, 0x3e, 
	  0xe6, 0xf3, 0xd4, 0x8c, 0xf7, 0xdd, 0xc8, 0x85, 0xa9, 0x6d, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
	{ 0x52, 0x74, 0xc3, 0xe4, 0xc5, 0xf7, 0x20, 0xb0, 0xd9, 0x52, 
	  0xdb, 0xb3, 0xee, 0x46, 0x66, 0x8f, 0xe1, 0xb6, 0x30, 0x9d, },
  	{ 0x51, 0x3c, 0xb5, 0xdf, 0xb9, 0x1e, 0x9d, 0xaf, 0xd4, 0x4a, 
	  0x95, 0x79, 0xf1, 0xd6, 0x54, 0xe, 0xb0, 0xb0, 0x29, 0xe3, }

};


// --------------------------------------------------------------------------------
//           Much code taken from the DOMPrint Xerces example
// --------------------------------------------------------------------------------

static XMLFormatter*            gFormatter             = 0;
static XMLCh*                   gEncodingName          = 0;
static XMLFormatter::UnRepFlags gUnRepFlags            = XMLFormatter::UnRep_CharRef;




static const XMLCh  gEndElement[] = { chOpenAngle, chForwardSlash, chNull };
static const XMLCh  gEndPI[] = { chQuestion, chCloseAngle, chNull};
static const XMLCh  gStartPI[] = { chOpenAngle, chQuestion, chNull };
static const XMLCh  gXMLDecl1[] =
{
        chOpenAngle, chQuestion, chLatin_x, chLatin_m, chLatin_l
    ,   chSpace, chLatin_v, chLatin_e, chLatin_r, chLatin_s, chLatin_i
    ,   chLatin_o, chLatin_n, chEqual, chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl2[] =
{
        chDoubleQuote, chSpace, chLatin_e, chLatin_n, chLatin_c
    ,   chLatin_o, chLatin_d, chLatin_i, chLatin_n, chLatin_g, chEqual
    ,   chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl3[] =
{
        chDoubleQuote, chSpace, chLatin_s, chLatin_t, chLatin_a
    ,   chLatin_n, chLatin_d, chLatin_a, chLatin_l, chLatin_o
    ,   chLatin_n, chLatin_e, chEqual, chDoubleQuote, chNull
};
static const XMLCh  gXMLDecl4[] =
{
        chDoubleQuote, chQuestion, chCloseAngle
    ,   chLF, chNull
};

static const XMLCh  gStartCDATA[] =
{
        chOpenAngle, chBang, chOpenSquare, chLatin_C, chLatin_D,
        chLatin_A, chLatin_T, chLatin_A, chOpenSquare, chNull
};

static const XMLCh  gEndCDATA[] =
{
    chCloseSquare, chCloseSquare, chCloseAngle, chNull
};
static const XMLCh  gStartComment[] =
{
    chOpenAngle, chBang, chDash, chDash, chNull
};

static const XMLCh  gEndComment[] =
{
    chDash, chDash, chCloseAngle, chNull
};

static const XMLCh  gStartDoctype[] =
{
    chOpenAngle, chBang, chLatin_D, chLatin_O, chLatin_C, chLatin_T,
    chLatin_Y, chLatin_P, chLatin_E, chSpace, chNull
};
static const XMLCh  gPublic[] =
{
    chLatin_P, chLatin_U, chLatin_B, chLatin_L, chLatin_I,
    chLatin_C, chSpace, chDoubleQuote, chNull
};
static const XMLCh  gSystem[] =
{
    chLatin_S, chLatin_Y, chLatin_S, chLatin_T, chLatin_E,
    chLatin_M, chSpace, chDoubleQuote, chNull
};
static const XMLCh  gStartEntity[] =
{
    chOpenAngle, chBang, chLatin_E, chLatin_N, chLatin_T, chLatin_I,
    chLatin_T, chLatin_Y, chSpace, chNull
};
static const XMLCh  gNotation[] =
{
    chLatin_N, chLatin_D, chLatin_A, chLatin_T, chLatin_A,
    chSpace, chDoubleQuote, chNull
};



// ---------------------------------------------------------------------------
//  Local classes
// ---------------------------------------------------------------------------

class DOMPrintFormatTarget : public XMLFormatTarget
{
public:
    DOMPrintFormatTarget()  {};
    ~DOMPrintFormatTarget() {};

    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const   unsigned int    count,
                            XMLFormatter * const formatter)
    {
        // Surprisingly, Solaris was the only platform on which
        // required the char* cast to print out the string correctly.
        // Without the cast, it was printing the pointer value in hex.
        // Quite annoying, considering every other platform printed
        // the string with the explicit cast to char* below.
        cout.write((char *) toWrite, (int) count);
    };

private:
    // -----------------------------------------------------------------------
    //  Unimplemented methods.
    // -----------------------------------------------------------------------
    DOMPrintFormatTarget(const DOMPrintFormatTarget& other);
    void operator=(const DOMPrintFormatTarget& rhs);
};


// ---------------------------------------------------------------------------
//  ostream << DOMNode
//
//  Stream out a DOM node, and, recursively, all of its children. This
//  function is the heart of writing a DOM tree out as XML source. Give it
//  a document node and it will do the whole thing.
// ---------------------------------------------------------------------------
ostream& operator<<(ostream& target, DOMNode* toWrite)
{
    // Get the name and value out for convenience
    const XMLCh*   nodeName = toWrite->getNodeName();
    const XMLCh*   nodeValue = toWrite->getNodeValue();
    unsigned long lent = XMLString::stringLen(nodeValue);

    switch (toWrite->getNodeType())
    {
        case DOMNode::TEXT_NODE:
        {
            gFormatter->formatBuf(nodeValue,
                                  lent, XMLFormatter::CharEscapes);
            break;
        }


        case DOMNode::PROCESSING_INSTRUCTION_NODE :
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartPI  << nodeName;
            if (lent > 0)
            {
                *gFormatter << chSpace << nodeValue;
            }
            *gFormatter << XMLFormatter::NoEscapes << gEndPI;
            break;
        }


        case DOMNode::DOCUMENT_NODE :
        {

            DOMNode *child = toWrite->getFirstChild();
            while( child != 0)
            {
                target << child;
                // add linefeed in requested output encoding
                *gFormatter << chLF;
                target << flush;
                child = child->getNextSibling();
            }
            break;
        }


        case DOMNode::ELEMENT_NODE :
        {
            // The name has to be representable without any escapes
            *gFormatter  << XMLFormatter::NoEscapes
                         << chOpenAngle << nodeName;

            // Output the element start tag.

            // Output any attributes on this element
            DOMNamedNodeMap *attributes = toWrite->getAttributes();
            int attrCount = attributes->getLength();
            for (int i = 0; i < attrCount; i++)
            {
                DOMNode  *attribute = attributes->item(i);

                //
                //  Again the name has to be completely representable. But the
                //  attribute can have refs and requires the attribute style
                //  escaping.
                //
                *gFormatter  << XMLFormatter::NoEscapes
                             << chSpace << attribute->getNodeName()
                             << chEqual << chDoubleQuote
                             << XMLFormatter::AttrEscapes
                             << attribute->getNodeValue()
                             << XMLFormatter::NoEscapes
                             << chDoubleQuote;
            }

            //
            //  Test for the presence of children, which includes both
            //  text content and nested elements.
            //
            DOMNode *child = toWrite->getFirstChild();
            if (child != 0)
            {
                // There are children. Close start-tag, and output children.
                // No escapes are legal here
                *gFormatter << XMLFormatter::NoEscapes << chCloseAngle;

                while( child != 0)
                {
                    target << child;
                    child = child->getNextSibling();
                }

                //
                // Done with children.  Output the end tag.
                //
                *gFormatter << XMLFormatter::NoEscapes << gEndElement
                            << nodeName << chCloseAngle;
            }
            else
            {
                //
                //  There were no children. Output the short form close of
                //  the element start tag, making it an empty-element tag.
                //
                *gFormatter << XMLFormatter::NoEscapes << chForwardSlash << chCloseAngle;
            }
            break;
        }


        case DOMNode::ENTITY_REFERENCE_NODE:
            {
                //DOMNode *child;
#if 0
                for (child = toWrite.getFirstChild();
                child != 0;
                child = child.getNextSibling())
                {
                    target << child;
                }
#else
                //
                // Instead of printing the refernece tree
                // we'd output the actual text as it appeared in the xml file.
                // This would be the case when -e option was chosen
                //
                    *gFormatter << XMLFormatter::NoEscapes << chAmpersand
                        << nodeName << chSemiColon;
#endif
                break;
            }


        case DOMNode::CDATA_SECTION_NODE:
            {
            *gFormatter << XMLFormatter::NoEscapes << gStartCDATA
                        << nodeValue << gEndCDATA;
            break;
        }


        case DOMNode::COMMENT_NODE:
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartComment
                        << nodeValue << gEndComment;
            break;
        }


        case DOMNode::DOCUMENT_TYPE_NODE:
        {
            DOMDocumentType *doctype = (DOMDocumentType *)toWrite;;

            *gFormatter << XMLFormatter::NoEscapes  << gStartDoctype
                        << nodeName;

            const XMLCh* id = doctype->getPublicId();
            if (id != 0)
            {
                *gFormatter << XMLFormatter::NoEscapes << chSpace << gPublic
                    << id << chDoubleQuote;
                id = doctype->getSystemId();
                if (id != 0)
                {
                    *gFormatter << XMLFormatter::NoEscapes << chSpace
                       << chDoubleQuote << id << chDoubleQuote;
                }
            }
            else
            {
                id = doctype->getSystemId();
                if (id != 0)
                {
                    *gFormatter << XMLFormatter::NoEscapes << chSpace << gSystem
                        << id << chDoubleQuote;
                }
            }

            id = doctype->getInternalSubset();
            if (id !=0)
                *gFormatter << XMLFormatter::NoEscapes << chOpenSquare
                            << id << chCloseSquare;

            *gFormatter << XMLFormatter::NoEscapes << chCloseAngle;
            break;
        }


        case DOMNode::ENTITY_NODE:
        {
            *gFormatter << XMLFormatter::NoEscapes << gStartEntity
                        << nodeName;

            const XMLCh * id = ((DOMEntity *)toWrite)->getPublicId();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gPublic
                            << id << chDoubleQuote;

            id = ((DOMEntity *)toWrite)->getSystemId();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gSystem
                            << id << chDoubleQuote;

            id = ((DOMEntity *)toWrite)->getNotationName();
            if (id != 0)
                *gFormatter << XMLFormatter::NoEscapes << gNotation
                            << id << chDoubleQuote;

            *gFormatter << XMLFormatter::NoEscapes << chCloseAngle << chLF;

            break;
        }

/*
        case DOMNode::NOTATION_NODE:
        {
            const XMLCh *  str;

            *gFormatter << gXMLDecl1 << ((DOMXMLDecl *)toWrite)->getVersion();

            *gFormatter << gXMLDecl2 << gEncodingName;

            str = ((DOMXMLDecl *)toWrite)->getStandalone();
            if (str != 0)
                *gFormatter << gXMLDecl3 << str;

            *gFormatter << gXMLDecl4;

            break;
        }

*/
        default:
            cerr << "Unrecognized node type = "
                 << (long)toWrite->getNodeType() << endl;
    }
    return target;
}



// ---------------------------------------------------------------------------
//  ostream << DOMString
//
//  Stream out a DOM string. Doing this requires that we first transcode
//  to char * form in the default code page for the system
// ---------------------------------------------------------------------------
/*ostream& operator<< (ostream& target, const DOMString& s)
{
    char *p = s.transcode();
    target << p;
    delete [] p;
    return target;
}


XMLFormatter& operator<< (XMLFormatter& strm, const DOMString& s)
{
    unsigned int lent = s.length();

	if (lent <= 0)
		return strm;

    XMLCh*  buf = new XMLCh[lent + 1];
    XMLString::copyNString(buf, s.rawBuffer(), lent);
    buf[lent] = 0;
    strm << buf;
    delete [] buf;
    return strm;
}*/

// --------------------------------------------------------------------------------
//           End of outputter
// --------------------------------------------------------------------------------

class DOMMemFormatTarget : public XMLFormatTarget
{
public:
    
	unsigned char * buffer;		// Buffer to write to

	DOMMemFormatTarget()  {};
    ~DOMMemFormatTarget() {};

	void setBuffer (unsigned char * toSet) {buffer = toSet;};


    // -----------------------------------------------------------------------
    //  Implementations of the format target interface
    // -----------------------------------------------------------------------

    void writeChars(const   XMLByte* const  toWrite,
                    const   unsigned int    count,
                            XMLFormatter * const formatter)
    {
        // Surprisingly, Solaris was the only platform on which
        // required the char* cast to print out the string correctly.
        // Without the cast, it was printing the pointer value in hex.
        // Quite annoying, considering every other platform printed
        // the string with the explicit cast to char* below.
        memcpy(buffer, (char *) toWrite, (int) count);
		buffer[count] = '\0';
    };

private:
    // -----------------------------------------------------------------------
    //  Unimplemented methods.
    // -----------------------------------------------------------------------
    DOMMemFormatTarget(const DOMMemFormatTarget& other);
    void operator=(const DOMMemFormatTarget& rhs);

	
};

// ---------------------------------------------------------------------------
//  ostream << DOMString
//
//  Stream out a DOM string. Doing this requires that we first transcode
//  to char * form in the default code page for the system
// ---------------------------------------------------------------------------


DOMPrintFormatTarget *DOMtarget;
DOMMemFormatTarget *MEMtarget;
XMLFormatter *formatter, *MEMformatter;
unsigned char *charBuffer;



int attributeNodeCount(DOMElement *d) {

	int ret;

	ret = d->getAttributes()->getLength();

	DOMNode *c;

	c = d->getFirstChild();

	while (c != NULL) {

		if (c->getNodeType() == DOMNode::ELEMENT_NODE)
			ret += attributeNodeCount((DOMElement *) c);

		c = c->getNextSibling();

	}

	return ret;

}

void outputHex(unsigned char * buf, int len) {

	cout << std::ios::hex;
	for (int i = 0; i < len; ++i) {
		cout << "0x" << (unsigned int) buf[i] << ", ";
	}
	cout << std::ios::dec << endl;

}


int main(int argc, char **argv) {


	// First initialise the XML system

	try {

		XMLPlatformUtils::Initialize();
#ifndef XSEC_NO_XALAN
		XPathEvaluator::initialize();
		XalanTransformer::initialize();
#endif
		XSECPlatformUtils::Initialise();

	}
	catch (const XMLException &e) {

		cerr << "Error during initialisation of Xerces" << endl;
		cerr << "Error Message = : "
		     << e.getMessage() << endl;

	}

	cerr << "Creating a known doc and signing (HMAC-SHA1)" << endl;
	
	// Create a blank Document

    //DOMImplementation impl;

	XMLCh tempStr[100];
    XMLString::transcode("Core", tempStr, 99);    
    DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(tempStr);
        

    DOMDocument *doc = impl->createDocument(
                0,                    // root element namespace URI.
                MAKE_UNICODE_STRING("ADoc"),            // root element name
                NULL);// DOMDocumentType());  // document type object (DTD).

    DOMElement *rootElem = doc->getDocumentElement();
	rootElem->setAttributeNS(DSIGConstants::s_unicodeStrURIXMLNS, 
		MAKE_UNICODE_STRING("xmlns:foo"), MAKE_UNICODE_STRING("http://www.foo.org"));

    DOMElement  * prodElem = doc->createElement(MAKE_UNICODE_STRING("product"));
    rootElem->appendChild(prodElem);

    DOMText    * prodDataVal = doc->createTextNode(MAKE_UNICODE_STRING("XMLSecurityC"));
    prodElem->appendChild(prodDataVal);

    DOMElement  *catElem = doc->createElement(MAKE_UNICODE_STRING("category"));
    rootElem->appendChild(catElem);
    catElem->setAttribute(MAKE_UNICODE_STRING("idea"), MAKE_UNICODE_STRING("great"));

    DOMText    *catDataVal = doc->createTextNode(MAKE_UNICODE_STRING("XML Security Tools"));
    catElem->appendChild(catDataVal);

	XSECProvider prov;
	DSIGSignature *sig;
	DSIGReference *ref[10];
	DOMElement *sigNode;
	int refCount;

	try {
		
		sig = prov.newSignature();
		sig->setDSIGNSPrefix(MAKE_UNICODE_STRING("ds"));

		sigNode = sig->createBlankSignature(doc, CANON_C14N_COM, SIGNATURE_HMAC, HASH_SHA1);
		rootElem->appendChild(doc->createTextNode(DSIGConstants::s_unicodeStrNL));
		rootElem->insertBefore(doc->createComment(MAKE_UNICODE_STRING(" a comment ")), prodElem);
		rootElem->appendChild(sigNode);
		rootElem->insertBefore(doc->createTextNode(DSIGConstants::s_unicodeStrNL), prodElem);
		ref[0] = sig->createReference(MAKE_UNICODE_STRING(""));
		ref[0]->appendEnvelopedSignatureTransform();

		ref[1] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[1]->appendEnvelopedSignatureTransform();
		ref[1]->appendCanonicalizationTransform(CANON_C14N_NOC);

		ref[2] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[2]->appendEnvelopedSignatureTransform();
		ref[2]->appendCanonicalizationTransform(CANON_C14N_COM);

		ref[3] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[3]->appendEnvelopedSignatureTransform();
		ref[3]->appendCanonicalizationTransform(CANON_C14NE_NOC);

		ref[4] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[4]->appendEnvelopedSignatureTransform();
		ref[4]->appendCanonicalizationTransform(CANON_C14NE_COM);

		ref[5] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[5]->appendEnvelopedSignatureTransform();
		DSIGTransformC14n * ce = ref[5]->appendCanonicalizationTransform(CANON_C14NE_COM);
		ce->addInclusiveNamespace("foo");

		sig->setECNSPrefix(MAKE_UNICODE_STRING("ec"));
		ref[6] = sig->createReference(MAKE_UNICODE_STRING("#xpointer(/)"));
		ref[6]->appendEnvelopedSignatureTransform();
		ce = ref[6]->appendCanonicalizationTransform(CANON_C14NE_COM);
		ce->addInclusiveNamespace("foo");

#ifdef XSEC_NO_XALAN

		cerr << "WARNING : No testing of XPath being performed as Xalan not present" << endl;
		refCount = 7;

#else

		ref[7] = sig->createReference(MAKE_UNICODE_STRING(""));
		/*		ref[5]->appendXPathTransform("ancestor-or-self::dsig:Signature", 
				"xmlns:dsig=http://www.w3.org/2000/09/xmldsig#"); */

		DSIGTransformXPath * x = ref[7]->appendXPathTransform("count(ancestor-or-self::dsig:Signature | \
here()/ancestor::dsig:Signature[1]) > \
count(ancestor-or-self::dsig:Signature)");
		x->setNamespace("dsig", "http://www.w3.org/2000/09/xmldsig#");

		refCount = 8;

#endif
	
		sig->appendKeyName(MAKE_UNICODE_STRING("The secret key is \"secret\""));

#if defined (HAVE_OPENSSL)
		OpenSSLCryptoKeyHMAC * hmacKey = new OpenSSLCryptoKeyHMAC();
		cerr << "Using OpenSSL as the cryptography provider" << endl;
#else
#	if defined (HAVE_WINCAPI)
		WinCAPICryptoKeyHMAC * hmacKey = new WinCAPICryptoKeyHMAC();
		cerr << "Using Windows Crypto API as the cryptography provider" << endl;
#	endif
#endif
		hmacKey->setKey((unsigned char *) "secret", strlen("secret"));
		sig->setSigningKey(hmacKey);
		sig->sign();

		cerr << "Doc signed OK - Checking values against Known Good" << endl;

		//

		unsigned char buf[128];
		int len;

		for (int i = 0; i < refCount; ++i) {

			cerr << "Calculating hash for reference " << i << " ... ";

			len = (int) ref[i]->calculateHash(buf, 128);

			cerr << " Done\nChecking -> ";

			if (len != 20) {
				cerr << "Bad (Length = " << len << ")" << endl;
				exit (1);
			}

			for (int j = 0; j < 20; ++j) {

				if (buf[j] != createdDocRefs[i][j]) {
					cerr << "Bad at location " << j << endl;
					exit (1);
				}
			
			}
			cerr << "Good.\n";

		}

		cerr << "Running \"verifySignatureOnly()\" on calculated signature ... ";
		if (sig->verifySignatureOnly()) {
			cerr << "OK" << endl;
		}
		else {
			cerr << "Failed" << endl;
			char * e = XMLString::transcode(sig->getErrMsgs());
			cout << e << endl;
			delete [] e;
			exit(1);
		}

		cerr << "Setting incorrect key in Signature object" << endl;
#if defined (HAVE_OPENSSL)
		hmacKey = new OpenSSLCryptoKeyHMAC();
#else
#	if defined (HAVE_WINCAPI)
		hmacKey = new WinCAPICryptoKeyHMAC();
#	endif
#endif
		hmacKey->setKey((unsigned char *) "badsecret", strlen("badsecret"));
		sig->setSigningKey(hmacKey);

		cerr << "Running \"verifySignatureOnly()\" on calculated signature ... ";
		if (!sig->verifySignatureOnly()) {
			cerr << "OK (Signature bad)" << endl;
		}
		else {
			cerr << "Failed (signature OK but should be bad)" << endl;
			exit(1);
		}

		// Don't need the signature now the DOM structure is in place
		prov.releaseSignature(sig);
	}

	catch (XSECException &e)
	{
		cerr << "An error occured during signature processing\n   Message: ";
		char * ce = XMLString::transcode(e.getMsg());
		cerr << ce << endl;
		delete ce;
		exit(1);
		
	}	
	catch (XSECCryptoException &e)
	{
		cerr << "A cryptographic error occured during signature processing\n   Message: "
		<< e.getMsg() << endl;
		exit(1);
	}


	// Print out the doc

	DOMPrintFormatTarget* formatTarget = new DOMPrintFormatTarget();
	
    const XMLCh* encNameStr = XMLString::transcode("UTF-8");
    DOMNode *aNode = doc->getFirstChild();
    if (aNode->getNodeType() == DOMNode::ENTITY_NODE)
    {
        const XMLCh* aStr = ((DOMEntity *)aNode)->getEncoding();
        if (!strEquals(aStr, ""))
        {
            encNameStr = aStr;
        }
    }
    unsigned int lent = XMLString::stringLen(encNameStr);
    gEncodingName = new XMLCh[lent + 1];
    XMLString::copyNString(gEncodingName, encNameStr, lent);
    gEncodingName[lent] = 0;

	
	
	gFormatter = new XMLFormatter("UTF-8", formatTarget,
                                          XMLFormatter::NoEscapes, gUnRepFlags);


	cout << doc;

	cerr << "All tests passed" << endl;

	return 0;

}
