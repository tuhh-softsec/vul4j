/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.configuration.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseHierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.junit.Test;

/**
 * Test class for the events generated by hierarchical configurations.
 *
 * @version $Id$
 */
public class TestHierarchicalConfigurationEvents extends
        AbstractTestConfigurationEvents
{
    @Override
    protected AbstractConfiguration createConfiguration()
    {
        return new BaseHierarchicalConfiguration();
    }

    /**
     * Tests events generated by the clearTree() method.
     */
    @Test
    public void testClearTreeEvent()
    {
        HierarchicalConfiguration hc = (HierarchicalConfiguration) config;
        String key = EXIST_PROPERTY.substring(0, EXIST_PROPERTY.indexOf('.'));
        Collection<ConfigurationNode> nodes = hc.getExpressionEngine()
                .query(hc.getRootNode(), key);
        hc.clearTree(key);
        l.checkEvent(BaseHierarchicalConfiguration.EVENT_CLEAR_TREE, key, null,
                true);
        l.checkEvent(BaseHierarchicalConfiguration.EVENT_CLEAR_TREE, key, nodes,
                false);
        l.done();
    }

    /**
     * Tests events generated by the addNodes() method.
     */
    @Test
    public void testAddNodesEvent()
    {
        HierarchicalConfiguration hc = (HierarchicalConfiguration) config;
        Collection<ConfigurationNode> nodes = new ArrayList<ConfigurationNode>(1);
        nodes.add(new DefaultConfigurationNode("a_key", TEST_PROPVALUE));
        hc.addNodes(TEST_PROPNAME, nodes);
        l.checkEvent(BaseHierarchicalConfiguration.EVENT_ADD_NODES, TEST_PROPNAME,
                nodes, true);
        l.checkEvent(BaseHierarchicalConfiguration.EVENT_ADD_NODES, TEST_PROPNAME,
                nodes, false);
        l.done();
    }

    /**
     * Tests events generated by addNodes() when the list of nodes is empty. In
     * this case no events should be generated.
     */
    @Test
    public void testAddNodesEmptyEvent()
    {
        ((HierarchicalConfiguration) config).addNodes(TEST_PROPNAME,
                new ArrayList<ConfigurationNode>());
        l.done();
    }

    /**
     * Tests whether manipulations of a subnode configuration trigger correct
     * events.
     */
    @Test
    public void testSubnodeChangedEvent()
    {
        SubnodeConfiguration sub = ((HierarchicalConfiguration) config)
                .configurationAt(EXIST_PROPERTY);
        sub.addProperty("newProp", "newValue");
        checkSubnodeEvent(l
                .nextEvent(BaseHierarchicalConfiguration.EVENT_SUBNODE_CHANGED),
                true);
        checkSubnodeEvent(l
                .nextEvent(BaseHierarchicalConfiguration.EVENT_SUBNODE_CHANGED),
                false);
        l.done();
    }

    /**
     * Tests whether a received event contains a correct subnode event.
     *
     * @param event the event object
     * @param before the expected before flag
     */
    private void checkSubnodeEvent(ConfigurationEvent event, boolean before)
    {
        assertEquals("Wrong before flag of nesting event", before, event
                .isBeforeUpdate());
        assertTrue("No subnode event found in value",
                event.getPropertyValue() instanceof ConfigurationEvent);
        ConfigurationEvent evSub = (ConfigurationEvent) event
                .getPropertyValue();
        assertEquals("Wrong event type",
                BaseHierarchicalConfiguration.EVENT_ADD_PROPERTY, evSub.getType());
        assertEquals("Wrong property name", "newProp", evSub.getPropertyName());
        assertEquals("Wrong property value", "newValue", evSub
                .getPropertyValue());
        assertEquals("Wrong before flag", before, evSub.isBeforeUpdate());
    }
}
