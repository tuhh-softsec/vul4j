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
 * IOStreamOutputer.cpp := Used by all samples that need to output a DOM document
 *							to an IOStream
 * 
 * NOTE: Much of this code came from Apache examples in the Xerces distribution
 *
 * Author(s): Berin Lautenbach
 *
 * $Id$
 *
 */

#include <xercesc/dom/DOM.hpp>
#include <xercesc/framework/XMLFormatter.hpp>
#include <iostream>

using std::cerr;
using std::cout;
using std::endl;
using std::ostream;
using std::flush;

#if !defined(XERCES_CPP_NAMESPACE_QUALIFIER)
#    define XERCES_CPP_NAMESPACE_QUALIFIER
#endif

// --------------------------------------------------------------------------------
//           Much code taken from the DOMPrint Xerces example
// --------------------------------------------------------------------------------

ostream& operator<<(ostream& target, XERCES_CPP_NAMESPACE_QUALIFIER DOMNode * toWrite);
//ostream& operator<< (ostream& target, const DOMString& s);
//XMLFormatter& operator<< (XMLFormatter& strm, const DOMString& s);

void docSetup(XERCES_CPP_NAMESPACE_QUALIFIER DOMDocument *doc);

