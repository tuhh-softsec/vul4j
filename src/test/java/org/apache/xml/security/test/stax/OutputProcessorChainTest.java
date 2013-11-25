/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.test.stax;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.impl.OutboundSecurityContextImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.OutputProcessorChainImpl;

import javax.xml.stream.XMLStreamException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class OutputProcessorChainTest extends org.junit.Assert {

    @Before
    public void setUp() throws Exception {
        Init.init(this.getClass().getClassLoader().getResource("security-config.xml").toURI());
    }

    abstract class AbstractOutputProcessor implements OutputProcessor {

        private XMLSecurityConstants.Phase phase = XMLSecurityConstants.Phase.PROCESSING;
        private Set<Object> beforeProcessors = new HashSet<Object>();
        private Set<Object> afterProcessors = new HashSet<Object>();

        @Override
        public void setXMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        }

        @Override
        public void setAction(XMLSecurityConstants.Action action) {
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        }

        @Override
        public void addBeforeProcessor(Object processor) {
            this.beforeProcessors.add(processor);
        }

        @Override
        public Set<Object> getBeforeProcessors() {
            return beforeProcessors;
        }

        @Override
        public void addAfterProcessor(Object processor) {
            this.afterProcessors.add(processor);
        }

        @Override
        public Set<Object> getAfterProcessors() {
            return afterProcessors;
        }

        @Override
        public XMLSecurityConstants.Phase getPhase() {
            return phase;
        }

        public void setPhase(XMLSecurityConstants.Phase phase) {
            this.phase = phase;
        }

        @Override
        public void processNextEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        }

        @Override
        public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        }
    }

    @Test
    public void testAddProcessorPhase1() {
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(new OutboundSecurityContextImpl());

        AbstractOutputProcessor outputProcessor1 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor1);

        AbstractOutputProcessor outputProcessor2 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor2);

        AbstractOutputProcessor outputProcessor3 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor3);

        Assert.assertEquals(outputProcessorChain.getProcessors().get(0), outputProcessor1);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(1), outputProcessor2);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(2), outputProcessor3);
    }

    @Test
    public void testAddProcessorPhase2() {
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(new OutboundSecurityContextImpl());

        AbstractOutputProcessor outputProcessor1 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor1);

        AbstractOutputProcessor outputProcessor2 = new AbstractOutputProcessor() {
        };
        outputProcessor2.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor2);

        AbstractOutputProcessor outputProcessor3 = new AbstractOutputProcessor() {
        };
        outputProcessor3.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor3);

        AbstractOutputProcessor outputProcessor4 = new AbstractOutputProcessor() {
        };
        outputProcessor4.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor4);

        AbstractOutputProcessor outputProcessor5 = new AbstractOutputProcessor() {
        };
        outputProcessor5.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor5);

        AbstractOutputProcessor outputProcessor6 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor6);

        Assert.assertEquals(outputProcessorChain.getProcessors().get(0), outputProcessor2);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(1), outputProcessor5);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(2), outputProcessor1);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(3), outputProcessor6);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(4), outputProcessor3);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(5), outputProcessor4);
    }

    @Test
    public void testAddProcessorBefore1() {
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(new OutboundSecurityContextImpl());

        AbstractOutputProcessor outputProcessor1 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor1);

        AbstractOutputProcessor outputProcessor2 = new AbstractOutputProcessor() {
        };
        outputProcessor2.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor2);

        AbstractOutputProcessor outputProcessor3 = new AbstractOutputProcessor() {
        };
        outputProcessor3.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor3);

        AbstractOutputProcessor outputProcessor4 = new AbstractOutputProcessor() {
        };
        outputProcessor4.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessor4.addBeforeProcessor(outputProcessor3.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor4);

        AbstractOutputProcessor outputProcessor5 = new AbstractOutputProcessor() {
        };
        outputProcessor5.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessor5.addBeforeProcessor(outputProcessor2.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor5);

        AbstractOutputProcessor outputProcessor6 = new AbstractOutputProcessor() {
        };
        outputProcessor6.addBeforeProcessor(outputProcessor1.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor6);

        Assert.assertEquals(outputProcessorChain.getProcessors().get(0), outputProcessor5);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(1), outputProcessor2);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(2), outputProcessor6);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(3), outputProcessor1);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(4), outputProcessor4);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(5), outputProcessor3);
    }

    @Test
    public void testAddProcessorAfter1() {
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(new OutboundSecurityContextImpl());

        AbstractOutputProcessor outputProcessor1 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor1);

        AbstractOutputProcessor outputProcessor2 = new AbstractOutputProcessor() {
        };
        outputProcessor2.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor2);

        AbstractOutputProcessor outputProcessor3 = new AbstractOutputProcessor() {
        };
        outputProcessor3.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessorChain.addProcessor(outputProcessor3);

        AbstractOutputProcessor outputProcessor4 = new AbstractOutputProcessor() {
        };
        outputProcessor4.setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
        outputProcessor4.addAfterProcessor(outputProcessor3.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor4);

        AbstractOutputProcessor outputProcessor5 = new AbstractOutputProcessor() {
        };
        outputProcessor5.setPhase(XMLSecurityConstants.Phase.PREPROCESSING);
        outputProcessor5.addAfterProcessor(outputProcessor2.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor5);

        AbstractOutputProcessor outputProcessor6 = new AbstractOutputProcessor() {
        };
        outputProcessor6.addAfterProcessor(outputProcessor1.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor6);

        Assert.assertEquals(outputProcessorChain.getProcessors().get(0), outputProcessor2);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(1), outputProcessor5);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(2), outputProcessor1);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(3), outputProcessor6);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(4), outputProcessor3);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(5), outputProcessor4);
    }

    @Test
    public void testAddProcessorBeforeAndAfter1() {
        OutputProcessorChainImpl outputProcessorChain = new OutputProcessorChainImpl(new OutboundSecurityContextImpl());

        AbstractOutputProcessor outputProcessor1 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor1);

        AbstractOutputProcessor outputProcessor2 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor2);

        AbstractOutputProcessor outputProcessor3 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor3);

        AbstractOutputProcessor outputProcessor4 = new AbstractOutputProcessor() {
        };
        outputProcessorChain.addProcessor(outputProcessor4);

        AbstractOutputProcessor outputProcessor5 = new AbstractOutputProcessor() {
        };
        outputProcessor5.addBeforeProcessor("");
        outputProcessor5.addAfterProcessor(outputProcessor3.getClass().getName());
        outputProcessorChain.addProcessor(outputProcessor5);

        AbstractOutputProcessor outputProcessor6 = new AbstractOutputProcessor() {
        };
        outputProcessor6.addBeforeProcessor(outputProcessor5.getClass().getName());
        outputProcessor6.addAfterProcessor("");
        outputProcessorChain.addProcessor(outputProcessor6);

        Assert.assertEquals(outputProcessorChain.getProcessors().get(0), outputProcessor1);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(1), outputProcessor2);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(2), outputProcessor3);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(3), outputProcessor6);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(4), outputProcessor5);
        Assert.assertEquals(outputProcessorChain.getProcessors().get(5), outputProcessor4);
    }
}
