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
package org.apache.commons.digester.annotations;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.digester.Digester;

/**
 * Abstract implementation of Class-&gt;Digester Rules-&gt;parse & confronting.
 *
 * @version $Id$
 * @since 2.1
 */
public abstract class AbstractAnnotatedPojoTestCase extends TestCase {

    private DigesterLoader digesterLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        this.digesterLoader = DigesterLoaderBuilder.byDefaultFactories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        this.digesterLoader = null;
    }

    /**
     * Loads the digester rules parsing the expected object class, parses the
     * XML and verify the digester produces the same result.
     *
     * @param expected the expected object
     * @throws Exception if any error occurs
     */
    public final void verifyExpectedEqualsToParsed(Object expected) throws Exception {
        Class<?> clazz = expected.getClass();

        String resource = clazz.getSimpleName() + ".xml";
        InputStream input = clazz.getResourceAsStream(resource);

        Digester digester = this.digesterLoader.createDigester(clazz);
        this.decorate(digester);

        Object actual = digester.parse(input);

        if (input != null) {
            input.close();
        }

        assertEquals(expected, actual);
    } 

    protected DigesterLoader getDigesterLoader() {
        return this.digesterLoader;
    }

    protected void decorate(Digester digester) {
        // do nothing
    }

}
