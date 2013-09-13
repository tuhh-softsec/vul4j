/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.io.*;
import java.util.*;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.*;

/**
 * This is a class which performs xml signature validation upon request
 *
 * @author Sean Mullan
 * @author Valerie Peng
 */
public class SignatureValidator {

    private File dir;

    public SignatureValidator(File base) {
        dir = base;
    }

    public boolean validate(String fn, KeySelector ks) throws Exception {
        return validate(fn, ks, null);
    }

    public DOMValidateContext getValidateContext(String fn, KeySelector ks)
        throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).parse(new File(dir, fn));
        Element sigElement = getSignatureElement(doc);
        if (sigElement == null) {
            throw new Exception("Couldn't find signature Element");
        }
        DOMValidateContext vc = new DOMValidateContext(ks, sigElement);
        vc.setBaseURI(dir.toURI().toString());
        return vc;
    }

    public boolean validate(String fn, KeySelector ks, URIDereferencer ud)
        throws Exception {

        DOMValidateContext vc = getValidateContext(fn, ks);
        if (ud != null) {
            vc.setURIDereferencer(ud);
        }

        return validate(vc);
    }

    public boolean validate(DOMValidateContext vc) throws Exception {

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        XMLSignature signature = factory.unmarshalXMLSignature(vc);
        boolean coreValidity = signature.validate(vc);
    
        // Check core validation status
        if (coreValidity == false) {
            // check the validation status of each Reference
            Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
            while (i.hasNext()) {
                Reference reference = (Reference) i.next();
                reference.validate(vc);
            }
        }
        return coreValidity;
    }

    public static Element getSignatureElement(Document doc) {
        NodeIterator ni = ((DocumentTraversal)doc).createNodeIterator(
            doc.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, false);
        
        for (Node n = ni.nextNode(); n != null; n = ni.nextNode() ) {
            if ("Signature".equals(n.getLocalName())) {
                return (Element) n;
            }
        }
        return null;
    }
}
