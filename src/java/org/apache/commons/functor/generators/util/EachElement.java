/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/generators/util/Attic/EachElement.java,v 1.1 2003/06/24 15:17:00 rwaldhoff Exp $
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

package org.apache.commons.functor.generators.util;

import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generators.Generator;
import org.apache.commons.functor.generators.Generator;
import org.apache.commons.functor.generators.IteratorToGeneratorAdapter;

import java.util.Collection;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Generator for each element of a collection.
 *
 * @author  Jason Horman (jason@jhorman.org)
 * @version $Id: EachElement.java,v 1.1 2003/06/24 15:17:00 rwaldhoff Exp $
 */

public class EachElement extends Generator {

    /***************************************************
     *  Instance variables
     ***************************************************/

    private Generator generator = null;

    /***************************************************
     *  Constructors
     ***************************************************/

    /**
     * Generator for collections.
     */
    public EachElement(Collection collection) {
        generator = new IteratorToGeneratorAdapter(collection.iterator());
    }

    /**
     * Generator for maps. Generates {@link java.util.Map.Entry} objects.
     */
    public EachElement(Map map) {
        generator = new IteratorToGeneratorAdapter(map.entrySet().iterator());
    }

    /**
     * Generator for arrays.
     */
    public EachElement(Object[] array) {
        generator = new IteratorToGeneratorAdapter(Arrays.asList(array).iterator());
    }

    /**
     * EachElement over a generator.
     */
    public EachElement(Generator generator) {
        this.generator = generator;
    }

    /**
     * EachElement over a iterator.
     */
    public EachElement(Iterator iter) {
        this.generator = new IteratorToGeneratorAdapter(iter);
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    public void run(UnaryProcedure proc) {
        generator.run(proc);
    }

    public void stop() {
        generator.stop();
    }

    public String toString() {
        return "EachElement<" + generator + ">";
    }

    /***************************************************
     *  Class methods
     ***************************************************/

    public static final EachElement from(Collection col) {
        return new EachElement(col);
    }

    public static final EachElement from(Map map) {
        return new EachElement(map);
    }

    public static final EachElement from(Object[] array) {
        return new EachElement(array);
    }

    public static final EachElement from(Iterator iter) {
        return new EachElement(iter);
    }

    public static final EachElement from(Generator gen) {
        return new EachElement(gen);
    }
}