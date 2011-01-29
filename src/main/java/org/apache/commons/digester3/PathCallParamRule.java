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
package org.apache.commons.digester3;

import org.xml.sax.Attributes;

/**
 * <p>Rule implementation that saves a parameter containing the 
 * <code>Digester</code> matching path for use by a surrounding 
 * <code>CallMethodRule</code>. This Rule is most useful when making 
 * extensive use of wildcards in rule patterns.</p>
 */
public class PathCallParamRule extends Rule {

    /**
     * The zero-relative index of the parameter we are saving.
     */
    private final int paramIndex;

    /**
     * Construct a "call parameter" rule that will save the body text of this
     * element as the parameter value.
     *
     * @param paramIndex The zero-relative parameter number
     */
    public PathCallParamRule(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    /**
     * Process the start of this element.
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     * @param attributes The attribute list for this element
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String param = getDigester().getMatch();

        if (param != null) {
            Object parameters[] = (Object[]) this.getDigester().peekParams();
            parameters[this.paramIndex] = param;
        }
    }

    /**
     * Render a printable version of this Rule.
     */
    @Override
    public String toString() {
        return String.format("PathCallParamRule[paramIndex=%s]", this.paramIndex);
    }

}
