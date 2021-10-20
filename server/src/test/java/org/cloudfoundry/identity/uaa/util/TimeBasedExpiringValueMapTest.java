/*
 * ****************************************************************************
 *     Cloud Foundry
 *     Copyright (c) [2009-2017] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 * ****************************************************************************
 */

package org.cloudfoundry.identity.uaa.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class TimeBasedExpiringValueMapTest {

    public static final int TIMEOUT = 5;
    private MockTimeService timeService = new MockTimeService();
    private TimeBasedExpiringValueMap<String,Object> map;
    private RandomValueStringGenerator generator = new RandomValueStringGenerator();
    private String key1 = generator.generate(), key2 = generator.generate();
    private Object value1 = new Object(), value2 = new Object();

    @Before
    public void setUp() throws Exception {
        map = new TimeBasedExpiringValueMap<>(timeService, TIMEOUT);
    }

    @Test
    public void no_value() throws Exception {
        assertNull(map.get(generator.generate()));
    }

    @Test
    public void put_then_get() throws Exception {
        map.put(key1, value1);
        assertSame(value1, map.get(key1));
    }

    @Test
    public void clear() throws Exception {
        map.put(key1, value1);
        assertNotNull(map.get(key1));
        assertEquals(1, map.size());
        map.clear();
        assertNull(map.get(key1));
        assertEquals(0, map.size());
    }

    @Test
    public void expire_on_get() throws Exception {
        map.put(key1, value1);
        timeService.addAndGet(TIMEOUT*2);
        assertEquals(1, map.size());
        assertSame(value1, map.get(key1));
        assertEquals(0, map.size());
        assertNull(map.get(key1));
    }

    @Test
    public void expire_on_put() throws Exception {
        map.put(key1, value1);
        assertEquals(1, map.size());
        timeService.addAndGet(TIMEOUT*2);
        map.put(key2, value2);
        assertEquals(1, map.size());
    }

    @Test
    public void remove() throws Exception {
        map.put(key1, value1);
        assertSame(value1, map.remove(key1));
        assertEquals(0, map.size());
    }

    @Test
    public void non_existent_remove() throws Exception {
        assertNull(map.remove("does-not-exist"));
    }

}
