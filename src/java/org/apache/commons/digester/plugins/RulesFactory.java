/* $Id: RulesFactory.java,v 1.1 2004/06/11 03:49:55 skitching Exp $
 *
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;

/**
 * Whenever the scope of a plugin tag is entered, the PluginRules class
 * creates a new Rules instance and configures it with the appropriate
 * parsing rules for the plugged-in class.
 * <p>
 * Users of the plugins module can specify a subclass of this one to
 * control the creation of that Rules object. In particular, it can
 * set up default rules within the returned instance which are applicable
 * to all plugged-in classes.
 *
 * @since 1.6
 */

public abstract class RulesFactory {

    /**
     * Return an instance of some Rules implementation that the plugged-in
     * class shall use to match its private parsing rules.
     * <p>
     * @param d is the digester that the returned rules object will be 
     * associated with.
     *
     * @param pluginClass is the class that is to be configured using rules
     * added to the returnedobject.
     * 
     * @throws PluginException if the algorithm finds a source
     * of rules, but there is something invalid about that source.
     */

     public abstract Rules newRules(Digester d, Class pluginClass) 
                        throws PluginException;
}

