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
package org.swssf.xmlsec.impl.processor.input;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.swssf.xmlsec.ext.*;
import org.swssf.xmlsec.ext.stax.XMLSecEvent;

import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class LogInputProcessor extends AbstractInputProcessor {

    private static final transient Log logger = LogFactory.getLog(LogInputProcessor.class);

    public LogInputProcessor(XMLSecurityProperties securityProperties) {
        super(securityProperties);
        setPhase(XMLSecurityConstants.Phase.POSTPROCESSING);
    }

    @Override
    public XMLSecEvent processNextHeaderEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        return inputProcessorChain.processHeaderEvent();
    }

    @Override
    public XMLSecEvent processNextEvent(InputProcessorChain inputProcessorChain)
            throws XMLStreamException, XMLSecurityException {
        XMLSecEvent xmlSecEvent = inputProcessorChain.processEvent();
        StringWriter stringWriter = new StringWriter();
        xmlSecEvent.writeAsEncodedUnicode(stringWriter);
        logger.trace(stringWriter.toString());
        return xmlSecEvent;
    }
}
