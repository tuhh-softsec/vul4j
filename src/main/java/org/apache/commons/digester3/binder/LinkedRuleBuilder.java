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
package org.apache.commons.digester3.binder;

/**
 * Builder invoked to bind one or more rules to a pattern.
 *
 * @since 3.0
 */
public final class LinkedRuleBuilder
{

    private String namespaceURI;

    /**
     * Sets the namespace URI for the current rule pattern.
     *
     * @param namespaceURI the namespace URI associated to the rule pattern.
     * @return this {@link LinkedRuleBuilder} instance
     */
    public LinkedRuleBuilder withNamespaceURI( /* @Nullable */String namespaceURI )
    {
        if ( namespaceURI == null || namespaceURI.length() > 0 )
        {
            this.namespaceURI = namespaceURI;
        }
        else
        {
            // ignore empty namespaces, null is better
            this.namespaceURI = null;
        }

        return this;
    }

}
