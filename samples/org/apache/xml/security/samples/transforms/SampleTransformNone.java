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
package org.apache.xml.security.samples.transforms;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;

/**
 * Implements a null transform which leaved the input unmodified.
 *
 * @author Christian Geuer-Pollmann
 */
public class SampleTransformNone extends TransformSpi {

    static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(SampleTransformNone.class);

    /** Field implementedTransformURI */
    public static final String implementedTransformURI =
        "http://www.xmlsecurity.org/NS/Transforms#none";
    
    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Method engineGetURI
     *
     *
     */
    protected String engineGetURI() {
        return SampleTransformNone.implementedTransformURI;
    }

    public boolean wantsOctetStream ()   { return true; }
    public boolean wantsNodeSet ()       { return true; }
    public boolean returnsOctetStream () { return true; }
    public boolean returnsNodeSet ()     { return true; }

    /**
     * Method enginePerformTransform
     *
     * @param input
     *
     */
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, Transform _transformObject) {
        return input;
    }

}
