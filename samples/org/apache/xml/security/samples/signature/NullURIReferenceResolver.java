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
package org.apache.xml.security.samples.signature;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;

/**
 * This is a sample ResourceResolver who demonstrated how References without
 * URI attribuet could be handled.
 *
 * @author $Author$
 */
public class NullURIReferenceResolver extends ResourceResolverSpi {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(NullURIReferenceResolver.class);

    /** Field _data[] */
    byte _data[] = null;

    /** Field _data2[][] */
    byte _data2[][] = null;

    /** Field _count */
    int _count = -1;

    /**
     * Constructor NullURIReferenceResolver
     *
     * @param data
     */
    public NullURIReferenceResolver(byte[] data) {
        _data = data;
        _count = -1;
    }

    /**
     * Constructor NullURIReferenceResolver
     *
     * @param data
     */
    public NullURIReferenceResolver(byte[][] data) {
        _data2 = data;
        _count = 0;
    }

    /**
     * Method engineResolve
     *
     * @param uri
     * @param BaseURI
     *
     * @throws ResourceResolverException
     */
    public XMLSignatureInput engineResolve(Attr uri, String BaseURI)
        throws ResourceResolverException {

        XMLSignatureInput result = null;

        if ((this._data != null) && (this._count == -1)) {

            // we always return the same stuff;
            result = new XMLSignatureInput(this._data);

            result.setSourceURI("memory://null");
            result.setMIMEType("text/txt");
        } else if ((this._data == null) && (this._count != -1)) {
            if (this._count < this._data2.length) {
                result = new XMLSignatureInput(this._data2[this._count]);

                result.setSourceURI("memory://" + this._count);

                this._count++;

                result.setMIMEType("text/txt");
            } else {
                String errMsg = "You did not supply enough data!!! There are only "
                    + (this._data2.length) + " byte[] arrays";
                Object exArgs[] = { errMsg };

                throw new ResourceResolverException("empty", exArgs, uri, BaseURI);
            }
        } else {
            Object exArgs[] = { "You did not supply data !!!" };

            throw new ResourceResolverException("empty", exArgs, uri, BaseURI);
        }

        return result;
    }

    /**
     * Method engineCanResolve
     *
     * @param uri
     * @param BaseURI
     *
     */
    public boolean engineCanResolve(Attr uri, String BaseURI) {

        if (uri == null) {
            if ((this._data != null) && (this._count == -1)) {
                return true;
            } else if ((this._data == null) && (this._count != -1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method engineGetPropertyKeys
     *
     *
     */
    public String[] engineGetPropertyKeys() {
        return null;
    }
}
