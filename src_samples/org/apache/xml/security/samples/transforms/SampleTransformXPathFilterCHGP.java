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
package org.apache.xml.security.samples.transforms;



import java.io.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.transforms.params.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.crypto.*;


/**
 * Sample for the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class SampleTransformXPathFilterCHGP {

   /**
    * Method main
    *
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception {

      org.apache.xml.security.Init.init();

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();

      // String inputDoc = "<A><U><B><S><S><S></S><S><S></S><S></S></S><S></S></S><S><S></S></S></S><C><U><U><U><U></U><U><U></U><U></U></U><U></U></U><U><U></U></U></U></U></C></B><D><U></U></D><U><E><S><S><S></S><S><S></S><S></S></S><S></S></S><S><S></S></S></S></E><U><F><G><H/></G></F></U></U></U></A>";
      // String inputDoc = "<A><U><B><S></S><C><U></U></C></B><D><U></U></D><U><E><S></S></E><U><F><G><H/></G></F></U></U></U></A>";
      String inputDoc =
         "<A xmlns:foo=\"http://foo.bar/\">\n<U>\n<U>\n<U>\n<U>\n<U>\n<B foo:attr=\"attr\">\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<C>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n<U>\n<U>\n<U>\n<U/>\n<U>\n<U/>\n<U/>\n</U>\n<U/>\n</U>\n<U>\n<U/>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</C>\n</B>\n<D>\n<U/>\n</D>\n<U>\n<E>\n<S>\n<S>\n<S/>\n<S>\n<S/>\n<S/>\n</S>\n<S/>\n</S>\n<S>\n<S/>\n</S>\n</S>\n</E>\n<U>\n<F>\n<G>\n<H/>\n<G>\n<H/>\n</G>\n</G>\n</F>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</U>\n</A>\n";
      Document doc;

      doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));

      SampleTransformXPathFilterCHGP.outApache(doc);

      doc = db.parse(new ByteArrayInputStream(inputDoc.getBytes()));

      SampleTransformXPathFilterCHGP.outXFilter2(doc);
   }

   /**
    * Method outApache
    *
    * @param doc
    * @throws Exception
    */
   static void outApache(Document doc) throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);
      String includeButSearchStr = "//B | //E | //F | //H";
      String excludeButSearchStr = "//G";

      // String excludeStr = "//C | //D | //ds:Signature";
      String excludeStr =
         "//C | //D | here()/ancestor::ds:Signature[1] | //@x:attr";
      boolean includeSlashPolicy = XPathFilterCHGPContainer.ExcludeSlash;
      XPathFilterCHGPContainer xpathContainer =
         XPathFilterCHGPContainer.getInstance(doc, includeSlashPolicy,
                                              includeButSearchStr,
                                              excludeButSearchStr, excludeStr);

      xpathContainer.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);
      xpathContainer.setXPathNamespaceContext("x", "http://foo.bar/");
      transforms.addTransform(Transforms.TRANSFORM_XPATHFILTERCHGP,
                              xpathContainer.getElement());
      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);

      System.out.println(
         "-------------------------------------------------------------");
      System.out.println("The signed octets (output of the transforms) are ");
      System.out.println(
         "-------------------------------------------------------------");
      System.out
         .println(new String(sig.getSignedInfo().item(0).getTransformsOutput()
            .getBytes()));
      System.out.println(
         "-------------------------------------------------------------");
      System.out.println("The document is ");
      System.out.println(
         "-------------------------------------------------------------");
      System.out.println(new String(full));
      System.out.println(
         "-------------------------------------------------------------");

      Element sE =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature sigVer = new XMLSignature(sE, null);
      boolean verify =
         sigVer.checkSignatureValue(sigVer
            .createSecretKey("secret".getBytes()));

      System.out.println("verify: " + verify);
   }

   /**
    * Method outXFilter2
    *
    * @param doc
    * @throws Exception
    */
   static void outXFilter2(Document doc) throws Exception {

      XMLSignature sig = new XMLSignature(doc, null,
                                          XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

      doc.getDocumentElement().appendChild(sig.getElement());

      Transforms transforms = new Transforms(doc);

      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceIntersect(doc,
                                 "//E").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceUnion(doc,
                                 "//B").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceSubtract(doc,
                                 "//C").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceUnion(doc,
                                 "//F").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceSubtract(doc,
                                 "//G").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceUnion(doc,
                                 "//H").getElement());
      transforms.addTransform(Transforms.TRANSFORM_XPATH2FILTER,
                              XPath2FilterContainer.newInstanceSubtract(doc,
                                 "//@x:attr").getElement());
      transforms.setXPathNamespaceContext("xmlns:x", "http://foo.bar/");
      transforms
         .setXPathNamespaceContext(Transforms
            .getDefaultPrefix(Transforms.TRANSFORM_XPATH2FILTER), Transforms
            .TRANSFORM_XPATH2FILTER);
      sig.addDocument("", transforms);

      String secretKey = "secret";

      sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                  + "\" are used for signing ("
                                  + secretKey.length() + " octets)");
      sig.sign(sig.createSecretKey(secretKey.getBytes()));

      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte[] full = c14n.canonicalizeSubtree(doc);

      System.out.println(
         "-------------------------------------------------------------");
      System.out.println("The signed octets (output of the transforms) are ");
      System.out.println(
         "-------------------------------------------------------------");
      System.out
         .println(new String(sig.getSignedInfo().item(0).getTransformsOutput()
            .getBytes()));
      System.out.println(
         "-------------------------------------------------------------");
      System.out.println("The document is ");
      System.out.println(
         "-------------------------------------------------------------");
      System.out.println(new String(full));
      System.out.println(
         "-------------------------------------------------------------");

      Element sE =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature sigVer = new XMLSignature(sE, null);
      boolean verify =
         sigVer.checkSignatureValue(sigVer
            .createSecretKey("secret".getBytes()));

      System.out.println("verify: " + verify);
   }

   /**
    * Method check
    *
    * @param filename
    * @throws Exception
    */
   public static void check(String filename) throws Exception {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(new FileInputStream(filename));
      NodeList sigs = doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                                 Constants._TAG_SIGNATURE);
      XMLSignature sig =
         new XMLSignature((Element) sigs.item(0),
                          new File(filename).toURL().toString());
      boolean check =
         sig.checkSignatureValue(sig.createSecretKey("secret".getBytes()));

      System.out.println(
         "-------------------------------------------------------------");
      System.out.println("Verification of " + filename + ": " + check);
      System.out.println(
         "-------------------------------------------------------------");
      System.out
         .println(new String(sig.getSignedInfo().item(0).getTransformsOutput()
            .getBytes()));
      System.out.println(
         "-------------------------------------------------------------");
   }
}
