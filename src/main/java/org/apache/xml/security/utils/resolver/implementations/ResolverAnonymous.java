/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

package org.apache.xml.security.utils.resolver.implementations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;

/**
 * @author $Author$
 */
public class ResolverAnonymous extends ResourceResolverSpi {

    private XMLSignatureInput input = null;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }

    /**
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ResolverAnonymous(String filename) throws FileNotFoundException, IOException {
        this.input = new XMLSignatureInput(new FileInputStream(filename));
    }

    /**
     * @param is
     */
    public ResolverAnonymous(InputStream is) {
        this.input = new XMLSignatureInput(is);
    }

    /** @inheritDoc */
    public XMLSignatureInput engineResolve(Attr uri, String BaseURI) {
        return this.input;
    }

    /**    
     * @inheritDoc
     */
    public boolean engineCanResolve(Attr uri, String BaseURI) {
        if (uri == null) {
            return true;
        }
        return false;
    }

    /** @inheritDoc */
    public String[] engineGetPropertyKeys() {
        return new String[0];
    }
}
