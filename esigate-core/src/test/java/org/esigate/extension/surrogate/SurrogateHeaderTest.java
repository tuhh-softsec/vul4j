package org.esigate.extension.surrogate;

import static org.junit.Assert.assertEquals;

import org.esigate.extension.surrogate.http.SurrogateCapabilitiesHeader;
import org.junit.Test;

public class SurrogateHeaderTest {

    @Test
    public void testParsingAndFormating() {
        SurrogateCapabilitiesHeader h = SurrogateCapabilitiesHeader
                .fromHeaderValue("abc=\"Surrogate/1.0\", def=\"Surrogate/1.0  ESI/1.0 ESI-Inline\"");

        assertEquals(2, h.getSurrogates().size());
        assertEquals("abc", h.getSurrogates().get(0).getDeviceToken());
        assertEquals("def", h.getSurrogates().get(1).getDeviceToken());
        assertEquals(1, h.getSurrogates().get(0).getCapabilities().size());
        assertEquals("Surrogate", h.getSurrogates().get(0).getCapabilities().get(0).getId());
        assertEquals(3, h.getSurrogates().get(1).getCapabilities().size());
        assertEquals("Surrogate", h.getSurrogates().get(1).getCapabilities().get(0).getId());
        assertEquals("ESI", h.getSurrogates().get(1).getCapabilities().get(1).getId());
        assertEquals("ESI-Inline", h.getSurrogates().get(1).getCapabilities().get(2).getId());

        assertEquals("abc=\"Surrogate/1.0\", def=\"Surrogate/1.0 ESI/1.0 ESI-Inline\"", h.toString());
    }

}
