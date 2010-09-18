/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.asn1.ber.tlv;


/**
 * Enum for ASN.1 UNIVERSAL class tags. The tags values are constructed using
 * the SNACC representation for tags without the primitive/constructed bit. This
 * is done because several bit, octet and character string types can be encoded
 * as primitives or as constructed types to chunk the value out.
 * <p>
 * These tags can have one of the following values:
 * </p>
 * <p>
 * </p>
 * <table border="1" cellspacing="1" width="60%">
 * <tr>
 * <th>Id</th>
 * <th>Usage</th>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 0]</td>
 * <td>reserved for BER</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 1]</td>
 * <td>BOOLEAN</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 2]</td>
 * <td>INTEGER</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 3]</td>
 * <td>BIT STRING</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 4]</td>
 * <td>OCTET STRING</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 5]</td>
 * <td>NULL</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 6]</td>
 * <td>OBJECT IDENTIFIER</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 7]</td>
 * <td>ObjectDescriptor</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 8]</td>
 * <td>EXTERNAL, INSTANCE OF</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 9]</td>
 * <td>REAL</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 10]</td>
 * <td>ENUMERATED</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 11]</td>
 * <td>EMBEDDED PDV</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 12]</td>
 * <td>UTF8String</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 13]</td>
 * <td>RELATIVE-OID</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 14]</td>
 * <td>reserved for future use</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 15]</td>
 * <td>reserved for future use</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 16]</td>
 * <td>SEQUENCE, SEQUENCE OF</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 17]</td>
 * <td>SET, SET OF</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 18]</td>
 * <td>NumericString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 19]</td>
 * <td>PrintableString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 20]</td>
 * <td>TeletexString, T61String</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 21]</td>
 * <td>VideotexString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 22]</td>
 * <td>IA5String</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 23]</td>
 * <td>UTCTime</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 24]</td>
 * <td>GeneralizedTime</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 25]</td>
 * <td>GraphicString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 26]</td>
 * <td>VisibleString, ISO646String</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 27]</td>
 * <td>GeneralString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 28]</td>
 * <td>UniversalString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 29]</td>
 * <td>CHARACTER STRING</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 30]</td>
 * <td>BMPString</td>
 * </tr>
 * <tr>
 * <td>[UNIVERSAL 31]</td>
 * <td>reserved for future use</td>
 * </tr>
 * </table>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum UniversalTag
{
    /** value for the tag */
    RESERVED_0(( byte ) 0),

    /** value for the tag */
    BOOLEAN(( byte ) 1),

    /** value for the tag */
    INTEGER(( byte ) 2),

    /** value for the tag */
    BIT_STRING(( byte ) 3),

    /** value for the tag */
    OCTET_STRING(( byte ) 4),

    /** value for the tag */
    NULL(( byte ) 5),

    /** value for the tag */
    OBJECT_IDENTIFIER(( byte ) 6),

    /** value for the tag */
    OBJECT_DESCRIPTOR(( byte ) 7),

    /** value for the tag */
    EXTERNAL_INSTANCE_OF(( byte ) 8),

    /** value for the tag */
    REAL(( byte ) 9),

    /** value for the tag */
    ENUMERATED(( byte ) 0x0A),

    /** value for the tag */
    EMBEDDED_PDV(( byte ) 0x0B),

    /** value for the tag */
    UTF8_STRING(( byte ) 0x0C),

    /** value for the tag */
    RELATIVE_OID(( byte ) 0x0D),

    /** value for the tag */
    RESERVED_14(( byte ) 0x0E),

    /** value for the tag */
    RESERVED_15(( byte ) 0x0F),

    /** value for the tag */
    SEQUENCE_SEQUENCE_OF(( byte ) 0x10),

    /** value for the tag */
    SET_SET_OF(( byte ) 0x11),

    /** value for the tag */
    NUMERIC_STRING(( byte ) 0x12),

    /** value for the tag */
    PRINTABLE_STRING(( byte ) 0x13),

    /** value for the tag */
    TELETEX_STRING(( byte ) 0x14),

    /** value for the tag */
    VIDEOTEX_STRING(( byte ) 0x15),

    /** value for the tag */
    IA5_STRING(( byte ) 0x16),

    /** value for the tag */
    UTC_TIME(( byte ) 0x17),

    /** value for the tag */
    GENERALIZED_TIME(( byte ) 0x18),

    /** value for the tag */
    GRAPHIC_STRING(( byte ) 0x19),

    /** value for the tag */
    VISIBLE_STRING(( byte ) 0x1A),

    /** value for the tag */
    GENERAL_STRING(( byte ) 0x1B),

    /** value for the tag */
    UNIVERSAL_STRING(( byte ) 0x1C),

    /** value for the tag */
    CHARACTER_STRING(( byte ) 0x1D),

    /** value for the tag */
    BMP_STRING(( byte ) 0x1E),

    /** value for the tag */
    RESERVED_31(( byte ) 0x1F),

    /** SEQUENCE TAG */
    SEQUENCE(( byte ) 0x30),

    /** SET TAG */
    SET(( byte ) 0x31);

    /** The internal value */
    private byte value;


    /**
     * Creates a new instance of UniversalTag.
     *
     * @param value The tag value
     */
    private UniversalTag( byte value )
    {
        this.value = value;
    }


    /**
     * @return The UniversalTag value
     */
    public byte getValue()
    {
        return value;
    }
}
