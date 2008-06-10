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
package org.apache.commons.functor.example.map;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.BinaryProcedureBinaryFunction;
import org.apache.commons.functor.core.composite.ConditionalBinaryFunction;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class PredicatedMap extends FunctoredMap {
    public PredicatedMap(Map map, final UnaryPredicate keyPredicate, final UnaryPredicate valuePredicate) {
        super(map);
        setOnPut(new ConditionalBinaryFunction(
            new BinaryPredicate() {
                public boolean test(Object a, Object b) {
                    return keyPredicate.test(Array.get(b,0)) &&
                        valuePredicate.test(Array.get(b,1));
                }
            },
            DEFAULT_ON_PUT,
            BinaryProcedureBinaryFunction.adapt(new Throw(new IllegalArgumentException()))));

        setOnPutAll(new BinaryProcedure() {
            public void run(Object d, Object s) {
                Map dest = (Map) d;
                Map src = (Map) s;
                for (Iterator iter = src.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry pair = (Map.Entry) iter.next();
                    if (keyPredicate.test(pair.getKey()) &&
                        valuePredicate.test(pair.getValue())) {
                        dest.put(pair.getKey(),pair.getValue());
                    }
                }
            }
        });
    }
}
