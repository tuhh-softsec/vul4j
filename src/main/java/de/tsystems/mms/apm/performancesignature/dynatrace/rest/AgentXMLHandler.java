/*
 * Copyright (c) 2008-2015, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the dynaTrace software nor the names of its contributors
 *       may be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Collector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapi on 27.10.2014.
 */
public class AgentXMLHandler extends DefaultHandler {
    private final CharArrayWriter contents = new CharArrayWriter();
    private final List<Agent> agents = new ArrayList<Agent>();
    private final List<Collector> collectors = new ArrayList<Collector>();
    private Agent currentAgent = null;
    private Collector currentCollector = null;
    private String currentElement = null;
    private String parentElement = null;

    public List<Agent> getAgents() {
        return this.agents;
    }

    public List<Collector> getCollectors() {
        return this.collectors;
    }

    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attr) {
        this.contents.reset();
        if (localName.equals(Messages.AgentXMLHandler_AttrAgentInformation())) {
            this.parentElement = localName;
            this.currentAgent = new Agent();
            this.agents.add(this.currentAgent);
        } else if (localName.equals(Messages.AgentXMLHandler_AttrAgentProperties())) {
            this.parentElement = localName;
        } else if (localName.equals(Messages.AgentXMLHandler_AttrCollectorInformation())) {
            this.parentElement = localName;
            this.currentCollector = new Collector();
            this.collectors.add(this.currentCollector);
            if (this.currentAgent != null) {
                this.currentAgent.setCollector(this.currentCollector);
            }
        }
        this.currentElement = localName;
    }

    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals(Messages.AgentXMLHandler_AttrCollectorInformation())) {
            this.currentCollector = null;
        }
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.contents.write(ch, start, length);
        if (this.currentCollector != null) {
            this.currentCollector.setValue(this.currentElement, String.copyValueOf(ch, start, length));
        } else if (this.currentAgent != null) {
            this.currentAgent.setValue(this.currentElement, this.parentElement, String.copyValueOf(ch, start, length));
        }
    }
}
