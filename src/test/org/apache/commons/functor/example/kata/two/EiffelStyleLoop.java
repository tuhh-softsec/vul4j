/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/two/EiffelStyleLoop.java,v 1.1 2003/12/01 21:14:47 rwaldhoff Exp $
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
package org.apache.commons.functor.example.kata.two;

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.core.ConstantPredicate;
import org.apache.commons.functor.core.NoOp;

/**
 * Supports an Eiffel style loop construct.
 * <pre>
 * new EiffelStyleLoop()
 *   .from(new Procedure() { public void run() {} }) // init code
 *   .invariant(new Predicate() { public boolean test() {} }) // invariants 
 *   .variant(new Procedure() { public Object evaluate() {} }) // diminishing comparable value 
 *   // or
 *   // .variant(new Predicate() { public boolean test() {} }) // more invariants 
 *   .until(new Predicate() { public boolean test() {} }) // terminating condition
 *   .loop(new Procedure() { public void run() {} }) // the acutal loop
 *   .run();
 * </pre>
 * 
 * Note that <tt>new EiffelStyleLoop().run()</tt> executes just fine.
 * You only need to set the parts of the loop you want to use.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/12/01 21:14:47 $
 * @author Rodney Waldhoff
 */
public class EiffelStyleLoop implements Procedure {
    public EiffelStyleLoop from(Procedure procedure) {
        from = procedure;
        return this;
    }

    public EiffelStyleLoop invariant(Predicate predicate) {
        invariant = predicate;
        return this;
    }

    public EiffelStyleLoop variant(Predicate predicate) {
        variant = predicate;
        return this;
    }

    public EiffelStyleLoop variant(final Function function) {
        return variant(new Predicate() {
            public boolean test() {
                boolean result = true;
                Comparable next = (Comparable)(function.evaluate());
                if(null != last) {
                    result = last.compareTo(next) > 0;
                }
                last = next;
                return result;                
            }
            private Comparable last = null;
        });
    }
    
    public EiffelStyleLoop until(Predicate predicate) {
        until = predicate;
        return this;
    }

    public EiffelStyleLoop loop(Procedure procedure) {
        loop = procedure;
        return this;
    }

    public void run() {
        from.run();
        assertTrue(invariant.test());
        while(! until.test() ) {
            loop.run();                
            assertTrue(variant.test());
            assertTrue(invariant.test());
        }
        
        // Note that: 
        //   assertTrue(until.test());
        // holds here, but isn't necessary since that's
        // the only way we could get out of the loop

        // Also note that: 
        //   assertTrue(invariant.test());
        // holds here, but was the last thing called
        // before until.test()
    }
    
    private void assertTrue(boolean value) {
        if(!value) {
            throw new IllegalStateException("Assertion failed");
        }
    }
    
    private Procedure from = NoOp.instance();
    private Predicate invariant = ConstantPredicate.trueInstance();
    private Predicate variant = ConstantPredicate.trueInstance();
    private Predicate until = ConstantPredicate.falseInstance();
    private Procedure loop = NoOp.instance();        

}