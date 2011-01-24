
/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.samples.keys;

import java.io.File;
import java.security.PublicKey;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.keys.storage.implementations.CertsInFilesystemDirectoryResolver;
import org.apache.xml.security.samples.SampleUtils;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;

/**
 *
 * @author $Author$
 */
public class RetrievePublicKeys {

    /**
     * Method main
     *
     * @param unused
     */
    public static void main(String unused[]) {
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        String merlinsDir =
            "data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/";
        String ourDir =
            "data/org/apache/xml/security/temp/key/";
        String filenames[] = { merlinsDir +
                               /* 0 */ "signature-keyname.xml",
                               merlinsDir +
                               /* 1 */ "signature-retrievalmethod-rawx509crt.xml",
                               merlinsDir +
                               /* 2 */ "signature-x509-crt-crl.xml",
                               merlinsDir +
                               /* 3 */ "signature-x509-crt.xml",
                               merlinsDir +
                               /* 4 */ "signature-x509-is.xml",
                               merlinsDir +
                               /* 5 */ "signature-x509-ski.xml",
                               merlinsDir +
                               /* 6 */ "signature-x509-sn.xml",
                               ourDir +
                               /* 7 */ "signature-retrievalmethod-x509data.xml",
                               ourDir +
                               /* 8 */ "signature-retrievalmethod-dsavalue.xml",
                               ourDir +
                               /* 9 */ "retrieval-from-same-doc.xml"
        };

        int start = 0;
        int end = filenames.length;
        for (int filetoverify = start; filetoverify < end; filetoverify++) {
            String filename = filenames[filetoverify];

            System.out.println("#########################################################");
            System.out.println("Try to verify " + filename);

            try {
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                org.w3c.dom.Document doc =
                    db.parse(new java.io.FileInputStream(filename));
                Element nscontext = SampleUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);

                Element kiElement = 
                    (Element) XPathAPI.selectSingleNode(doc, "//ds:KeyInfo[1]", nscontext);
                KeyInfo ki = new KeyInfo(kiElement, (new File(filename)).toURI().toURL().toString());
                StorageResolver storageResolver = 
                    new StorageResolver(new CertsInFilesystemDirectoryResolver(merlinsDir + "certs"));

                ki.addStorageResolver(storageResolver);

                PublicKey pk = ki.getPublicKey();

                System.out.println("PublicKey" + ((pk != null) ? " found:" : " not found!!!"));

                if (pk != null) {
                    System.out.println("   Format: " + pk.getFormat());
                    System.out.println("   Algorithm: " + pk.getAlgorithm());
                }

                System.out.println("   Key: " + pk);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
