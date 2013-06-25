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
/*
 * $Id$
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;

/**
 * DOM-based abstract implementation of Transform.
 *
 * @author Sean Mullan
 */
public class DOMTransform extends BaseStructure implements Transform {

    protected TransformService spi;

    /**
     * Creates a <code>DOMTransform</code>.
     *
     * @param spi the TransformService
     */
    public DOMTransform(TransformService spi) {
        this.spi = spi;
    }

    /**
     * Creates a <code>DOMTransform</code> from an element. This constructor
     * invokes the abstract {@link #unmarshalParams unmarshalParams} method to
     * unmarshal any algorithm-specific input parameters.
     *
     * @param transElem a Transform element
     */
    public DOMTransform(Element transElem, XMLCryptoContext context,
                        Provider provider)
        throws MarshalException
    {
        String algorithm = DOMUtils.getAttributeValue(transElem, "Algorithm");
        if (provider == null) {
            try {
                spi = TransformService.getInstance(algorithm, "DOM");
            } catch (NoSuchAlgorithmException e1) {
                throw new MarshalException(e1);
            }
        } else {
            try {
                spi = TransformService.getInstance(algorithm, "DOM", provider);
            } catch (NoSuchAlgorithmException nsae) {
                try {
                    spi = TransformService.getInstance(algorithm, "DOM");
                } catch (NoSuchAlgorithmException e2) {
                    throw new MarshalException(e2);
                }
            }
        }
        try {
            spi.init(new javax.xml.crypto.dom.DOMStructure(transElem), context);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    @Override
    public final AlgorithmParameterSpec getParameterSpec() {
        return spi.getParameterSpec();
    }

    @Override
    public final String getAlgorithm() {
        return spi.getAlgorithm();
    }

    /**
     * This method invokes the abstract {@link #marshalParams marshalParams} 
     * method to marshal any algorithm-specific parameters.
     */
    public void marshal(XmlWriter xwriter, String dsPrefix, XMLCryptoContext context)
        throws MarshalException
    {
        String parentLocalName = xwriter.getCurrentLocalName();
        String localName = parentLocalName.equals("Transforms") ? "Transform" : "CanonicalizationMethod";
        xwriter.writeStartElement(dsPrefix, localName, XMLSignature.XMLNS);
        xwriter.writeAttribute("", "", "Algorithm", getAlgorithm());

        javax.xml.crypto.XMLStructure xmlStruct = xwriter.getCurrentNodeAsStructure();
        spi.marshalParams(xmlStruct, context);

        xwriter.writeEndElement(); // "Transforms" or "CanonicalizationMethod"
    }

    /**
     * Transforms the specified data using the underlying transform algorithm.
     *
     * @param data the data to be transformed
     * @param sc the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @return the transformed data
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     * @throws XMLSignatureException if an unexpected error occurs while
     *    executing the transform
     */
    @Override
    public Data transform(Data data, XMLCryptoContext xc)
        throws TransformException
    {
        return spi.transform(data, xc);
    }

    /**
     * Transforms the specified data using the underlying transform algorithm.
     *
     * @param data the data to be transformed
     * @param sc the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @param os the <code>OutputStream</code> that should be used to write
     *    the transformed data to
     * @return the transformed data
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     * @throws XMLSignatureException if an unexpected error occurs while
     *    executing the transform
     */
    @Override
    public Data transform(Data data, XMLCryptoContext xc, OutputStream os)
        throws TransformException
    {
        return spi.transform(data, xc, os);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Transform)) {
            return false;
        }
        Transform otransform = (Transform)o;

        return (getAlgorithm().equals(otransform.getAlgorithm()) &&
                DOMUtils.paramsEqual(getParameterSpec(),
                                     otransform.getParameterSpec()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getAlgorithm().hashCode();
        AlgorithmParameterSpec spec = getParameterSpec();
        if (spec != null) {
            result = 31 * result + spec.hashCode();
        }

        return result;
    }
    
    /**
     * Transforms the specified data using the underlying transform algorithm.
     * This method invokes the {@link #marshal marshal} method and passes it
     * the specified <code>DOMSignContext</code> before transforming the data.
     *
     * @param data the data to be transformed
     * @param sc the <code>XMLCryptoContext</code> containing
     *    additional context (may be <code>null</code> if not applicable)
     * @param context the marshalling context
     * @return the transformed data
     * @throws MarshalException if an exception occurs while marshalling
     * @throws NullPointerException if <code>data</code> or <code>context</code> 
     *    is <code>null</code>
     * @throws XMLSignatureException if an unexpected error occurs while
     *    executing the transform
     */
    Data transform(Data data, XMLCryptoContext xc, DOMSignContext context)
        throws MarshalException, TransformException
    {
        Node parent = context.getParent();
        XmlWriter xwriter = new XmlWriterToTree(Marshaller.getMarshallers(), parent);
        marshal(xwriter, DOMUtils.getSignaturePrefix(context), context);
        return transform(data, xc);
    }
}
