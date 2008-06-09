/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.example.kata.one;

import org.apache.commons.functor.UnaryFunction;


/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class Product {
    public Product(String name, String sku, int cost) {
        this(name,sku,ToMoney.from(Multiply.by(cost)));
    }

    public Product(String name, String sku, UnaryFunction<? super Integer, Money> price) {
        this.name = name;
        this.sku = sku;
        this.priceFunction = price;
    }

    public String getName() {
        return name;
    }

    public UnaryFunction<? super Integer, Money> getPriceFunction() {
        return priceFunction;
    }

    public String getSku() {
        return sku;
    }

    public void setName(String string) {
        name = string;
    }

    public void setPriceFunction(UnaryFunction<? super Integer, Money> function) {
        priceFunction = function;
    }

    public void setSku(String string) {
        sku = string;
    }

    public Money getPrice(int quantity) {
        return priceFunction.evaluate(quantity);
    }

    private String name;
    private String sku;
    private UnaryFunction<? super Integer, Money> priceFunction;
}
