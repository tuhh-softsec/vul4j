/*
 * Copyright  2003-2004 The Apache Software Foundation.
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

        ReferenceImpl(String _uri) {
            this.uri = _uri;
            referenceInformation = new LinkedList();
        }

        public String getURI() {
            return (uri);
        }

        public Iterator getElementRetrievalInformation() {
            return (referenceInformation.iterator());
        }

        public void setURI(String _uri) {
        	this.uri = _uri;
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

