/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/four/DataMunger.java,v 1.1 2003/12/02 01:12:07 rwaldhoff Exp $
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
import java.util.StringTokenizer;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.composite.CompositeUnaryFunction;
import org.apache.commons.functor.example.lines.Lines;

/**
 * @version $Revision: 1.1 $ $Date: 2003/12/02 01:12:07 $
 * @author Rodney Waldhoff
 */
public class DataMunger {

    public static final Object process(final InputStream file, final int selected, final int col1, final int col2) {
        return process(new InputStreamReader(file),selected,col1,col2);
    }

    public static final Object process(final Reader file, final int selected, final int col1, final int col2) {
        return nthColumn(selected).evaluate(
            Algorithms.inject(
                Lines.from(file).where(nthColumnIsInteger(0)), 
                null,
                lesserSpread(col1,col2)));            
    }
    

    /** 
     * A UnaryFunction that selects the nth column from the input 
     * String and converts it to an Integer.
     */
    private static final UnaryFunction nthInteger(int n) {  
        return new CompositeUnaryFunction(toInteger(),nthColumn(n));
    }

    /** 
     * Selects the nth column from the input 
     * obj (String) and converts it to an int.
     */
    private static final int nthInteger(int n, Object obj) {
        return toInt(nthInteger(n).evaluate(obj));  
    }

    /** 
     * A UnaryPredicate that returns true iff the the nth column 
     * in the input String can be converted into an Integer.
     * See {@link #toInteger}.
     */
    private static final UnaryPredicate nthColumnIsInteger(final int n) {
        return new UnaryPredicate() {
            public boolean test(Object obj) {
                try {
                    nthInteger(n).evaluate(obj);
                    return true;
                } catch(RuntimeException e) {
                    return false;
                }
            }
        };
    }
    
    /** 
     * A UnaryFunction that returns the nth whitespace
     * delimited column in the given input String, or 
     * null if there is no such column.
     */
    private static final UnaryFunction nthColumn(final int n) {
        return new UnaryFunction() {
            public Object evaluate(Object obj) {
                StringTokenizer toker = new StringTokenizer((String)obj);
                for(int count = 0; count < n && toker.hasMoreTokens();count++) {
                    toker.nextToken();
                }
                return toker.hasMoreTokens() ? toker.nextToken() : null;
            }
        };
    }
    
    /** 
     * Accessor method for {@link #TO_INTEGER}.
     */
    private static final UnaryFunction toInteger() {
        return TO_INTEGER;
    }
    
    /** 
     * Converts the input String to an Integer.
     * Any trailing characters that aren't digits
     * are ignored.
     */
    private static final UnaryFunction TO_INTEGER = new UnaryFunction() {
        public Object evaluate(Object obj) {
            return evaluate((String)obj);
        }
        
        public Object evaluate(String str) {
            StringBuffer buf = new StringBuffer();
            for(int i=0;i<str.length();i++) {
                if(Character.isDigit(str.charAt(i))) {
                    buf.append(str.charAt(i));
                } else {
                    break;
                }
            }
            try {
                return new Integer(buf.toString());
            } catch(NumberFormatException e) {
                throw new NumberFormatException(str);
            }
        }
    };

    /** 
     * A BinaryFunction that will calcuate the absolute
     * difference between col1 and col2 in the given 
     * String arguments, and return the argument
     * whose difference is smallest.
     */
    private static final BinaryFunction lesserSpread(final int col1, final int col2) {
        return new BinaryFunction() {
            public Object evaluate(Object left, Object right) {
                if(null == left) {
                    return right;
                } else {
                    return absSpread(left) < absSpread(right) ? left : right;
                }
            }
            
            private int absSpread(Object obj) {
                return Math.abs(nthInteger(col1,obj) - nthInteger(col2,obj));
            }
        };
    }

    /**
     * Convert the given Number into an int.
     */    
    private static int toInt(Object obj) {
        return ((Number)obj).intValue();
    }
    
}
