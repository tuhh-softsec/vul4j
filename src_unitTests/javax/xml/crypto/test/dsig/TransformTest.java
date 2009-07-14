/*
 * Copyright 2006-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.util.Collections;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.security.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.Transform
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class TransformTest extends TestCase {

    XMLSignatureFactory factory;

    private static final String TRANSFORM_ALGOS[] = {
	Transform.BASE64,
	Transform.ENVELOPED,
	Transform.XPATH,
	Transform.XPATH2,
	Transform.XSLT
    };

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public TransformTest() {
	super("TransformTest");
    }

    public TransformTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
	factory = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    public void tearDown() { }

    public void testisFeatureSupported() throws Exception {
	Transform tm; 
	for (int i = 0; i < TRANSFORM_ALGOS.length; i++) {
	    String algo = TRANSFORM_ALGOS[i];
	    TransformParameterSpec params = null;
	    if (algo.equals(Transform.XPATH)) {
		params = new XPathFilterParameterSpec("xPath");
	    } else if (algo.equals(Transform.XPATH2)) {
		params = new XPathFilter2ParameterSpec
		    (Collections.singletonList(new XPathType
			("xPath2", XPathType.Filter.INTERSECT)));
	    } else if (algo.equals(Transform.XSLT)) {
		params = new XSLTTransformParameterSpec(new XSLTStructure());
	    }
	    tm = factory.newTransform(algo, params);
	    try {
		tm.isFeatureSupported(null); 
		fail(TRANSFORM_ALGOS[i] + 
		     ": Should raise a NPE for null feature"); 
	    } catch (NullPointerException npe) {}
	    
	    assertTrue(!tm.isFeatureSupported("not supported"));
	}
    }

    public void testConstructor() throws Exception {
	// test newTransform(String algorithm, 
	//                   AlgorithmParameterSpec params)
	// for generating Transform objects
	Transform tm; 
	for (int i = 0; i < TRANSFORM_ALGOS.length; i++) {
	    String algo = TRANSFORM_ALGOS[i];
	    TransformParameterSpec params = null;
	    if (algo.equals(Transform.XPATH)) {
		params = new XPathFilterParameterSpec("xPath");
	    } else if (algo.equals(Transform.XPATH2)) {
		params = new XPathFilter2ParameterSpec
		    (Collections.singletonList(new XPathType
			("xPath2", XPathType.Filter.INTERSECT)));
	    } else if (algo.equals(Transform.XSLT)) {
		params = new XSLTTransformParameterSpec(new XSLTStructure());
	    }
	    try {
		tm = factory.newTransform(algo, params);
		assertNotNull(tm);
		assertEquals(tm.getAlgorithm(), algo);
		assertEquals(tm.getParameterSpec(), params);
	    } catch (Exception ex) {
		fail(TRANSFORM_ALGOS[i] + ": Unexpected exception " + ex); 
	    }
	    try {
		tm = factory.newTransform
		    (algo, new TestUtils.MyOwnC14nParameterSpec());
		fail(TRANSFORM_ALGOS[i] + 
		     ": Should raise an IAPE for invalid parameters"); 
	    } catch (InvalidAlgorithmParameterException iape) {
	    } catch (Exception ex) {
		fail(TRANSFORM_ALGOS[i] + 
		     ": Should raise a IAPE instead of " + ex); 
	    }
	}

	try {
	    tm = factory.newTransform(null, (TransformParameterSpec) null); 
	    fail("Should raise a NPE for null algo"); 
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should raise a NPE instead of " + ex); 
	}

	try {
	    tm = factory.newTransform
		("non-existent", (TransformParameterSpec) null); 
	    fail("Should raise an NSAE for non-existent algos"); 
	} catch (NoSuchAlgorithmException nsae) {
	} catch (Exception ex) {
	    fail("Should raise an NSAE instead of " + ex); 
	}
    }

    private static class XSLTStructure implements XMLStructure {
	public boolean isFeatureSupported(String feature) { return false; }
    }
}
