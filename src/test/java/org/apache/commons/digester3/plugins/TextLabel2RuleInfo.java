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

package org.apache.commons.digester3.plugins;

import org.apache.commons.digester3.Digester;

public class TextLabel2RuleInfo
{
    // define default rules
    public static void addRules( Digester digester, String pattern )
    {
        digester.addCallMethod( pattern + "/id", "setId", 0 );
        digester.addCallMethod( pattern + "/label", "setLabel", 0 );
    }

    // define different rules on this class
    public static void addAltRules( Digester digester, String pattern )
    {
        digester.addCallMethod( pattern + "/alt-id", "setId", 0 );
        digester.addCallMethod( pattern + "/alt-label", "setLabel", 0 );
    }
}
