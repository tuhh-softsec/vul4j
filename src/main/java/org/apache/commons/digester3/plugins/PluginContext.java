package org.apache.commons.digester3.plugins;

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

import java.util.List;
import java.util.LinkedList;

import org.apache.commons.digester3.plugins.strategies.FinderFromClass;
import org.apache.commons.digester3.plugins.strategies.FinderFromDfltClass;
import org.apache.commons.digester3.plugins.strategies.FinderFromDfltMethod;
import org.apache.commons.digester3.plugins.strategies.FinderFromDfltResource;
import org.apache.commons.digester3.plugins.strategies.FinderFromFile;
import org.apache.commons.digester3.plugins.strategies.FinderFromMethod;
import org.apache.commons.digester3.plugins.strategies.FinderFromResource;
import org.apache.commons.digester3.plugins.strategies.FinderSetProperties;

/**
 * Provides data and services which should exist only once per digester.
 * <p>
 * This class holds a number of useful items which should be shared by all plugin objects. Such data cannot be stored on
 * the PluginRules or PluginManager classes, as there can be multiple instances of these at various times during a
 * parse.
 * <p>
 * The name "Context" refers to the similarity between this class and a ServletContext class in a servlet engine. A
 * ServletContext object provides access to the container's services such as obtaining global configuration parameters
 * for the container, or getting access to logging services. For plugins, a Digester instance can be regarded as
 * "the container".
 * 
 * @since 1.6
 */
public class PluginContext
{

    private static final String DFLT_PLUGIN_CLASS_ATTR_NS = null;

    private static final String DFLT_PLUGIN_CLASS_ATTR = "plugin-class";

    // the xml attribute the user uses on an xml element to specify
    // the plugin's class
    private static final String DFLT_PLUGIN_ID_ATTR_NS = null;

    private static final String DFLT_PLUGIN_ID_ATTR = "plugin-id";

    /** See {@link #setPluginClassAttribute}. */
    private String pluginClassAttrNs = DFLT_PLUGIN_CLASS_ATTR_NS;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginClassAttr = DFLT_PLUGIN_CLASS_ATTR;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginIdAttrNs = DFLT_PLUGIN_ID_ATTR_NS;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginIdAttr = DFLT_PLUGIN_ID_ATTR;

    /**
     * A list of RuleFinder objects used by all Declarations (and thus indirectly by all PluginCreateRules to locate the
     * custom rules for plugin classes.
     */
    private List<RuleFinder> ruleFinders;

    // ------------------- methods ---------------------------------------

    /**
     * Return the list of RuleFinder objects. Under normal circumstances this method creates a default list of these
     * objects when first called (ie "on-demand" or "lazy initialization"). However if setRuleFinders has been called
     * first, then the list specified there is returned.
     * <p>
     * It is explicitly permitted for the caller to modify this list by inserting or removing RuleFinder objects.
     *
     * @return the list of RuleFinder objects
     */
    public List<RuleFinder> getRuleFinders()
    {
        if ( ruleFinders == null )
        {
            // when processing a plugin declaration, attempts are made to
            // find custom rules in the order in which the Finder objects
            // are added below. However this list can be modified
            ruleFinders = new LinkedList<RuleFinder>();
            ruleFinders.add( new FinderFromFile() );
            ruleFinders.add( new FinderFromResource() );
            ruleFinders.add( new FinderFromClass() );
            ruleFinders.add( new FinderFromMethod() );
            ruleFinders.add( new FinderFromDfltMethod() );
            ruleFinders.add( new FinderFromDfltClass() );
            ruleFinders.add( new FinderFromDfltResource() );
            ruleFinders.add( new FinderFromDfltResource( ".xml" ) );
            ruleFinders.add( new FinderSetProperties() );
        }
        return ruleFinders;
    }

    /**
     * Set the list of RuleFinder objects. This may be useful if working in a non-english language, allowing the
     * application developer to replace the standard list with a list of objects which look for xml attributes in the
     * local language.
     * <p>
     * If the intent is just to add an additional rule-finding algorithm, then it may be better to call #getRuleFinders,
     * and insert a new object into the start of the list.
     *
     * @param ruleFinders the list of RuleFinder objects
     */
    public void setRuleFinders( List<RuleFinder> ruleFinders )
    {
        this.ruleFinders = ruleFinders;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a PluginCreateRule which class should be
     * instantiated.
     * <p>
     * Example:
     * 
     * <pre>
     * setPluginClassAttribute( null, &quot;class&quot; );
     * </pre>
     * 
     * will allow this in the input xml:
     * 
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin class="com.acme.widget"&gt; ......
     * </pre>
     * 
     * instead of the default syntax:
     * 
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin plugin-class="com.acme.widget"&gt; ......
     * </pre>
     * 
     * This is particularly useful if the input xml document is not in English.
     * <p>
     * Note that the xml attributes used by PluginDeclarationRules are not affected by this method.
     * 
     * @param namespaceUri is the namespace uri that the specified attribute is in. If the attribute is in no namespace,
     *            then this should be null. Note that if a namespace is used, the attrName value should <i>not</i>
     *            contain any kind of namespace-prefix. Note also that if you are using a non-namespace-aware parser,
     *            this parameter <i>must</i> be null.
     * @param attrName is the attribute whose value contains the name of the class to be instantiated.
     */
    public void setPluginClassAttribute( String namespaceUri, String attrName )
    {
        pluginClassAttrNs = namespaceUri;
        pluginClassAttr = attrName;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a PluginCreateRule which plugin declaration is
     * being referenced.
     * <p>
     * Example:
     * 
     * <pre>
     * setPluginIdAttribute( null, &quot;id&quot; );
     * </pre>
     * 
     * will allow this in the input xml:
     * 
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin id="widget"&gt; ......
     * </pre>
     * 
     * rather than the default behaviour:
     * 
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin plugin-id="widget"&gt; ......
     * </pre>
     * 
     * This is particularly useful if the input xml document is not in English.
     * <p>
     * Note that the xml attributes used by PluginDeclarationRules are not affected by this method.
     * 
     * @param namespaceUri is the namespace uri that the specified attribute is in. If the attribute is in no namespace,
     *            then this should be null. Note that if a namespace is used, the attrName value should <i>not</i>
     *            contain any kind of namespace-prefix. Note also that if you are using a non-namespace-aware parser,
     *            this parameter <i>must</i> be null.
     * @param attrName is the attribute whose value contains the id of the plugin declaration to be used when
     *            instantiating an object.
     */
    public void setPluginIdAttribute( String namespaceUri, String attrName )
    {
        pluginIdAttrNs = namespaceUri;
        pluginIdAttr = attrName;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a PluginCreateRule which class is to be plugged in.
     * <p>
     * May be null (in fact, normally will be).
     *
     * @return the namespace for the xml attribute which indicates which class is to be plugged in.
     */
    public String getPluginClassAttrNs()
    {
        return pluginClassAttrNs;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a PluginCreateRule which class is to be plugged in.
     * <p>
     * The return value is never null.
     *
     * @return the namespace for the xml attribute which indicates which class is to be plugged in.
     */
    public String getPluginClassAttr()
    {
        return pluginClassAttr;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a PluginCreateRule which previous plugin declaration
     * should be used.
     * <p>
     * May be null (in fact, normally will be).
     *
     * @return the namespace for the xml attribute which indicates which previous plugin declaration should be used.
     */
    public String getPluginIdAttrNs()
    {
        return pluginIdAttrNs;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a PluginCreateRule which previous plugin declaration
     * should be used.
     * <p>
     * The return value is never null.
     *
     * @return the namespace for the xml attribute which indicates which previous plugin declaration should be used.
     */
    public String getPluginIdAttr()
    {
        return pluginIdAttr;
    }

}
