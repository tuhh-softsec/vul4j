package org.apache.commons.digester3;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.xml.sax.Attributes;

/**
 * <p>
 * (Logical) Interface for substitution strategies. (It happens to be implemented as a Java abstract class to allow
 * future additions to be made without breaking backwards compatibility.)
 * </p>
 * <p>
 * Usage: When {@link Digester#setSubstitutor} is set, <code>Digester</code> calls the methods in this interface to
 * create substitute values which will be passed into the Rule implementations. Of course, it is perfectly acceptable
 * for implementations not to make substitutions and simply return the inputs.
 * </p>
 * <p>
 * Different strategies are supported for attributes and body text.
 * </p>
 * 
 * @since 1.6
 */
public abstract class Substitutor
{

    /**
     * <p>
     * Substitutes the attributes (before they are passed to the <code>Rule</code> implementations's).
     * </p>
     * <p>
     * <code>Digester</code> will only call this method a second time once the original <code>Attributes</code> instance
     * can be safely reused. The implementation is therefore free to reuse the same <code>Attributes</code> instance for
     * all calls.
     * </p>
     * 
     * @param attributes the <code>Attributes</code> passed into <code>Digester</code> by the SAX parser, not null (but
     *            may be empty)
     * @return <code>Attributes</code> to be passed to the <code>Rule</code> implementations. This method may pass back
     *         the Attributes passed in. Not null but possibly empty.
     */
    public abstract Attributes substitute( Attributes attributes );

    /**
     * Substitutes for the body text. This method may substitute values into the body text of the elements that Digester
     * parses.
     * 
     * @param bodyText the body text (as passed to <code>Digester</code>)
     * @return the body text to be passed to the <code>Rule</code> implementations
     */
    public abstract String substitute( String bodyText );

}
