/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package org.apache.xml.security.test.encryption;

import java.security.cert.X509Certificate;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * Test resolver - simply maps a key name to the appropriate key
 *
 * @author Berin Lautenbach
 */

public class BobKeyResolver extends KeyResolverSpi {
	
	/** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(BobKeyResolver.class.getName());
	
	KeyName _kn = null;

	/**
	 * Method engineCanResolve
	 *
	 * @param element
	 * @param BaseURI
	 * @param storage
	 *
	 */

	public boolean engineCanResolve(Element element, String BaseURI,
									StorageResolver storage) {

		log.debug("Can I resolve " + element.getTagName());

		if (element == null) {
			return false;
		}

		boolean isKeyName = XMLUtils.elementIsInSignatureSpace(element,
									   Constants._TAG_KEYNAME);

		try {
			if (isKeyName) {
				_kn = new KeyName(element, "");
				if (_kn.getKeyName().equals("bob")) {
					return true;
				}
			}
		}
		catch (Exception e) {
			// Do nothing
		}
		
		return false;
	}

	/**
	 * Method engineResolvePublicKey
	 *
	 * @param element
	 * @param BaseURI
	 * @param storage
	 * @return null if no {@link PublicKey} could be obtained
	 * @throws KeyResolverException
	 */
	public PublicKey engineResolvePublicKey(
		  Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

		return null;
	}

	/**
	 * Method engineResolveX509Certificate
	 *
	 * @param element
	 * @param BaseURI
	 * @param storage
	 *
	 * @throws KeyResolverException
	 */
	public X509Certificate engineResolveX509Certificate(
			   Element element, String BaseURI, StorageResolver storage)
		throws KeyResolverException {
		return null;
	}

	/**
	 * Method engineResolveSecretKey
	 *
	 * @param element
	 * @param BaseURI
	 * @param storage
	 *
	 * @throws KeyResolverException
	 */
	public javax.crypto.SecretKey engineResolveSecretKey(
           Element element, String BaseURI, StorageResolver storage)
		throws KeyResolverException {

		if (engineCanResolve(element, BaseURI, storage)) {
			try {
				DESedeKeySpec keySpec = new DESedeKeySpec(
					"abcdefghijklmnopqrstuvwx".getBytes("ASCII"));
				SecretKeyFactory keyFactory = 
					SecretKeyFactory.getInstance("DESede");
				SecretKey key = keyFactory.generateSecret(keySpec);
					
				return key;
			}
			catch (Exception e) {
				throw new KeyResolverException("Something badly wrong in creation of bob's key");
			}
		}

		return null;
	}
}

