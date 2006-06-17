/*
 * Copyright 2005 The Apache Software Foundation.
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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id$
 */
package javax.xml.crypto;

/**
 * Identifies a data object via a URI-Reference, as specified by 
 * <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
 *
 * <p>Note that some subclasses may not have a <code>type</code> attribute
 * and for objects of those types, the {@link #getType} method always returns 
 * <code>null</code>. 
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 * @see URIDereferencer
 */
public interface URIReference {

    /**
     * Returns the URI of the referenced data object.
     *
     * @return the URI of the data object in RFC 2396 format (may be
     *    <code>null</code> if not specified)
     */
    String getURI();

    /**
     * Returns the type of data referenced by this URI.
     *
     * @return the type (a URI) of the data object (may be <code>null</code> 
     *    if not specified)
     */
    String getType();
}
