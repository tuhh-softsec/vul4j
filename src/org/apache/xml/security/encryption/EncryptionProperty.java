/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.encryption;


import java.util.Iterator;
import org.w3c.dom.Element;

/**
 * Additional information items concerning the generation of the
 * <code>EncryptedData</code> or <code>EncryptedKey</code> can be placed in an
 * <code>EncryptionProperty</code> element (e.g., date/time stamp or the serial
 * number of cryptographic hardware used during encryption). The Target
 * attribute identifies the <code>EncryptedType</code> structure being
 * described. anyAttribute permits the inclusion of attributes from the XML
 * namespace to be included (i.e., <code>xml:space</code>,
 * <code>xml:lang</code>, and <code>xml:base</code>).
 * <p>
 * It is defined as follows:
 * <xmp>
 * <element name='EncryptionProperty' type='xenc:EncryptionPropertyType'/>
 * <complexType name='EncryptionPropertyType' mixed='true'>
 *     <choice maxOccurs='unbounded'>
 *         <any namespace='##other' processContents='lax'/>
 *     </choice>
 *     <attribute name='Target' type='anyURI' use='optional'/>
 *     <attribute name='Id' type='ID' use='optional'/>
 *     <anyAttribute namespace="http://www.w3.org/XML/1998/namespace"/>
 * </complexType>
 * </xmp>
 *
 * @author Axl Mattheus
 */
public interface EncryptionProperty {
    /**
     * Returns the <code>EncryptedType</code> being described.
     *
     * @return the <code>EncryptedType</code> being described by this
     *   <code>EncryptionProperty</code>.
     */
    String getTarget();

    /**
     * Sets the target.
     *
     * @param target.
     */
    void setTarget(String target);

    /**
     * Returns the id of the <CODE>EncryptionProperty</CODE>.
     *
     * @return the id.
     */
    String getId();

    /**
     * Sets the id.
     *
     * @param id.
     */
    void setId(String id);

    /**
     * Returns the attribute's value in the <code>xml</code> namespace.
     *
     * @return the attribute's value.
     */
    String getAttribute(String attribute);

    /**
     * Set the attribute value.
     *
     * @param attribute the attribute's name.
     * @param value the attribute's value.
     */
    void setAttribute(String attribute, String value);

    /**
     * Returns the properties of the <CODE>EncryptionProperty</CODE>.
     *
     * @return an <code>Iterator</code> over all the addiitonal encryption
     *   information contained in this class.
     */
    Iterator getEncryptionInformation();

    /**
     * Adds encryption information.
     *
     * @param information the additional encryption information.
     */
    void addEncryptionInformation(Element information);

    /**
     * Removes encryption information.
     *
     * @param information the information to remove.
     */
    void removeEncryptionInformation(Element information);
}
