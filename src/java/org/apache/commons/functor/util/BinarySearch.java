/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/util/Attic/BinarySearch.java,v 1.5 2003/12/01 07:32:54 rwaldhoff Exp $
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

package org.apache.commons.functor.util;

import org.apache.commons.functor.RecursiveFunction;

import java.util.List;

/**
 * Recursive binary search function. The {@link #evaluate} method returns either
 * the next recursive BinarySearch call, or the position of the item found.
 * Either use the {@link #recurse} call, the static {@link #execute} call, or
 * the {@link org.apache.commons.functor.Algorithms#recurse} call to execute the
 * search.
 *
 * @version $Revision: 1.5 $ $Date: 2003/12/01 07:32:54 $
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public class BinarySearch extends RecursiveFunction {
// TODO: should have explict null strategy


   // constructors
   //---------------------------------------------------------------

    public BinarySearch(List list, Comparable item) {
        this.list = list; this.item = item;
        this.lower = 0; this.upper = list.size();
    }

    public BinarySearch(List list, Comparable item, int lower, int upper) {
        this.list = list; this.item = item;
        this.lower = lower; this.upper = upper;
    }

    // instance methods variables
    //---------------------------------------------------------------

    /**
     * Either returns the next BinarySearch function or position of the item
     * when it is found. If the item is not found -1 (as Integer) is returned.
     */
    public Object evaluate() {
        // TODO: should be using compareTo instead of equals
        if (lower == upper) {
            if(upper >= list.size()) {
                return new Integer(-1);
            } else if(list.get(upper).equals(item)) {
                return new Integer(upper);
            } else {
                return new Integer(-1);
            }
        } else {
            int middle = (lower + upper) / 2;
            if (item.compareTo(list.get(middle)) > 0) {
                return new BinarySearch(list, item, middle+1, upper);
            } else {
                return new BinarySearch(list, item, lower, middle);
            }
        }
    }

    // class methods
    //---------------------------------------------------------------

    public static int execute(List list, Comparable item) {
        return ((Number)(new BinarySearch(list, item)).recurse()).intValue();
    }

    // private variables
    //---------------------------------------------------------------
    
    private List list = null;
    private Comparable item = null;
    private int lower = 0;
    private int upper = 0;
}