/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/collection/IsElementOf.java,v 1.2 2003/06/24 15:49:58 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.functor.core.collection;

import org.apache.commons.functor.UnaryPredicate;

import java.util.Collection;
import java.io.Serializable;

/**
 * A {@link UnaryPredicate} that checks to see if elements are
 * part of a Collection.
 *
 * @since 1.0
 * @version $Revision: 1.2 $ $Date: 2003/06/24 15:49:58 $
 * @author  Jason Horman (jason@jhorman.org)
 */

public class IsElementOf implements UnaryPredicate, Serializable {

    /***************************************************
     *  Instance variables
     ***************************************************/

    /** The collection that will be checked, .contains'd */
    private Collection c = null;

    /** Hashcode of the name of this Predicate. */
    private static final int nameHashCode = "IsElementOf".hashCode();

    /***************************************************
     *  Constructors
     ***************************************************/

    public IsElementOf(Collection c) {
        if (c == null) {
            throw new IllegalArgumentException("collection must not be null");
        }

        this.c = c;
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    public boolean test(Object o) {
        return c.contains(o);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IsElementOf)) return false;
        final IsElementOf isElementOf = (IsElementOf) o;
        if (!c.equals(isElementOf.c)) return false;
        return true;
    }

    public int hashCode() {
        return 29 * c.hashCode() + nameHashCode;
    }

    public String toString() {
        return "IsElementOf(" + c + ")";
    }
}