
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



import java.io.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * Class TransformXSLT
 *
 * Implements the <CODE>http://www.w3.org/TR/1999/REC-xslt-19991116</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformXSLT extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XSLT;
   //J-
   public static final String XSLTSpecNS              = "http://www.w3.org/1999/XSL/Transform";
   public static final String defaultXSLTSpecNSprefix = "xslt";
   public static final String XSLTSTYLESHEET          = "stylesheet";

   public boolean wantsOctetStream ()   { return false; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return true; }
   //J+

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this.implementedTransformURI;
   }

   /**
    * Method enginePerformTransform
    *
    * @param input the input for this transform
    * @return the result of this Transform
    * @throws CanonicalizationException
    * @throws IOException
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws IOException, CanonicalizationException,
                  TransformationException {

      try {
         Element transformElement = this._transformObject.getElement();
         Document doc = transformElement.getOwnerDocument();
         Element nscontext = XMLUtils.createDSctx(doc, "xslt", XSLTSpecNS);

         Element _xsltElement =
            (Element) XPathAPI.selectSingleNode(transformElement,
                                                "./xslt:stylesheet", nscontext);

         if (_xsltElement == null) {
            Object exArgs[] = { "xslt:stylesheet", "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         TransformerFactory tFactory = TransformerFactory.newInstance();

         /*
          * This transform requires an octet stream as input. If the actual
          * input is an XPath node-set, then the signature application should
          * attempt to convert it to octets (apply Canonical XML]) as described
          * in the Reference Processing Model (section 4.3.3.2).
          */
         Source xmlSource =
            new StreamSource(new ByteArrayInputStream(input.getBytes()));
         Source stylesheet;

         /*
          * This complicated transformation of the stylesheet itself is necessary
          * because of the need to get the pure style sheet. If we simply say
          * Source stylesheet = new DOMSource(this._xsltElement);
          * whereby this._xsltElement is not the rootElement of the Document,
          * this causes problems;
          * so we convert the stylesheet to byte[] and use this as input stream
          */
         {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource((Node) _xsltElement);
            StreamResult result = new StreamResult(os);

            transformer.transform(source, result);

            stylesheet =
               new StreamSource(new ByteArrayInputStream(os.toByteArray()));
         }

         Transformer transformer = tFactory.newTransformer(stylesheet);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         StreamResult outputTarget = new StreamResult(baos);

         transformer.transform(xmlSource, outputTarget);

         return new XMLSignatureInput(baos.toByteArray());
      } catch (InvalidCanonicalizerException ex) {
         Object exArgs[] = { ex.getMessage() };

         throw new TransformationException("generic.EmptyMessage", exArgs, ex);
      } catch (XMLSecurityException ex) {
         Object exArgs[] = { ex.getMessage() };

         throw new TransformationException("generic.EmptyMessage", exArgs, ex);
      } catch (TransformerConfigurationException ex) {
         Object exArgs[] = { ex.getMessage() };

         throw new TransformationException("generic.EmptyMessage", exArgs, ex);
      } catch (TransformerException ex) {
         Object exArgs[] = { ex.getMessage() };

         throw new TransformationException("generic.EmptyMessage", exArgs, ex);
      }
   }
}
