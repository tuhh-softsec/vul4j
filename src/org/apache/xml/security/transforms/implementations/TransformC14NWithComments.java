
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



import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.transforms.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


/**
 * Implements the <CODE>http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 */
public class TransformC14NWithComments extends TransformSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(TransformC14NWithComments.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_C14N_WITH_COMMENTS;

   /** Field _c14n */
   Canonicalizer _c14n = null;

   //J-
   public boolean wantsOctetStream ()   { return true; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return true; }
   public boolean returnsNodeSet ()     { return false; }
   //J+

   /**
    * Constructor TransformC14NWithComments
    *
    * @throws InvalidCanonicalizerException
    */
   public TransformC14NWithComments() throws InvalidCanonicalizerException {

      this._c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

      this._c14n.setXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
   }

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
    * @param input
    * @return
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws ParserConfigurationException
    * @throws SAXException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws IOException, CanonicalizationException,
                  InvalidCanonicalizerException, ParserConfigurationException,
                  SAXException {

      try {
         input.setCanonicalizerURI(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

         byte[] result = null;

         if (input.isOctetStream()) {
            result = this._c14n.canonicalize(input.getBytes());
         } else {
            cat.debug("Length: " + input.getNodeSet().getLength()
                      + " nodes input");

            result = this._c14n.canonicalize(input.getNodeSet());
         }

         ByteArrayInputStream bais = new ByteArrayInputStream(result);

         return new XMLSignatureInput(bais);
      } catch (ParserConfigurationException ex) {
         Object[] exArgs = { ex.getMessage() };
         CanonicalizationException cex = new CanonicalizationException(
            "c14n.Canonicalizer.ParserConfigurationException", exArgs);

         cat.debug(cex.toString());

         throw cex;
      } catch (SAXException ex) {
         Object[] exArgs = { ex.toString() };
         CanonicalizationException cex =
            new CanonicalizationException("c14n.Canonicalizer.SAXException",
                                          exArgs);

         cat.debug(cex.toString());

         throw cex;
      }
   }
}
