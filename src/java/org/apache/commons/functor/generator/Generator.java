/*
 * $Id: Generator.java,v 1.5 2003/11/24 21:13:15 rwaldhoff Exp $
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
package org.apache.commons.functor.generator;

import java.util.Collection;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * @version $Revision: 1.5 $ $Date: 2003/11/24 21:13:15 $
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public interface Generator {
    /** Generators must implement this method. */
    public abstract void run(UnaryProcedure proc);
    /** Stop the generator. Will stop the wrapped generator if one was set. */
    public abstract void stop();
    /** Check if the generator is stopped. */
    public abstract boolean isStopped();
    /** See {@link org.apache.commons.functor.Algorithms#apply}. */
    public abstract Generator apply(UnaryFunction func);
    /** See {@link org.apache.commons.functor.Algorithms#contains}. */
    public abstract boolean contains(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#detect}. */
    public abstract Object detect(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#detect}. */
    public abstract Object detect(UnaryPredicate pred, Object ifNone);
    /** Synonym for run. */
    public abstract void foreach(UnaryProcedure proc);
    /** See {@link org.apache.commons.functor.Algorithms#inject}. */
    public abstract Object inject(Object seed, BinaryFunction func);
    /** See {@link org.apache.commons.functor.Algorithms#reject}. */
    public abstract Generator reject(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#select}. */
    public abstract Generator select(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#until}. */
    public abstract Generator until(UnaryPredicate pred);
    /**
     * {@link Transformer Transforms} this generator using the passed in
     * transformer. An example transformer might turn the contents of the
     * generator into a {@link Collection} of elements.
     */
    public abstract Object to(Transformer transformer);
    /** Same as to(new CollectionTransformer(collection)). */
    public abstract Collection to(Collection collection);
    /** Same as to(new CollectionTransformer()). */
    public abstract Collection toCollection();
}