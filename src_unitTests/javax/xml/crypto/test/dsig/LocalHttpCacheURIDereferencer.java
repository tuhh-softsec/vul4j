/*
 * Copyright 2010 The Apache Software Foundation.
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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignatureFactory;

/**
 * This URIDereferencer implementation retrieves http references used in
 * test signatures from local disk in order to avoid network requests. 
 * Currently only one file is cached but more can be added.
 */
public class LocalHttpCacheURIDereferencer implements URIDereferencer {

    private final URIDereferencer ud;
    private final File f;
    private final static String FS = System.getProperty("file.separator");
    private final static String BASEDIR = System.getProperty("basedir");

    public LocalHttpCacheURIDereferencer() {
        ud = XMLSignatureFactory.getInstance().getURIDereferencer();
        String base = BASEDIR == null ? "./": BASEDIR;
        File dir = new File(base + FS + "data" + FS + "javax" +
            FS + "xml" + FS + "crypto" + FS + "dsig");
        f = new File(dir, "xml-stylesheet");
    }
 
    public Data dereference(URIReference uriReference, XMLCryptoContext context)
        throws URIReferenceException {
        String uri = uriReference.getURI();
        if (uri.equals("http://www.w3.org/TR/xml-stylesheet")) {
            try {
                FileInputStream fis = new FileInputStream(f);
                return new OctetStreamData(
                    fis, uriReference.getURI(), uriReference.getType());
            } catch (Exception e) { throw new URIReferenceException(e); }
        }

        // fallback on builtin deref
        return ud.dereference(uriReference, context);
    }
}
