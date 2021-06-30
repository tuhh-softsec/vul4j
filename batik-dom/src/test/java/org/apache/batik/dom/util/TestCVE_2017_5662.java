package org.apache.batik.dom.util;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class TestCVE_2017_5662 {

    @Test
    public void testCVE_2017_5662() throws MalformedURLException {
        URL url = getClass().getClassLoader().getResource("ssrf.svg");
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
        SAXDocumentFactory f = new SAXDocumentFactory(impl, parser);

        try {
            Document doc = f.createDocument(url.toString());
        }
        catch (IOException e) {
            fail("Should not try to load external DTD: " + e);
        }
    }
}
