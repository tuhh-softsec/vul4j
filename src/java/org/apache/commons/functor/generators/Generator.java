/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/generators/Attic/Generator.java,v 1.3 2003/06/24 15:49:58 rwaldhoff Exp $
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

package org.apache.commons.functor.generators;

import java.util.Collection;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generators.util.CollectionTransformer;

/**
 * Base class for generators. Adds support for all of the {@link Algorithms} to
 * each subclass.
 *
 * @since 1.0
 * @version $Revision: 1.3 $ $Date: 2003/06/24 15:49:58 $
 * @author  Jason Horman (jason@jhorman.org)
 */

public abstract class Generator {

    /** A generator can wrap another generator. */
    private Generator wrappedGenerator = null;

    /** Create a new generator. */
    public Generator() {

    }

    /**
     * A generator can wrap another generator. When wrapping generators you
     * should use probably this constructor since doing so will cause the
     * {@link #stop} method to stop the wrapped generator as well.
     */
    public Generator(Generator generator) {
        this.wrappedGenerator = generator;
    }

    /** Get the generator that is being wrapped. */
    public Generator getWrappedGenerator() {
        return wrappedGenerator;
    }

    /** Generators must implement this method. */
    public abstract void run(UnaryProcedure proc);

    /** Stop the generator. Will stop the wrapped generator if one was set. */
    public void stop() {
        if (wrappedGenerator != null) wrappedGenerator.stop();
        stopped = true;
    }

    /** Check if the generator is stopped. */
    public boolean isStopped() {
        return stopped;
    }

    /** Set to true when the generator is {@link #stop stopped}. */
    private boolean stopped = false;

    /** See {@link Algorithms#apply}. */
    public final Generator apply(UnaryFunction func) {
        return Algorithms.apply(this,func);
    }

    /** See {@link Algorithms#contains}. */
    public final boolean contains(UnaryPredicate pred) {
        return Algorithms.contains(this, pred);
    }

    /** See {@link Algorithms#detect}. */
    public final Object detect(UnaryPredicate pred) {
        return Algorithms.detect(this, pred);
    }

    /** See {@link Algorithms#detect}. */
    public final Object detect(UnaryPredicate pred, Object ifNone) {
        return Algorithms.detect(this, pred, ifNone);
    }

    /** Synonym for run. */
    public final void foreach(UnaryProcedure proc) {
        Algorithms.foreach(this, proc);
    }

    /** See {@link Algorithms#inject}. */
    public final Object inject(Object seed, BinaryFunction func) {
        return Algorithms.inject(this, seed, func);
    }

    /** See {@link Algorithms#reject}. */
    public final Generator reject(UnaryPredicate pred) {
        return Algorithms.reject(this, pred);
    }

    /** See {@link Algorithms#select}. */
    public final Generator select(UnaryPredicate pred) {
        return Algorithms.select(this, pred);
    }

    /** See {@link Algorithms#until}. */
    public final Generator until(UnaryPredicate pred) {
        return Algorithms.until(this, pred);
    }

    /**
     * {@link Transformer Transforms} this generator using the passed in
     * transformer. An example transformer might turn the contents of the
     * generator into a {@link Collection} of elements.
     */
    public final Object to(Transformer transformer) {
        return transformer.transform(this);
    }

    /** Same as to(new CollectionTransformer(collection)). */
    public final Collection to(Collection collection) {
        return (Collection)to(new CollectionTransformer(collection));
    }

    /** Same as to(new CollectionTransformer()). */
    public final Collection toCollection() {
        return (Collection)to(new CollectionTransformer());
    }
}