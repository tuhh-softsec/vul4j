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

import org.apache.commons.functor.UnaryFunction;

/**
 * Converts a String value to an Integer, throwing
 * an exception if no such conversion can be made.
 *
 * Trailing, non-{@link Character#isDigit digit} characters
 * are ignored.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class ToInteger implements UnaryFunction<String, Integer> {

    public Integer evaluate(String str) {
        StringBuffer buf = new StringBuffer();
        for (int i=0;i<str.length();i++) {
            if (Character.isDigit(str.charAt(i))) {
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

    public static final ToInteger instance() {
        return INSTANCE;
    }

    private static final ToInteger INSTANCE = new ToInteger();
}