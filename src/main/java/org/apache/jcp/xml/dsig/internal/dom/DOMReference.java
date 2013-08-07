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
 * Portions copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * =========================================================================== 
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * $Id$
 */
package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dom.DOMURIReference;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.util.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.jcp.xml.dsig.internal.DigesterOutputStream;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;

/**
 * DOM-based implementation of Reference.
 *
 * @author Sean Mullan
 * @author Joyce Leung
 */
public final class DOMReference extends DOMStructure 
    implements Reference, DOMURIReference {
    
   /**
    * The maximum number of transforms per reference, if secure validation is enabled.
    */
   public static final int MAXIMUM_TRANSFORM_COUNT = 5;

   /**
    * Look up useC14N11 system property. If true, an explicit C14N11 transform
    * will be added if necessary when generating the signature. See section
    * 3.1.1 of http://www.w3.org/2007/xmlsec/Drafts/xmldsig-core/ for more info.
    *
    * If true, overrides the same property if set in the XMLSignContext.
    */
    private static boolean useC14N11 =
        AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.getBoolean
                    ("com.sun.org.apache.xml.internal.security.useC14N11");
            }
        });

    private static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(DOMReference.class);
    
    private final DigestMethod digestMethod;
    private final String id;
    private final List<Transform> transforms;
    private List<Transform> allTransforms;
    private final Data appliedTransformData;
    private Attr here;
    private final String uri;
    private final String type;
    private byte[] digestValue;
    private byte[] calcDigestValue;
    private Element refElem;
    private boolean digested = false;
    private boolean validated = false;
    private boolean validationStatus;
    private Data derefData;
    private InputStream dis;
    private MessageDigest md;
    private Provider provider;

    /**
     * Creates a <code>Reference</code> from the specified parameters.
     *
     * @param uri the URI (may be null)
     * @param type the type (may be null)
     * @param dm the digest method
     * @param transforms a list of {@link Transform}s. The list
     *    is defensively copied to protect against subsequent modification.
     *    May be <code>null</code> or empty.
     * @param id the reference ID (may be <code>null</code>)
     * @return a <code>Reference</code>
     * @throws NullPointerException if <code>dm</code> is <code>null</code>
     * @throws ClassCastException if any of the <code>transforms</code> are
     *    not of type <code>Transform</code>
     */
    public DOMReference(String uri, String type, DigestMethod dm,
                        List<? extends Transform> transforms, String id,
                        Provider provider)
    {
        this(uri, type, dm, null, null, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm,
                        List<? extends Transform> appliedTransforms,
                        Data result, List<? extends Transform> transforms,
                        String id, Provider provider)
    {
        this(uri, type, dm, appliedTransforms,
             result, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm,
                        List<? extends Transform> appliedTransforms,
                        Data result, List<? extends Transform> transforms,
                        String id, byte[] digestValue, Provider provider)
    {
        if (dm == null) {
            throw new NullPointerException("DigestMethod must be non-null");
        }
        if (appliedTransforms == null) {
            this.allTransforms = new ArrayList<Transform>();
        } else {
            this.allTransforms = new ArrayList<Transform>(appliedTransforms);
            for (int i = 0, size = this.allTransforms.size(); i < size; i++) {
                if (!(this.allTransforms.get(i) instanceof Transform)) {
                    throw new ClassCastException
                        ("appliedTransforms["+i+"] is not a valid type");
                }
            }
        }
        if (transforms == null) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = new ArrayList<Transform>(transforms);
            for (int i = 0, size = this.transforms.size(); i < size; i++) {
                if (!(this.transforms.get(i) instanceof Transform)) {
                    throw new ClassCastException
                        ("transforms["+i+"] is not a valid type");
                }
            }
            this.allTransforms.addAll(this.transforms);
        }
        this.digestMethod = dm;
        this.uri = uri;
        if (uri != null && !uri.equals("")) {
            try {
                new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        this.type = type;
        this.id = id;
        if (digestValue != null) {
            this.digestValue = digestValue.clone();
            this.digested = true;
        }
        this.appliedTransformData = result;
        this.provider = provider;
    }
 
    /**
     * Creates a <code>DOMReference</code> from an element.
     *
     * @param refElem a Reference element
     */
    public DOMReference(Element refElem, XMLCryptoContext context, 
                        Provider provider)
        throws MarshalException
    {
        boolean secVal = Utils.secureValidation(context);
        
        // unmarshal Transforms, if specified
        Element nextSibling = DOMUtils.getFirstChildElement(refElem);
        List<Transform> transforms = new ArrayList<Transform>(5);
        if (nextSibling.getLocalName().equals("Transforms")) {
            Element transformElem = DOMUtils.getFirstChildElement(nextSibling);
            
            int transformCount = 0;
            while (transformElem != null) {
                transforms.add
                    (new DOMTransform(transformElem, context, provider));
                transformElem = DOMUtils.getNextSiblingElement(transformElem);
                
                transformCount++;
                if (secVal && transformCount > MAXIMUM_TRANSFORM_COUNT) {
                    String error = "A maxiumum of " + MAXIMUM_TRANSFORM_COUNT + " " 
                        + "transforms per Reference are allowed with secure validation";
                    throw new MarshalException(error);
                }
            }
            nextSibling = DOMUtils.getNextSiblingElement(nextSibling);
        }

        // unmarshal DigestMethod
        Element dmElem = nextSibling;
        this.digestMethod = DOMDigestMethod.unmarshal(dmElem);
        String digestMethodAlgorithm = this.digestMethod.getAlgorithm();
        if (secVal 
            && MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5.equals(digestMethodAlgorithm)) {
            throw new MarshalException(
                "It is forbidden to use algorithm " + digestMethod + " when secure validation is enabled"
            );
        }

        // unmarshal DigestValue
        try {
            Element dvElem = DOMUtils.getNextSiblingElement(dmElem);
            this.digestValue = Base64.decode(dvElem);
        } catch (Base64DecodingException bde) {
            throw new MarshalException(bde);
        }

        // unmarshal attributes
        this.uri = DOMUtils.getAttributeValue(refElem, "URI");
        this.id = DOMUtils.getIdAttributeValue(refElem, "Id");

        this.type = DOMUtils.getAttributeValue(refElem, "Type");
        this.here = refElem.getAttributeNodeNS(null, "URI");
        this.refElem = refElem;
        this.transforms = transforms;
        this.allTransforms = transforms;
        this.appliedTransformData = null;
        this.provider = provider;
    }

    @Override
    public DigestMethod getDigestMethod() {
        return digestMethod;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<Transform> getTransforms() {
        return Collections.unmodifiableList(allTransforms);
    }

    @Override
    public byte[] getDigestValue() {
        return digestValue == null ? null : digestValue.clone();
    }

    @Override
    public byte[] getCalculatedDigestValue() {
        return calcDigestValue == null ? null 
                                        : calcDigestValue.clone();
    }

    @Override
    public void marshal(XmlWriter xwriter, String dsPrefix, XMLCryptoContext context)
        throws MarshalException
    {
        if (log.isDebugEnabled()) {
            log.debug("Marshalling Reference");
        }
        xwriter.writeStartElement(dsPrefix, "Reference", XMLSignature.XMLNS);
        XMLStructure refStruct = xwriter.getCurrentNodeAsStructure();
        refElem = (Element) ((javax.xml.crypto.dom.DOMStructure) refStruct).getNode();
        
        // set attributes
        xwriter.writeIdAttribute("", "", "Id", id);
        here = xwriter.writeAttribute("", "", "URI", uri);
        xwriter.writeAttribute("", "", "Type", type);

        // create and append Transforms element
        if (!allTransforms.isEmpty()) {
            xwriter.writeStartElement(dsPrefix, "Transforms", XMLSignature.XMLNS);
            for (Transform transform : allTransforms) {
                xwriter.marshalStructure(transform, dsPrefix, context);
            }
            xwriter.writeEndElement(); // "Transforms"
        }

        // create and append DigestMethod element
        DOMDigestMethod.marshal(xwriter, digestMethod, dsPrefix);

        // create and append DigestValue element
        if (log.isDebugEnabled()) {
            log.debug("Adding digestValueElem");
        }
        xwriter.writeStartElement(dsPrefix, "DigestValue", XMLSignature.XMLNS);
        if (digestValue != null) {
            xwriter.writeCharacters(Base64.encode(digestValue));
        }
        xwriter.writeEndElement(); // "DigestValue"
        xwriter.writeEndElement(); // "Reference"
    }

    public void digest(XMLSignContext signContext) 
        throws XMLSignatureException
    {
        Data data = null;
        if (appliedTransformData == null) {
            data = dereference(signContext);
        } else {
            data = appliedTransformData;
        }
        digestValue = transform(data, signContext);

        // insert digestValue into DigestValue element
        String encodedDV = Base64.encode(digestValue);
        if (log.isDebugEnabled()) {
            log.debug("Reference object uri = " + uri);
        }
        Element digestElem = DOMUtils.getLastChildElement(refElem);
        if (digestElem == null) {
            throw new XMLSignatureException("DigestValue element expected");
        }
        DOMUtils.removeAllChildren(digestElem);
        digestElem.appendChild
            (refElem.getOwnerDocument().createTextNode(encodedDV));

        digested = true;
        if (log.isDebugEnabled()) {
            log.debug("Reference digesting completed");
        }
    }

    @Override
    public boolean validate(XMLValidateContext validateContext)
        throws XMLSignatureException
    {
        if (validateContext == null) {
            throw new NullPointerException("validateContext cannot be null");
        }
        if (validated) {
            return validationStatus;
        }
        Data data = dereference(validateContext);
        calcDigestValue = transform(data, validateContext);

        if (log.isDebugEnabled()) {
            log.debug("Expected digest: " + Base64.encode(digestValue));
            log.debug("Actual digest: " + Base64.encode(calcDigestValue));
        }

        validationStatus = Arrays.equals(digestValue, calcDigestValue);
        validated = true;
        return validationStatus;
    }

    @Override
    public Data getDereferencedData() {
        return derefData;
    }

    @Override
    public InputStream getDigestInputStream() {
        return dis;
    }

    private Data dereference(XMLCryptoContext context) 
        throws XMLSignatureException
    {
        Data data = null;

        // use user-specified URIDereferencer if specified; otherwise use deflt
        URIDereferencer deref = context.getURIDereferencer();
        if (deref == null) {
            deref = DOMURIDereferencer.INSTANCE;
        }
        try {
            data = deref.dereference(this, context);
            if (log.isDebugEnabled()) {
                log.debug("URIDereferencer class name: " + deref.getClass().getName());
                log.debug("Data class name: " + data.getClass().getName());
            }
        } catch (URIReferenceException ure) {
            throw new XMLSignatureException(ure);
        }

        return data;
    }

    private byte[] transform(Data dereferencedData, 
                             XMLCryptoContext context)
        throws XMLSignatureException
    {
        if (md == null) {
            try {
                md = MessageDigest.getInstance
                    (((DOMDigestMethod)digestMethod).getMessageDigestAlgorithm());
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        md.reset();
        DigesterOutputStream dos;
        Boolean cache = (Boolean)
            context.getProperty("javax.xml.crypto.dsig.cacheReference");
        if (cache != null && cache) {
            this.derefData = copyDerefData(dereferencedData);
            dos = new DigesterOutputStream(md, true);
        } else {
            dos = new DigesterOutputStream(md);
        }
        OutputStream os = null;
        Data data = dereferencedData;
        try {
            os = new UnsyncBufferedOutputStream(dos);
            for (int i = 0, size = transforms.size(); i < size; i++) {
                DOMTransform transform = (DOMTransform)transforms.get(i);
                if (i < size - 1) {
                    data = transform.transform(data, context);
                } else {
                    data = transform.transform(data, context, os);
                }
            }
        
            if (data != null) {
                XMLSignatureInput xi;
                // explicitly use C14N 1.1 when generating signature
                // first check system property, then context property
                boolean c14n11 = useC14N11;
                String c14nalg = CanonicalizationMethod.INCLUSIVE;
                if (context instanceof XMLSignContext) {
                    if (!c14n11) {
                        Boolean prop = (Boolean)context.getProperty
                            ("org.apache.xml.security.useC14N11");
                        c14n11 = prop != null && prop;
                        if (c14n11) {
                            c14nalg = "http://www.w3.org/2006/12/xml-c14n11";
                        }
                    } else {
                        c14nalg = "http://www.w3.org/2006/12/xml-c14n11";
                    }
                }
                if (data instanceof ApacheData) {
                    xi = ((ApacheData)data).getXMLSignatureInput();
                } else if (data instanceof OctetStreamData) {
                    xi = new XMLSignatureInput
                        (((OctetStreamData)data).getOctetStream());
                } else if (data instanceof NodeSetData) {
                    TransformService spi = null;
                    if (provider == null) {
                        spi = TransformService.getInstance(c14nalg, "DOM");
                    } else {
                        try {
                            spi = TransformService.getInstance(c14nalg, "DOM", provider);
                        } catch (NoSuchAlgorithmException nsae) {
                            spi = TransformService.getInstance(c14nalg, "DOM");
                        }
                    }
                    data = spi.transform(data, context);
                    xi = new XMLSignatureInput
                        (((OctetStreamData)data).getOctetStream());
                } else {
                    throw new XMLSignatureException("unrecognized Data type");
                }
                if (context instanceof XMLSignContext && c14n11
                    && !xi.isOctetStream() && !xi.isOutputStreamSet()) {
                    TransformService spi = null;
                    if (provider == null) {
                        spi = TransformService.getInstance(c14nalg, "DOM");
                    } else {
                        try {
                            spi = TransformService.getInstance(c14nalg, "DOM", provider);
                        } catch (NoSuchAlgorithmException nsae) {
                            spi = TransformService.getInstance(c14nalg, "DOM");
                        }
                    }
                    
                    DOMTransform t = new DOMTransform(spi);
                    Element transformsElem = null;
                    String dsPrefix = DOMUtils.getSignaturePrefix(context);
                    if (allTransforms.isEmpty()) {
                        transformsElem = DOMUtils.createElement(
                            refElem.getOwnerDocument(), 
                            "Transforms", XMLSignature.XMLNS, dsPrefix);
                        refElem.insertBefore(transformsElem, 
                            DOMUtils.getFirstChildElement(refElem));
                    } else {
                        transformsElem = DOMUtils.getFirstChildElement(refElem);
                    }
                    XmlWriter xwriter = new XmlWriterToTree(Marshaller.getMarshallers(), transformsElem);
                    t.marshal(xwriter, dsPrefix, context);
                    allTransforms.add(t);
                    xi.updateOutputStream(os, true);
                } else {
                    xi.updateOutputStream(os);
                }
            }
            os.flush();
            if (cache != null && cache) {
                this.dis = dos.getInputStream();
            }
            return dos.getDigestValue();
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSignatureException(e);
        } catch (TransformException e) {
            throw new XMLSignatureException(e);
        } catch (MarshalException e) {
            throw new XMLSignatureException(e);
        } catch (IOException e) {
            throw new XMLSignatureException(e);
        } catch (org.apache.xml.security.c14n.CanonicalizationException e) {
            throw new XMLSignatureException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new XMLSignatureException(e);
                } 
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    throw new XMLSignatureException(e);
                } 
            }
        }
    }

    @Override
    public Node getHere() {
        return here;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Reference)) {
            return false;
        }
        Reference oref = (Reference)o;

        boolean idsEqual = id == null ? oref.getId() == null
                                       : id.equals(oref.getId());
        boolean urisEqual = uri == null ? oref.getURI() == null
                                         : uri.equals(oref.getURI());
        boolean typesEqual = type == null ? oref.getType() == null
                                           : type.equals(oref.getType());
        boolean digestValuesEqual =
            Arrays.equals(digestValue, oref.getDigestValue());

        return digestMethod.equals(oref.getDigestMethod()) && idsEqual &&
            urisEqual && typesEqual && 
            allTransforms.equals(oref.getTransforms()) && digestValuesEqual;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (id != null) {
            result = 31 * result + id.hashCode();
        }
        if (uri != null) {
            result = 31 * result + uri.hashCode();
        }
        if (type != null) {
            result = 31 * result + type.hashCode();
        }
        if (digestValue != null) {
            result = 31 * result + Arrays.hashCode(digestValue);
        }
        result = 31 * result + digestMethod.hashCode();
        result = 31 * result + allTransforms.hashCode();
        
        return result;
    }

    boolean isDigested() {
        return digested;
    }

    private static Data copyDerefData(Data dereferencedData) {
        if (dereferencedData instanceof ApacheData) {
            // need to make a copy of the Data
            ApacheData ad = (ApacheData)dereferencedData;
            XMLSignatureInput xsi = ad.getXMLSignatureInput();
            if (xsi.isNodeSet()) {
                try {
                    final Set<Node> s = xsi.getNodeSet();
                    return new NodeSetData() {
                        @Override
                        public Iterator<Node> iterator() { return s.iterator(); }
                    };
                } catch (Exception e) {
                    // log a warning
                    log.warn("cannot cache dereferenced data: " + e);
                    return null;
                }
            } else if (xsi.isElement()) {
                return new DOMSubTreeData
                    (xsi.getSubNode(), xsi.isExcludeComments());
            } else if (xsi.isOctetStream() || xsi.isByteArray()) {
                try {
                    return new OctetStreamData
                        (xsi.getOctetStream(), xsi.getSourceURI(),
                         xsi.getMIMEType());
                } catch (IOException ioe) {
                    // log a warning
                    log.warn("cannot cache dereferenced data: " + ioe);
                    return null;
                }
            }
        }
        return dereferencedData;
    }
}
