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
 * c14n := tool to dump a XML file to the console after canonacalising it thru
 *			c14n
 *
 * Author(s): Berin Lautenbach
 *
 * $ID$
 *
 * $LOG$
 *
 */

//XSEC includes



//#include <Include/PlatformDefinitions.hpp>
//#include <cassert>

#include <memory.h>
#include <string.h>
#include <iostream.h>
#include <stdlib.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/dom/DOM.hpp>
#include <xercesc/parsers/XercesDOMParser.hpp>
#include <xercesc/util/XMLException.hpp>

// XSEC

#include <xsec/canon/XSECC14n20010315.hpp>
#include <xsec/utils/XSECNameSpaceExpander.hpp>
#include <xsec/utils/XSECPlatformUtils.hpp>

XSEC_USING_XERCES(XMLException);
XSEC_USING_XERCES(XMLString);
XSEC_USING_XERCES(XercesDOMParser);
XSEC_USING_XERCES(XMLPlatformUtils);
XSEC_USING_XERCES(DOMException);

void printUsage(void) {

	cerr << "\nUsage: c14n [-n] <input file name>\n";
	cerr << "       -n = No comments\n\n";

}

int main(int argc, char **argv) {

	bool printComments = true;		// By default print comments

	// Check arguments

	if (argc < 2) {

		printUsage();
		exit (1);
	}

	if (argc > 2) {

		for (int i = 1; i < argc - 1; ++i) {

			if (!strcmp(argv[i], "-n") || !strcmp(argv[i], "-N"))
				printComments = false;
			else {
				cerr << "Unknown option %s\n\n";
				printUsage();
				exit (1);
			}
		}
	}

				

	// Initialise the XML system

	try {

		XMLPlatformUtils::Initialize();
		XSECPlatformUtils::Initialise();

	}
	catch (const XMLException &e) {

		cerr << "Error during initialisation of Xerces" << endl;
		cerr << "Error Message = : "
		     << e.getMessage() << endl;

	}

	// Create and set up the parser

	XercesDOMParser * parser = new XercesDOMParser;
	parser->setDoNamespaces(false);
	parser->setValidationScheme(XercesDOMParser::Val_Never);
	parser->setDoSchema(false);
	parser->setCreateEntityReferenceNodes(false);

	// Now parse out file

	bool errorsOccured = false;
	int errorCount = 0;
    try
    {
    	parser->parse(argv[argc-1]);
        errorCount = parser->getErrorCount();
        if (errorCount > 0)
            errorsOccured = true;
    }

    catch (const XMLException& e)
    {
        cerr << "An error occured during parsing\n   Message: "
             << e.getMessage() << endl;
        errorsOccured = true;
    }


    catch (const DOMException& e)
    {
       cerr << "A DOM error occured during parsing\n   DOMException code: "
             << e.code << endl;
        errorsOccured = true;
    }

	if (errorsOccured) {

		cout << "Errors during parse" << endl;
		exit (1);

	}

	/*

		Now that we have the parsed file, get the DOM document and start looking at it

	*/
	
	DOMNode *doc;		// The document that we parsed

	doc = parser->getDocument();
	DOMDocument *theDOM = parser->getDocument();

	// Expand name spaces so that c14n will work correctly
	XSECNameSpaceExpander nse(theDOM);
	nse.expandNameSpaces();

	// Creat the canonicalizer

	XSECC14n20010315 canon(theDOM);
	canon.setCommentsProcessing(printComments);

	// canon.XPathSelectNodes("(/descendant-or-self::node() | /descendant-or-self::node()/attribute::* | /descendant-or-self::node()/namespace::*)[ self::ietf:e1 or (parent::ietf:e1 and not(self::text() or self::e2)) or count (id(\"E3\") | ancestor-or-self::node()) = count (ancestor-or-self::node())]");

	char buffer[512];
	int res = canon.outputBuffer((unsigned char *) buffer, 128);


	while (res != 0) {
		buffer[res] = '\0';
		cout << buffer;
		res = canon.outputBuffer((unsigned char *) buffer, 128);
	}

	cout << endl;
	
	return 0;
}
