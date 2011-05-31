package org.apache.commons.digester3.plugins.strategies;

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

import org.apache.commons.logging.Log;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * A RuleLoader which creates a single SetPropertiesRule and adds it to the digester when its addRules() method is
 * invoked.
 * <p>
 * This loader ensures that any xml attributes on the plugin tag get mapped to equivalent properties on a javabean. This
 * allows JavaBean classes to be used as plugins without any requirement to create custom plugin rules.
 * 
 * @since 1.6
 */
public class LoaderSetProperties
    extends RuleLoader
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRules( Digester digester, String path )
    {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if ( debug )
        {
            log.debug( "LoaderSetProperties loading rules for plugin at path [" + path + "]" );
        }

        digester.addSetProperties( path );
    }

}
