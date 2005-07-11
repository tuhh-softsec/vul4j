/*

   Copyright 2000,2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationNameEvent;

/**
 * Class to implement DOM 3 MutationName events.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class DOMMutationNameEvent
        extends DOMMutationEvent
        implements MutationNameEvent {

    /**
     * The node's previous namespace URI.
     */
    protected String prevNamespaceURI;

    /**
     * The node's previous name.
     */
    protected String prevNodeName;

    /**
     * Initializes this MutationNameEvent.
     */
    public void initMutationNameEvent(String typeArg,
                                      boolean canBubbleArg,
                                      boolean cancelableArg,
                                      Node relatedNodeArg,
                                      String prevNamespaceURIArg,
                                      String prevNodeNameArg) {
        initMutationEvent(typeArg,
                          canBubbleArg,
                          cancelableArg,
                          relatedNodeArg,
                          null,
                          null,
                          null,
                          (short) 0);
        prevNamespaceURI = prevNamespaceURIArg;
        prevNodeNameArg = prevNodeNameArg;
    }

    /**
     * Initializes this MutationNameEvent.
     */
    public void initMutationNameEventNS(String namespaceURI,
                                        String typeArg,
                                        boolean canBubbleArg,
                                        boolean cancelableArg,
                                        Node relatedNodeArg,
                                        String prevNamespaceURIArg,
                                        String prevNodeNameArg) {
        initMutationEventNS(namespaceURI,
                            typeArg,
                            canBubbleArg,
                            cancelableArg,
                            relatedNodeArg,
                            null,
                            null,
                            null,
                            (short) 0);
        prevNamespaceURI = prevNamespaceURIArg;
        prevNodeNameArg = prevNodeNameArg;
    }

    /**
     * Gets the node's previous namespace URI.
     */
    public String getPrevNamespaceURI() {
        return prevNamespaceURI;
    }

    /**
     * Gets the node's previous node name.
     */
    public String getPrevNodeName() {
        return prevNodeName;
    }
}
