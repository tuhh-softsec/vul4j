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
import java.util.List;
import java.util.LinkedList;
import org.w3c.dom.Element;


/**
 * <code>ReferenceList</code> is an element that contains pointers from a key
 * value of an <code>EncryptedKey</code> to items encrypted by that key value
 * (<code>EncryptedData</code> or <code>EncryptedKey</code> elements).
 * <p>
 * It is defined as follows:
 * <xmp>
 * <element name='ReferenceList'>
 *     <complexType>
 *         <choice minOccurs='1' maxOccurs='unbounded'>
 *             <element name='DataReference' type='xenc:ReferenceType'/>
 *             <element name='KeyReference' type='xenc:ReferenceType'/>
 *         </choice>
 *     </complexType>
 * </element>
 * </xmp>
 *
 * @author Axl Mattheus
 * @see Reference.
 */
public class ReferenceList {
    public static final int DATA_REFERENCE = 0x00000001;
    public static final int KEY_REFERENCE  = 0x00000002;
    private Class sentry;
    private List references;

    /**
     * Returns an instance of <code>ReferenceList</code>, initialized with the
     * appropriate parameters.
     *
     * @param type the type of references this <code>ReferenceList</code> will
     *   hold.
     */
    public ReferenceList (int type) {
        if (type == DATA_REFERENCE) {
            sentry = DataReference.class;
        } else if (type == KEY_REFERENCE) {
            sentry = KeyReference.class;
        } else {
            throw new IllegalArgumentException();
        }
        references = new LinkedList();
    }

    /**
     * Adds a reference to this reference list.
     *
     * @param reference the reference to add.
     * @throws IllegalArgurmentException if the <code>Reference</code> is not an
     *   instance of <code>DataReference</code> or <code>KeyReference</code>.
     */
    public void add(Reference reference) {
        if (!reference.getClass().equals(sentry)) {
            throw new IllegalArgumentException();
        } else {
            references.add(reference);
        }
    }

    /**
     * Removes a reference from the <code>ReferenceList</code>.
     *
     * @param reference the reference to remove.
     */
    public void remove(Reference reference) {
        if (!reference.getClass().equals(sentry)) {
            throw new IllegalArgumentException();
        } else {
            references.remove(reference);
        }
    }

    /**
     * Returns the size of the <code>ReferenceList</code>.
     *
     * @return the size of the <code>ReferenceList</code>.
     */
    public int size() {
        return (references.size());
    }

    /**
     * Indicates if the <code>ReferenceList</code> is empty.
     *
     * @return <code><b>true</b></code> if the <code>ReferenceList</code> is
     *     empty, else <code><b>false</b></code>.
     */
    public boolean isEmpty() {
        return (references.isEmpty());
    }

    /**
     * Returns an <code>Iterator</code> over all the <code>Reference</code>s
     * contatined in this <code>ReferenceList</code>.
     *
     * @return Iterator.
     */
    public Iterator getReferences() {
        return (references.iterator());
    }

    /**
     * <code>DataReference</code> factory method. Returns a
     * <code>DataReference</code>.
     */
    public static Reference newDataReference(String uri) {
        return (new DataReference(uri));
    }

    /**
     * <code>KeyReference</code> factory method. Returns a
     * <code>KeyReference</code>.
     */
    public static Reference newKeyReference(String uri) {
        return (new KeyReference(uri));
    }

    /**
     * <code>ReferenceImpl</code> is an implementation of
     * <code>Reference</code>.
     *
     * @see Reference.
     */
    private static class ReferenceImpl implements Reference {
        private String uri;
        private List referenceInformation;

        ReferenceImpl(String uri) {
            this.uri = uri;
            referenceInformation = new LinkedList();
        }

        public String getURI() {
            return (uri);
        }

        public Iterator getElementRetrievalInformation() {
            return (referenceInformation.iterator());
        }

        public void setURI(String uri) {
        }

        public void removeElementRetrievalInformation(Element node) {
            referenceInformation.remove(node);
        }

        public void addElementRetrievalInformation(Element node) {
            referenceInformation.add(node);
        }
    }

    private static class DataReference extends ReferenceImpl {
        DataReference(String uri) {
            super(uri);
        }
    }

    private static class KeyReference extends ReferenceImpl {
        KeyReference(String uri) {
            super (uri);
        }
    }
}

