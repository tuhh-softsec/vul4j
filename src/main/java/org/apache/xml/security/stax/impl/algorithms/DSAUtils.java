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
package org.apache.xml.security.stax.impl.algorithms;

import java.io.IOException;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class DSAUtils {

    /**
     * Converts an ASN.1 DSA value to a XML Signature DSA Value.
     * <p/>
     * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
     * pairs; the XML Signature requires the core BigInteger values.
     *
     * @param asn1Bytes
     * @throws java.io.IOException
     * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
     */
    public static byte[] convertASN1toXMLDSIG(byte asn1Bytes[]) throws IOException {
        byte rLength = asn1Bytes[3];
        int i;

        for (i = rLength; i > 0 && asn1Bytes[4 + rLength - i] == 0; i--) ;

        byte sLength = asn1Bytes[5 + rLength];
        int j;

        for (j = sLength; j > 0 && asn1Bytes[6 + rLength + sLength - j] == 0; j--) ;

        if (asn1Bytes[0] != 48
                || asn1Bytes[1] != asn1Bytes.length - 2
                || asn1Bytes[2] != 2 || i > 20
                || asn1Bytes[4 + rLength] != 2 || j > 20) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        } else {
            byte xmldsigBytes[] = new byte[40];

            System.arraycopy(asn1Bytes, 4 + rLength - i, xmldsigBytes, 20 - i, i);
            System.arraycopy(asn1Bytes, 6 + rLength + sLength - j, xmldsigBytes, 40 - j, j);

            return xmldsigBytes;
        }
    }

    /**
     * Converts a XML Signature DSA Value to an ASN.1 DSA value.
     * <p/>
     * The JAVA JCE DSA Signature algorithm creates ASN.1 encoded (r,s) value
     * pairs; the XML Signature requires the core BigInteger values.
     *
     * @param xmldsigBytes
     * @throws IOException
     * @see <A HREF="http://www.w3.org/TR/xmldsig-core/#dsa-sha1">6.4.1 DSA</A>
     */
    public static byte[] convertXMLDSIGtoASN1(byte xmldsigBytes[]) throws IOException {
        if (xmldsigBytes.length != 40) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }

        int i;

        for (i = 20; i > 0 && xmldsigBytes[20 - i] == 0; i--) ;

        int j = i;

        if (xmldsigBytes[20 - i] < 0) {
            j += 1;
        }

        int k;

        for (k = 20; k > 0 && xmldsigBytes[40 - k] == 0; k--) ;

        int l = k;

        if (xmldsigBytes[40 - k] < 0) {
            l += 1;
        }

        byte asn1Bytes[] = new byte[6 + j + l];

        asn1Bytes[0] = 48;
        asn1Bytes[1] = (byte) (4 + j + l);
        asn1Bytes[2] = 2;
        asn1Bytes[3] = (byte) j;

        System.arraycopy(xmldsigBytes, 20 - i, asn1Bytes, 4 + j - i, i);

        asn1Bytes[4 + j] = 2;
        asn1Bytes[5 + j] = (byte) l;

        System.arraycopy(xmldsigBytes, 40 - k, asn1Bytes, 6 + j + l - k, k);

        return asn1Bytes;
    }
}
