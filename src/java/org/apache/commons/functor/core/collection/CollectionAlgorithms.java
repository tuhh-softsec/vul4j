/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/core/collection/Attic/CollectionAlgorithms.java,v 1.5 2003/11/24 23:09:13 rwaldhoff Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Utility methods and algorithms for applying functors 
 * to {@link Collection Collections}.
 * 
 * @version $Revision: 1.5 $ $Date: 2003/11/24 23:09:13 $
 * @author Rodney Waldhoff
 */
public final class CollectionAlgorithms {

    // constructor
    // ------------------------------------------------------------------------
    
    /** 
     * Public constructor, for systems that prefer 
     * instantiable classes (Jelly, Velocity, etc.)
     */
    public CollectionAlgorithms() {
    }
 
    // static methods
    // ------------------------------------------------------------------------
    
    /**
     * {@link UnaryFunction#evaluate Apply} the given 
     * {@link UnaryFunction UnaryFunction} to
     * each element in the given {@link Iterator Iterator},
     * and {@link Collection#add add} the result to a
     * new {@link Collection}.
     * 
     * @see #collect(Iterator,UnaryFunction,Collection)
     */
    public static Collection collect(Iterator iter, UnaryFunction func) {
        return collect(iter,func,new ArrayList());
    }
    
    /**
     * {@link UnaryFunction#evaluate Apply} the given 
     * {@link UnaryFunction UnaryFunction} to
     * each element in the given {@link Iterator Iterator},
     * and {@link Collection#add add} the result to the
     * given {@link Collection}.
     * 
     * @return the given {@link Collection}
     * @see #collect(Iterator,UnaryFunction)
     */
    public static Collection collect(Iterator iter, UnaryFunction func, Collection col) {
        while(iter.hasNext()) {
            col.add(func.evaluate(iter.next()));
        }
        return col;
    }
    
    /**
     * Return <code>true</code> iff some element in
     * the given
     * {@link Iterator Iterator} that matches the given
     * {@link UnaryPredicate UnaryPredicate}.
     * 
     * @see #detect(Iterator,UnaryPredicate)
     */
    public static boolean contains(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            if(pred.test(iter.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the first element within the given
     * {@link Iterator Iterator} that matches the given
     * {@link UnaryPredicate UnaryPredicate}, or throw 
     * {@link NoSuchElementException NoSuchElementException} if no
     * matching element can be found.
     * 
     * @see #detect(Iterator,UnaryPredicate,Object)
     */
    public static Object detect(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(pred.test(obj)) {
                return obj;
            }
        }
        throw new NoSuchElementException("No element matching " + pred + " was found.");
    }

    /**
     * Return the first element within the given
     * {@link Iterator Iterator} that matches the given
     * {@link UnaryPredicate UnaryPredicate}, or return
     * the given (possibly <code>null</code> <code>Object</code>
     * if no matching element can be found.
     * 
     * @see #detect(Iterator,UnaryPredicate)
     */
    public static Object detect(Iterator iter, UnaryPredicate pred, Object ifNone) {
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(pred.test(obj)) {
                return obj;
            }
        }
        return ifNone;
    }

    /**
     * {@link UnaryProcedure#run Apply} the given 
     * {@link UnaryProcedure UnaryProcedure}
     * to each element in the given {@link Iterator Iterator}.
     */
    public static void foreach(Iterator iter, UnaryProcedure proc) {
        while(iter.hasNext()) {
            proc.run(iter.next());
        }
    }

    /**
     * {@link BinaryFunction#evaluate Evaluate} the pair
     * <i>( previousResult, element )</i> for each element 
     * in the given {@link Iterator Iterator} where 
     * previousResult is initially <i>seed</i>, and thereafter
     * the result of the evaluation of the previous element
     * in the iterator.  Returns the result of the final
     * evaluation.
     * <p>
     * In code:
     * <pre>while(iter.hasNext()) { 
     *   seed = func.evaluate(seed,iter.next());
     * }
     * return seed;</pre>
     */
    public static Object inject(Iterator iter, Object seed, BinaryFunction func) {        
        while(iter.hasNext()) {
            seed = func.evaluate(seed,iter.next());
        }
        return seed;
    }

    /**
     * {@link Collection#add Add} all elements within the
     * given {@link Iterator Iterator} that fail to match the
     * given {@link UnaryPredicate UnaryPredicate} to the
     * given {@link Collection Collection}.
     * 
     * @return the given {@link Collection Collection}
     * @see #reject(Iterator,UnaryPredicate)
     * @see #select(Iterator,UnaryPredicate,Collection)
     */
    public static Collection reject(Iterator iter, UnaryPredicate pred, Collection col) {
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(!pred.test(obj)) {
                col.add(obj);
            }
        }
        return col;
    }

    /**
     * {@link Collection#add Add} all elements within the
     * given {@link Iterator Iterator} that fail to match the
     * given {@link UnaryPredicate UnaryPredicate} to a
     * new {@link Collection Collection}.
     * 
     * @return the new {@link Collection Collection}
     * @see #reject(Iterator,UnaryPredicate,Collection)
     * @see #select(Iterator,UnaryPredicate)
     */
    public static Collection reject(Iterator iter, UnaryPredicate pred) {
        return reject(iter,pred,new ArrayList());
    }

    /**
     * {@link Iterator#remove Renmove} from the
     * given {@link Iterator Iterator} all elements 
     * that match the
     * given {@link UnaryPredicate UnaryPredicate}.
     * 
     * @see #retain(Iterator,UnaryPredicate)
     */
    public static void remove(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            if(pred.test(iter.next())) {
                iter.remove();
            }
        }        
    }
    
    /**
     * {@link Iterator#remove Renmove} from the
     * given {@link Iterator Iterator} all elements 
     * that fail to match the
     * given {@link UnaryPredicate UnaryPredicate}.
     * 
     * @see #remove(Iterator,UnaryPredicate)
     */
    public static void retain(Iterator iter, UnaryPredicate pred) {
        while(iter.hasNext()) {
            if(!(pred.test(iter.next()))) {
                iter.remove();
            }
        }
    }

    /**
     * {@link Collection#add Add} all elements within the
     * given {@link Iterator Iterator} that match the
     * given {@link UnaryPredicate UnaryPredicate} to the
     * given {@link Collection Collection}.
     * 
     * @return the given {@link Collection Collection}
     * @see #select(Iterator,UnaryPredicate)
     * @see #reject(Iterator,UnaryPredicate,Collection)
     */
    public static Collection select(Iterator iter, UnaryPredicate pred, Collection col) {
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(pred.test(obj)) {
                col.add(obj);
            }
        }
        return col;
    }

    /**
     * {@link Collection#add Add} all elements within the
     * given {@link Iterator Iterator} that match the
     * given {@link UnaryPredicate UnaryPredicate} to a
     * new {@link Collection Collection}.
     * 
     * @return the new {@link Collection Collection}
     * @see #select(Iterator,UnaryPredicate,Collection)
     * @see #reject(Iterator,UnaryPredicate)
     */
    public static Collection select(Iterator iter, UnaryPredicate pred) {
        return select(iter,pred,new ArrayList());
    }

    /**
     * {@link ListIterator#set Set} each element of the
     * given {@link ListIterator ListIterator} to
     * the result of applying the 
     * given {@link UnaryFunction UnaryFunction} to
     * its original value.
     */
    public static void transform(ListIterator iter, UnaryFunction func) {
        while(iter.hasNext()) {
            iter.set(func.evaluate(iter.next()));
        }
    }
}
