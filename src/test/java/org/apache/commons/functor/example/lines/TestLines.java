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
package org.apache.commons.functor.example.lines;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.adapter.ProcedureUnaryProcedure;
import org.apache.commons.functor.core.Offset;
import org.apache.commons.functor.core.algorithm.FoldLeft;
import org.apache.commons.functor.core.collection.Size;
import org.apache.commons.functor.core.composite.UnaryAnd;
import org.apache.commons.functor.core.composite.UnaryNot;
import org.apache.commons.functor.generator.FilteredGenerator;
import org.apache.commons.functor.generator.TransformedGenerator;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestLines extends TestCase {

    public TestLines(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLines.class);
    }

    private Reader reader = null;

    public void setUp() throws Exception {
        super.setUp();
        reader = new StringReader(DOCUMENT);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        reader = null;
    }

    public void testCountCharacters() throws Exception {
        Object result = new FoldLeft<Integer>(Sum.instance()).evaluate(
                new TransformedGenerator<String, Integer>(Lines.from(reader), new Size<String>()));

        assertEquals("Expected 990 characters",new Integer(990),result);
    }

    public void testCountWords() throws Exception {
        Object result = new FoldLeft<Integer>(Sum.instance()).evaluate(
                new TransformedGenerator<String, Integer>(Lines.from(reader),WordCount.instance()));

        assertEquals("Expected 157 words",new Integer(157),result);
    }

    public void testCountLines() throws Exception {
        Count count = new Count();
        Lines
            .from(reader)
                .run(ProcedureUnaryProcedure.adapt(count));

        assertEquals("Expected 16 lines",16,count.getCount());
    }

    public void testCountWordsExcludingComments() throws Exception {
        Object result = new FoldLeft<Integer>(Sum.instance()).evaluate(new TransformedGenerator<String, Integer>(
                new FilteredGenerator<String>(Lines.from(reader), UnaryNot.not(new StartsWith<String>("#"))), WordCount
                        .instance()));

        assertEquals("Expected 90 words",new Integer(90),result);
    }

    public void testCountCommentLines() throws Exception {
        Count count = new Count();
        new FilteredGenerator<String>(Lines.from(reader), new StartsWith<String>("#"))
                    .run(ProcedureUnaryProcedure.<String>adapt(count));

        assertEquals("Expected 6 lines",6,count.getCount());
    }

    public void testFindMatchingLines() throws Exception {
        Collection<String> matches = new FilteredGenerator<String>(Lines.from(reader), new Contains<String>("lo"))
                .toCollection();
        assertEquals("Expected 5 lines",5,matches.size());
    }

    public void testFindMatchingFromTail() throws Exception {
        Collection<String> matches = new FilteredGenerator<String>(Lines.from(reader), new UnaryAnd<String>(new Offset(
                8), new Contains<String>("lo"))).toCollection();
        assertEquals("Expected 2 lines",2,matches.size());
    }


    private static final String DOCUMENT =
        "# Lorem ipsum dolor sit amet, consectetuer adipiscing elit. \n" +
        "# Aliquam erat volutpat. Donec nec eros. Etiam eget tortor eu \n" +
        "tortor rutrum cursus. Pellentesque ornare pretium risus. Nulla \n" +
        "libero pede, blandit nec, rutrum ut, sodales eu, enim. Duis leo. \n" +
        "Nunc non est. Nunc consequat lobortis nisl. Vivamus et tortor in \n" +
        "# nunc euismod elementum. Ut ut dui. Morbi semper, pede eu cursus \n" +
        "# tristique, diam nunc luctus nibh, id tempor justo metus eget lorem.\n" +
        "\n" +
        "Quisque pharetra hendrerit odio. Etiam consequat ante et dui. Etiam \n" +
        "accumsan elit ac augue. Mauris porta pulvinar tellus. Nulla ac enim ac \n"+
        "augue ornare pharetra. Nunc dignissim eros et nibh. Sed justo dolor, \n" +
        "# ullamcorper non, posuere eget, tempus non, ipsum. Praesent at velit. \n" +
        "# Mauris tempor nisl sed tortor. Vestibulum ante ipsum primis in faucibus \n" +
        "orci luctus et ultrices posuere cubilia Curae; Integer mollis malesuada \n" +
        "dolor. Vestibulum cursus, mi in dictum blandit, eros enim convallis wisi, \n" +
        "id ullamcorper metus tortor interdum dui.\n";
}
