/* $Id$
 *
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
package org.apache.commons.digester3.annotations.internal;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.digester3.annotations.FromAnnotationsRuleSet;

/**
 * Simple in-memory LRU cache implementation.
 *
 * @since 2.1
 */
public final class RuleSetCache implements Serializable {

    /**
     * This class serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The fixed cache size.
     */
    private final int cacheSize = 255;

    /**
     * The fixed cache load facor.
     */
    private final float loadFactor = 0.75f;

    /**
     * The fixed cache capacity.
     */
    private final int capacity = (int) Math.ceil(this.cacheSize / this.loadFactor) + 1;

    /**
     * The map that implements the LRU cache.
     */
    private final Map<Class<?>, FromAnnotationsRuleSet> data =
        new LinkedHashMap<Class<?>, FromAnnotationsRuleSet>(capacity, loadFactor) {

        /**
         * This class serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<Class<?>, FromAnnotationsRuleSet> eldest) {
            return size() > cacheSize;
        }
    };

    /**
     * Returns true if this cache contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested.
     * @return true if this map contains a mapping for the specified key, false
     *         otherwise.
     */
    public boolean containsKey(Class<?> key) {
        checkKey(key);
        return this.data.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is cached, or null if this
     * cache contains no mapping for the key.
     *
     * Key parameter must not be null.
     *
     * @param key the key has to be checked it is present, it must not be null.
     * @return the value to which the specified key is cached, null if this
     *         cache contains no mapping for the key.
     */
    public FromAnnotationsRuleSet get(Class<?> key) {
        checkKey(key);
        return this.data.get(key);
    }

    /**
     * Associates the specified value with the specified key in this cache.
     *
     * Key parameter must not be null.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     */
    public void put(Class<?> key, FromAnnotationsRuleSet value) {
        checkKey(key);
        this.data.put(key, value);
    }

    /**
     * Verify that a key is not null.
     *
     * @param <T> the generic key type.
     * @param key the key object.
     */
    private static void checkKey(Class<?> key) {
        if (key == null) {
            throw new IllegalArgumentException("null keys not supported");
        }
    }

}
