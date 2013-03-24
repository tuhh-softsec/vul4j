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
package org.apache.xml.security.test.dom.utils.resolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

/**
 * This class helps us home users to resolve http URIs without a network
 * connection.
 * <BR />
 * The OfflineResolver is only needed for Unit testing. This is not needed for
 * a production environment. It's a very simple cache/proxy to HTTP space
 * so that I can do unit testing with http:// URIs even if I'm not connected
 * to the internet.
 *
 */
public class OfflineResolver extends ResourceResolverSpi {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(OfflineResolver.class);
    
    /** Field _uriMap */
    static Map<String, String> _uriMap = null;

    /** Field _mimeMap */
    static Map<String, String> _mimeMap = null;
    
    static {
        org.apache.xml.security.Init.init();

        _uriMap = new HashMap<String, String>();
        _mimeMap = new HashMap<String, String>();

        String basedir = 
            System.getProperty("basedir") == null ? "./": System.getProperty("basedir") + "/";

        OfflineResolver.register(
            "http://www.w3.org/TR/xml-stylesheet",
            basedir + "src/test/resources/org/w3c/www/TR/xml-stylesheet.html", "text/html"
        );
        OfflineResolver.register(
            "http://www.w3.org/TR/2000/REC-xml-20001006",
            basedir + "src/test/resources/org/w3c/www/TR/2000/REC-xml-20001006", "text/xml"
        );
        OfflineResolver.register(
            "http://www.nue.et-inf.uni-siegen.de/index.html",
            basedir + "src/test/resources/org/apache/xml/security/temp/nuehomepage", "text/html"
        );
        OfflineResolver.register(
            "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/id2.xml",
            basedir + "src/test/resources/org/apache/xml/security/temp/id2.xml", "text/xml"
        );
        OfflineResolver.register(
            "http://xmldsig.pothole.com/xml-stylesheet.txt",
             basedir + "src/test/resources/com/pothole/xmldsig/xml-stylesheet.txt", "text/xml"
        );
        OfflineResolver.register(
            "http://www.w3.org/Signature/2002/04/xml-stylesheet.b64",
            basedir 
            + "src/test/resources/ie/baltimore/merlin-examples/merlin-xmldsig-twenty-three/xml-stylesheet.b64", 
            "text/plain"
        );
    }

    /**
     * Method engineResolve
     *
     * @param context
     *
     * @throws ResourceResolverException
     */
    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {
        try {
            String URI = context.uriToResolve;

            if (OfflineResolver._uriMap.containsKey(URI)) {
                String newURI = OfflineResolver._uriMap.get(URI);

                log.debug("Mapped " + URI + " to " + newURI);

                InputStream is = new FileInputStream(newURI);

                log.debug("Available bytes = " + is.available());

                XMLSignatureInput result = new XMLSignatureInput(is);

                result.setSourceURI(URI);
                result.setMIMEType(OfflineResolver._mimeMap.get(URI));

                return result;
            } else {
                Object exArgs[] = {"The URI " + URI + " is not configured for offline work" };

                throw new ResourceResolverException(
                    "generic.EmptyMessage", exArgs, context.uriToResolve, context.baseUri
                );
            }
        } catch (IOException ex) {
            throw new ResourceResolverException(
                "generic.EmptyMessage", ex, context.uriToResolve, context.baseUri
            );
        }
    }

    /**
     * We resolve http URIs <I>without</I> fragment...
     *
     * @param context
     */
    @Override
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        String uriNodeValue = context.uriToResolve;
        if (uriNodeValue.equals("") || uriNodeValue.startsWith("#")) {
            return false;
        }

        URI uriNew = null;
        try {
            uriNew = getNewURI(context.uriToResolve, context.baseUri);
            if (uriNew.getScheme().equals("http")) {
                log.debug("I state that I can resolve " + uriNew.toString());
                return true;
            }

        } catch (URISyntaxException ex) {
            //
        }

        return false;
    }

    /**
     * Method register
     *
     * @param URI
     * @param filename
     * @param MIME
     */
    private static void register(String URI, String filename, String MIME) {
        OfflineResolver._uriMap.put(URI, filename);
        OfflineResolver._mimeMap.put(URI, MIME);
    }
    
    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri = null;
        if (baseURI == null || "".equals(baseURI)) {
            newUri = new URI(uri);
        } else {
            newUri = new URI(baseURI).resolve(uri);
        }
        
        // if the URI contains a fragment, ignore it
        if (newUri.getFragment() != null) {
            URI uriNewNoFrag = 
                new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
            return uriNewNoFrag;
        }
        return newUri;
    }

}
