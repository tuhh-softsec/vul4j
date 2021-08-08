package com.opensymphony.xwork2.config.providers;

import org.apache.struts2.StrutsInternalTestCase;


public class EnvsValueSubstitutorTest extends StrutsInternalTestCase {

    public void testNoSubstitution() throws Exception {
        // given
        ValueSubstitutor substitutor = new EnvsValueSubstitutor();

        // when
        String actual = substitutor.substitute("val1");

        // then
        assertEquals("val1", actual);
    }
}
