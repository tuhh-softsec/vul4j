
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
package org.apache.xml.security.transforms.implementations;



import org.w3c.dom.*;
import org.apache.xpath.XPathContext;


/**
 * {@link FuncHereContext} extends {@link XPathContext} for supplying context
 * for the <CODE>here()</CODE> function. The here() function needs to know
 * <I>where</I> in an XML instance the XPath text string appeared. This can be#
 * in {@link Text}, {@link Attr}ibutes and {@ProcessingInstrinction} nodes. The
 * correct node must be supplied to the constructor of {@link FuncHereContext}.
 * The supplied Node MUST contain the XPath which is to be executed.
 *
 * <PRE>
 * From: Scott_Boag\@lotus.com
 * To: Christian Geuer-Pollmann <maillist\@nue.et-inf.uni-siegen.de>
 * CC: xalan-dev@xml.apache.org
 * Subject: Re: Cleanup of XPathContext & definition of XSLTContext
 * Date: Tue, 21 Aug 2001 18:36:24 -0400
 *
 * > My point is to say to get this baby to run, the XPath must have a
 * > possibility to retrieve the information where itself occured in a
 * > document.
 *
 * It sounds to me like you have to derive an XMLSigContext from the
 * XPathContext?
 *
 * > and supplied the Node which contains the xpath string as "owner". Question:
 * > Is this the correct use of the owner object? It works, but I don't know
 * > whether this is correct from the xalan-philosophy...
 *
 * Philosophically it's fine.  The owner is the TransformerImpl if XPath is
 * running under XSLT.  If it is not running under XSLT, it can be whatever
 * you want.
 *
 * -scott
 * </PRE>
 *
 * @author $Author$
 * @see org.apache.xml.security.transforms.implementations.FuncHere
 * @see org.apache.xml.security.utils.XPathFuncHereAPI;
 * @see <A HREF="http://www.w3.org/Signature/Drafts/xmldsig-core/Overview.html#function-here">XML Signature - The here() function</A>
 */
public class FuncHereContext extends XPathContext {

   /**
    * This constuctor is disabled because if we use the here() function we
    * <I>always</I> need to kxnow in which node the XPath occured.
    */
   private FuncHereContext() {}

   /**
    * Constructor FuncHereContext
    *
    * @param owner
    */
   public FuncHereContext(Node owner) {
      super((Object) owner);
   }

   static {
      org.apache.xml.security.Init.init();
   }
}
