/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/four/DataMunger.java,v 1.6 2003/12/17 22:05:26 rwaldhoff Exp $
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
package org.apache.commons.functor.example.kata.four;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.core.IsNull;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;
import org.apache.commons.functor.core.comparator.IsLessThan;
import org.apache.commons.functor.core.composite.Composite;
import org.apache.commons.functor.core.composite.Conditional;
import org.apache.commons.functor.core.composite.ConditionalBinaryFunction;
import org.apache.commons.functor.example.kata.one.BinaryFunctionUnaryFunction;
import org.apache.commons.functor.example.kata.one.Subtract;
import org.apache.commons.functor.example.lines.Lines;

/**
 * @version $Revision: 1.6 $ $Date: 2003/12/17 22:05:26 $
 * @author Rodney Waldhoff
 */
public class DataMunger {

    public static final Object process(final InputStream file, final int selected, final int col1, final int col2) {
        return process(new InputStreamReader(file),selected,col1,col2);
    }

    public static final Object process(final Reader file, final int selected, final int col1, final int col2) {
        return NthColumn.instance(selected).evaluate(
            Algorithms.inject(
                Lines.from(file).where(
                    Composite.predicate(IsInteger.instance(),NthColumn.instance(0))),                    
                null,
                lesserSpread(col1,col2)));            
    }
    

    /** 
     * A BinaryFunction that will calcuate the absolute
     * difference between col1 and col2 in the given 
     * String arguments, and return the argument
     * whose difference is smallest.
     */
    private static final BinaryFunction lesserSpread(final int col1, final int col2) {
        return new ConditionalBinaryFunction(            
            IsNull.left(),                                 // if left is null
            RightIdentity.instance(),                      //   return right
            Conditional.function(                          //   else return the parameter with the least spread
                Composite.predicate(                       //     if left is less than right
                    IsLessThan.instance(),
                    absSpread(col1,col2),
                    absSpread(col1,col2)),
                LeftIdentity.instance(),                   //       return left
                RightIdentity.instance()                   //       else return right 
            )
        );
    }

    private static UnaryFunction absSpread(final int col1, final int col2) {
        return Composite.function(
            Abs.instance(),
            new BinaryFunctionUnaryFunction(
                Composite.function(
                    Subtract.instance(),
                    Composite.function(ToInteger.instance(),NthColumn.instance(col1)),
                    Composite.function(ToInteger.instance(),NthColumn.instance(col2)))
                ));
    }

}
