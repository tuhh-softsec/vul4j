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
package org.apache.commons.functor.example.kata.four;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.adapter.BinaryFunctionUnaryFunction;
import org.apache.commons.functor.core.IsNull;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;
import org.apache.commons.functor.core.algorithm.FoldLeft;
import org.apache.commons.functor.core.comparator.IsLessThan;
import org.apache.commons.functor.core.composite.Composite;
import org.apache.commons.functor.core.composite.Conditional;
import org.apache.commons.functor.core.composite.ConditionalBinaryFunction;
import org.apache.commons.functor.example.kata.one.Subtract;
import org.apache.commons.functor.example.lines.Lines;
import org.apache.commons.functor.generator.FilteredGenerator;

/**
 * The real workhorse of this Kata excercise.
 *
 * DataMunger wires together various functors and exposes them
 * as static utility methhods.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class DataMunger {
	/** See {@link #process(Reader,int,int,int)} */
    public static final Object process(final InputStream file, final int selected, final int col1, final int col2) {
        return process(new InputStreamReader(file),selected,col1,col2);
    }

	/**
	 * Processes each line of the given Reader, returning the <i>selected</i> column for the
	 * line where the absolute difference between the integer value of <i>col1</i> and <i>col2</i>
	 * is least.  Note that lines that don't begin with an Integer are ignored.
	 */
    public static final Object process(final Reader file, final int selected, final int col1, final int col2) {
        return NthColumn.instance(selected).evaluate(
                new FoldLeft<String>(lesserSpread(col1, col2)).evaluate(new FilteredGenerator<String>(Lines.from(file),
                    Composite.predicate(IsInteger.instance(),NthColumn.instance(0)))));
    }

    /**
     * A BinaryFunction that will calculate the absolute
     * difference between col1 and col2 in the given
     * String arguments, and return the argument
     * whose difference is smallest.
     */
    private static final BinaryFunction<String, String, String> lesserSpread(final int col1, final int col2) {
        return new ConditionalBinaryFunction<String, String, String>(
            IsNull.<String>left(),                                 // if left is null
            RightIdentity.<String, String>function(),                      //   return right
            Conditional.function(                          //   else return the parameter with the least spread
                Composite.predicate(                       //     if left is less than right
                    IsLessThan.instance(),
                    absSpread(col1,col2),
                    absSpread(col1,col2)),
                LeftIdentity.<String, String>function(),                   //       return left
                RightIdentity.<String, String>function()                   //       else return right
            )
        );
    }

	/**
	 * A UnaryFunction that returns the absolute value of the difference
	 * between the Integers stored in the <i>col1</i> and <i>col2</i>th
	 * whitespace delimited columns of the input line (a String).
	 */
    private static UnaryFunction<String, Integer> absSpread(final int col1, final int col2) {
        return Composite.function(
            Abs.instance(),
            new BinaryFunctionUnaryFunction<String, Number>(
                Composite.function(
                    Subtract.instance(),
                    Composite.function(ToInteger.instance(),NthColumn.instance(col1)),
                    Composite.function(ToInteger.instance(),NthColumn.instance(col2)))
                ));
    }

}
