package org.apache.xml.security.test.transforms;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.*;

public class SampleTransform extends TransformSpi {

    public static final String uri =
        "http://org.apache.xml.security.test.transforms.SampleTransform";

    public SampleTransform() {
        try {
            Transform.init();
            Transform.register
               (uri, "org.apache.xml.security.test.transforms.SampleTransform");
        } catch (AlgorithmAlreadyRegisteredException e) { }
    }

    protected String engineGetURI() {
        return uri;
    }

    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
        throws IOException, CanonicalizationException,
               InvalidCanonicalizerException, TransformationException,
               ParserConfigurationException, SAXException {
        throw new UnsupportedOperationException();
    }
}
