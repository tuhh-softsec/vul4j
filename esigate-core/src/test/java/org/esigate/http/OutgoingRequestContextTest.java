package org.esigate.http;

import junit.framework.TestCase;

/**
 * Created by alexis on 02/11/14.
 */
public class OutgoingRequestContextTest extends TestCase {
    public void testSetRemoveAttribute() throws Exception {

        OutgoingRequestContext outgoingRequestContext = new OutgoingRequestContext();
        String attribute = "test";
        outgoingRequestContext.setAttribute(attribute, 1, true);
        assertEquals(1, outgoingRequestContext.getAttribute(attribute));
        outgoingRequestContext.setAttribute(attribute, 2, true);
        assertEquals(2, outgoingRequestContext.getAttribute(attribute));

        assertEquals(2, outgoingRequestContext.removeAttribute(attribute, true));
        assertEquals(1, outgoingRequestContext.getAttribute(attribute));
        assertEquals(1, outgoingRequestContext.removeAttribute(attribute, true));
        assertNull(outgoingRequestContext.getAttribute(attribute));
        assertNull(outgoingRequestContext.getAttribute(attribute));

        String att1 = "attribute1";
        String att2 = "attribute2";
        outgoingRequestContext.setAttribute(att1, 1, true);
        outgoingRequestContext.setAttribute(att2, 1, true);
        outgoingRequestContext.setAttribute(att2, 2, true);
        assertEquals(1, outgoingRequestContext.getAttribute(att1));
        assertEquals(2, outgoingRequestContext.getAttribute(att2));
        assertEquals(2, outgoingRequestContext.removeAttribute(att2, true));
        assertEquals(1, outgoingRequestContext.removeAttribute(att1, true));

        assertEquals(1, outgoingRequestContext.removeAttribute(att2, true));
        assertNull(outgoingRequestContext.removeAttribute(att2, true));

    }

}
