/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/one/SupermarketPricingExample.java,v 1.1 2003/12/01 05:29:08 rwaldhoff Exp $
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
package org.apache.commons.functor.example.kata.one;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.adapter.IgnoreRightFunction;
import org.apache.commons.functor.core.IdentityFunction;
import org.apache.commons.functor.core.comparator.IsGreaterThan;
import org.apache.commons.functor.core.composite.BinaryCompositeBinaryFunction;
import org.apache.commons.functor.core.composite.CompositeUnaryFunction;
import org.apache.commons.functor.core.composite.ConditionalUnaryFunction;

/**
 * See http://pragprog.com/pragdave/Practices/Kata/KataOne.rdoc,v
 * for more information on this Kata.
 * 
 * @version $Revision: 1.1 $ $Date: 2003/12/01 05:29:08 $
 * @author Rodney Waldhoff
 */
public class SupermarketPricingExample extends TestCase {
    public SupermarketPricingExample(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SupermarketPricingExample.class);
    }
    
    public void setUp() throws Exception {
        super.setUp();
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    // tests
    //----------------------------------------------------------
    
    /*
     * The simplest form of pricing is simply a constant
     * rate.  In Dave's example, a can of beans costs $0.65,
     * and n cans of beans cost n*0.65.
     * 
     * This pricing rule simply multiplies the quantity by
     * a constant, e.g.:
     *   ToMoney.from(Multiply.by(65))
     * 
     * This case is so common, we may want to introduce a
     * special Product constructor to wrap up create the
     * functors for us.
     */
    public void testConstantPricePerUnit() throws Exception {
        {
            Product beans = new Product(
                "Can of Beans", 
                "SKU-0001",
                ToMoney.from(Multiply.by(65)));

            assertEquals(new Money(0*65),beans.getPrice(0));
            assertEquals(new Money(1*65),beans.getPrice(1));
            assertEquals(new Money(2*65),beans.getPrice(2));
            assertEquals(new Money(3*65),beans.getPrice(3));
        }
        // or, using the speical constructor:
        {
            Product beans = new Product(
                "Can of Beans", 
                "SKU-0001",
                65);

            assertEquals(new Money(0*65),beans.getPrice(0));
            assertEquals(new Money(1*65),beans.getPrice(1));
            assertEquals(new Money(2*65),beans.getPrice(2));
            assertEquals(new Money(3*65),beans.getPrice(3));
        }
    }

    /*
     * A slighly more complicated example is a bulk 
     * discount.  For example, bananas may be
     * $0.33 cents each, or 4 for a dollar ($1.00).
     * 
     * This rule is underspecified by itself, there are
     * at least two ways to interpret this pricing rule:
     * 
     * a) the cost is $0.33 cents for 3 or fewer, $0.25 
     *    for 4 or more
     * 
     * or
     *  
     * b) the cost is $1.00 for every group of 4, $0.33
     *    each for anything left over
     * 
     * although I think in practice, "4 for a dollar" 
     * usually means the former and not the latter.
     *
     * We can implement either:
     */    
    public void testFourForADollar_A() throws Exception {
        Product banana = new Product(
            "Banana", 
            "SKU-0002",
            ToMoney.from(
                new ConditionalUnaryFunction(
                    IsGreaterThan.instance(new Integer(3)),
                    Multiply.by(25),
                    Multiply.by(33))));

        assertEquals(new Money(0*33),banana.getPrice(0));
        assertEquals(new Money(1*33),banana.getPrice(1));
        assertEquals(new Money(2*33),banana.getPrice(2));
        assertEquals(new Money(3*33),banana.getPrice(3));
        assertEquals(new Money(4*25),banana.getPrice(4));
        assertEquals(new Money(5*25),banana.getPrice(5));
        assertEquals(new Money(6*25),banana.getPrice(6));
        assertEquals(new Money(7*25),banana.getPrice(7));
        assertEquals(new Money(8*25),banana.getPrice(8));
    }


    public void testFourForADollar_B() throws Exception {
        Product banana = new Product(
            "Banana", 
            "SKU-0002",
            ToMoney.from(
                new BinaryFunctionUnaryFunction(
                    new BinaryCompositeBinaryFunction(
                        Add.instance(),
                        IgnoreRightFunction.adapt(
                            new CompositeUnaryFunction(
                                Multiply.by(100),
                                Divide.by(4))),
                        IgnoreRightFunction.adapt(
                            new CompositeUnaryFunction(
                                Multiply.by(33),
                                Mod.by(4)))))));
        assertEquals(new Money(0*33+0*25),banana.getPrice(0));
        assertEquals(new Money(1*33+0*25),banana.getPrice(1));
        assertEquals(new Money(2*33+0*25),banana.getPrice(2));
        assertEquals(new Money(3*33+0*25),banana.getPrice(3));
        assertEquals(new Money(0*33+4*25),banana.getPrice(4));
        assertEquals(new Money(1*33+4*25),banana.getPrice(5));
        assertEquals(new Money(2*33+4*25),banana.getPrice(6));
        assertEquals(new Money(3*33+4*25),banana.getPrice(7));
        assertEquals(new Money(0*33+8*25),banana.getPrice(8));
    }


    /*
     * Another interesting pricing rule is 
     * something like "buy 2, get 1 free". 
     * 
     * This may be implemented using a formula
     * like:
     *   costPerUnit * (quantity - quantity / 2)
     *  
     * For example...
     */    
    public void testBuyTwoGetOneFree_1() throws Exception {
        Product apple = new Product(
            "Apple", 
            "SKU-0003",
            ToMoney.from(
                new CompositeUnaryFunction(
                    Multiply.by(40),
                    new BinaryFunctionUnaryFunction(
                        new BinaryCompositeBinaryFunction(
                            Subtract.instance(),
                            IgnoreRightFunction.adapt(IdentityFunction.instance()),
                            IgnoreRightFunction.adapt(Divide.by(3)))))));

        assertEquals(new Money(0*40),apple.getPrice(0));
        assertEquals(new Money(1*40),apple.getPrice(1));
        assertEquals(new Money(2*40),apple.getPrice(2));       
        assertEquals(new Money(2*40),apple.getPrice(3)); 
        assertEquals(new Money(3*40),apple.getPrice(4));
        assertEquals(new Money(4*40),apple.getPrice(5));
        assertEquals(new Money(4*40),apple.getPrice(6));
        assertEquals(new Money(5*40),apple.getPrice(7));
        assertEquals(new Money(6*40),apple.getPrice(8));
        assertEquals(new Money(6*40),apple.getPrice(9));
        assertEquals(new Money(7*40),apple.getPrice(10));
    }

    /*
     * ...but our pricing rule is starting to get ugly,
     * and we haven't even considered things 
     * something like "buy 3, get 2 free", etc. 
     * 
     * Perhaps a special UnaryFunction instance is in
     * order:
     */    

    class BuyNGetMFree implements UnaryFunction {
       public BuyNGetMFree(int n, int m, int costPerUnit) {
           this.n = n;
           this.m = m;
           this.costPerUnit = costPerUnit;
       }
        
       public Object evaluate(Object obj) {
           return evaluate((Number)obj);
       }
                    
       public Object evaluate(Number num) {
           int quantity = num.intValue();
           int cost = 0;
           
           while(quantity >= n) {
               // buy n
               cost += n * costPerUnit;
               quantity -= n;
               // get m (or fewer) free 
               quantity -= Math.min(quantity,m);                                  
           }
           // buy less than n
           cost += quantity * costPerUnit;
           
           return new Integer(cost);
       }
        
       private int n, m, costPerUnit;        
   }

    public void testBuyTwoGetOneFree_2() throws Exception {
        Product apple = new Product(
            "Apple", 
            "SKU-0003",
            ToMoney.from(new BuyNGetMFree(2,1,40)));
            
        assertEquals(new Money(0*40),apple.getPrice(0));
        assertEquals(new Money(1*40),apple.getPrice(1));
        assertEquals(new Money(2*40),apple.getPrice(2));       
        assertEquals(new Money(2*40),apple.getPrice(3)); 
        assertEquals(new Money(3*40),apple.getPrice(4));
        assertEquals(new Money(4*40),apple.getPrice(5));
        assertEquals(new Money(4*40),apple.getPrice(6));
        assertEquals(new Money(5*40),apple.getPrice(7));
        assertEquals(new Money(6*40),apple.getPrice(8));
        assertEquals(new Money(6*40),apple.getPrice(9));
        assertEquals(new Money(7*40),apple.getPrice(10));
    }

   public void testBuyThreeGetTwoFree() throws Exception {
        Product apple = new Product(
            "Apple", 
            "SKU-0003",
            ToMoney.from(new BuyNGetMFree(3,2,40)));
            
        assertEquals(new Money(0*40),apple.getPrice(0));
        assertEquals(new Money(1*40),apple.getPrice(1));
        assertEquals(new Money(2*40),apple.getPrice(2));       
        assertEquals(new Money(3*40),apple.getPrice(3)); 
        assertEquals(new Money(3*40),apple.getPrice(4));
        assertEquals(new Money(3*40),apple.getPrice(5));
        assertEquals(new Money(4*40),apple.getPrice(6));
        assertEquals(new Money(5*40),apple.getPrice(7));
        assertEquals(new Money(6*40),apple.getPrice(8));
        assertEquals(new Money(6*40),apple.getPrice(9));
        assertEquals(new Money(6*40),apple.getPrice(10));
        assertEquals(new Money(7*40),apple.getPrice(11));
    }

    public void testBuyTwoGetFiveFree() throws Exception {
         Product apple = new Product(
             "Apple", 
             "SKU-0003",
             ToMoney.from(new BuyNGetMFree(2,5,40)));
            
         assertEquals(new Money(0*40),apple.getPrice(0));
         assertEquals(new Money(1*40),apple.getPrice(1));
         assertEquals(new Money(2*40),apple.getPrice(2));       
         assertEquals(new Money(2*40),apple.getPrice(3)); 
         assertEquals(new Money(2*40),apple.getPrice(4));
         assertEquals(new Money(2*40),apple.getPrice(5));
         assertEquals(new Money(2*40),apple.getPrice(6));
         assertEquals(new Money(2*40),apple.getPrice(7));
         assertEquals(new Money(3*40),apple.getPrice(8));
         assertEquals(new Money(4*40),apple.getPrice(9));
         assertEquals(new Money(4*40),apple.getPrice(10));
         assertEquals(new Money(4*40),apple.getPrice(11));
         assertEquals(new Money(4*40),apple.getPrice(12));
         assertEquals(new Money(4*40),apple.getPrice(13));
         assertEquals(new Money(4*40),apple.getPrice(14));
         assertEquals(new Money(5*40),apple.getPrice(15));
     }
}
