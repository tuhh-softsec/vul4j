/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

