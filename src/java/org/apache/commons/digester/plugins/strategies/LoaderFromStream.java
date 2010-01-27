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
 
package org.apache.commons.digester.plugins.strategies;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.xml.sax.InputSource;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.xmlrules.FromXmlRuleSet;
import org.apache.commons.logging.Log;

/**
 * A rule-finding algorithm which loads an xmlplugins-format file.
 * <p>
 * Note that the "include" feature of xmlrules is not supported.
 *
 * @since 1.6
 */

public class LoaderFromStream extends RuleLoader {

    private byte[] input;
    private FromXmlRuleSet ruleSet;
    
    /** See {@link #load}. */
    public LoaderFromStream(InputStream s) throws Exception {
        load(s);
    }

    /**
     * The contents of the input stream are loaded into memory, and
     * cached for later use.
     * <p>
     * The caller is responsible for closing the input stream after this
     * method has returned.
     */
    private void load(InputStream s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[256];
        for(;;) {
            int i = s.read(buf);
            if (i == -1)
                break;
            baos.write(buf, 0, i);
        }
        input = baos.toByteArray();
    }
    
    /**
     * Add the rules previously loaded from the input stream into the
     * specified digester.
     */
    @Override
    public void addRules(Digester d, String path) throws PluginException {
        Log log = d.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug(
                "LoaderFromStream: loading rules for plugin at path [" 
                + path + "]");
        }

        // Note that this input-source doesn't have any idea of its
        // system id, so it has no way of resolving relative URLs
        // such as the "include" feature of xmlrules. This is ok,
        // because that doesn't work well with our approach of
        // caching the input data in memory anyway.

        InputSource source = new InputSource(new ByteArrayInputStream(input));
        FromXmlRuleSet ruleSet = new FromXmlRuleSet(source);
        ruleSet.addRuleInstances(d, path);
    }
}

