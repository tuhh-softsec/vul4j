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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
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
 */
public class LocalHttpCacheURIDereferencer implements URIDereferencer {

    private final URIDereferencer ud;
    private static final String FS = System.getProperty("file.separator");
    private static final String BASEDIR = System.getProperty("basedir");
    private final Map<String, File> uriMap;

    public LocalHttpCacheURIDereferencer() {
        ud = XMLSignatureFactory.getInstance().getURIDereferencer();
        String base = BASEDIR == null ? "./": BASEDIR;
        File dir = new File(base + FS + "src/test/resources" + FS + "javax" +
            FS + "xml" + FS + "crypto" + FS + "dsig");
        uriMap = new HashMap<String, File>();
        uriMap.put("http://www.w3.org/TR/xml-stylesheet",
                   new File(dir, "xml-stylesheet"));
        uriMap.put("http://www.w3.org/Signature/2002/04/xml-stylesheet.b64",
                   new File(dir, "xml-stylesheet.b64"));
        uriMap.put("http://www.ietf.org/rfc/rfc3161.txt",
                   new File(dir, "rfc3161.txt"));
    }
 
    public Data dereference(URIReference uriReference, XMLCryptoContext context)
        throws URIReferenceException {
        String uri = uriReference.getURI();
        if (uriMap.containsKey(uri)) {
            try {
                FileInputStream fis = new FileInputStream(uriMap.get(uri));
                return new OctetStreamData(
                    fis, uriReference.getURI(), uriReference.getType());
            } catch (Exception e) { throw new URIReferenceException(e); }
        }

        // fallback on builtin deref
        return ud.dereference(uriReference, context);
    }
}
