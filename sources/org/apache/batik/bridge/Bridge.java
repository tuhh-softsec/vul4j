/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * A tagging interface that all bridges must implement. A bridge is
 * responsible on creating and maintaining an appropriate object
 * according to an Element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface Bridge {

    /**
     * Returns the namespace URI of the element this <tt>Bridge</tt> is
     * dedicated to.
     */
    String getNamespaceURI();

    /**
     * Returns the local name of the element this <tt>Bridge</tt> is dedicated
     * to.
     */
    String getLocalName();

    /**
     * Returns a new instance of this bridge.
     */
    Bridge getInstance();

    /**
     * Returns the handler that is called each time this bridge
     * updates its GVT product.
     */
    BridgeUpdateHandler getBridgeUpdateHandler();

    /**
     * Sets the handler that is used to track each update of this
     * bridge's GVT product.
     *
     * @param handler the handler to call
     * @param handlerKey a private key the handler might use when it registers
     */
    void setBridgeUpdateHandler(BridgeUpdateHandler handler, int handlerKey);

}
