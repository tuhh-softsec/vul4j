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

package org.apache.xml.security.encryption;

import java.io.IOException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.TransformationException;
//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xml.utils.URI;
import org.w3c.dom.Attr;
//import org.w3c.dom.Document;
//import org.w3c.dom.DocumentFragment;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import sun.misc.BASE64Encoder;
import org.apache.xml.security.utils.Base64;


/**
 * <code>XMLCipherInput</code> is used to wrap input passed into the
 * XMLCipher encryption operations.
 *
 * In decryption mode, it takes a <code>CipherData</code> object and allows
 * callers to dereference the CipherData into the encrypted bytes that it
 * actually represents.  This takes care of all base64 encoding etc.
 *
 * While primarily an internal class, this can be used by applications to
 * quickly and easily retrieve the encrypted bytes from an EncryptedType
 * object
 *
 * @author Berin Lautenbach
 */
public class XMLCipherInput {

    private static org.apache.commons.logging.Log logger = 
        org.apache.commons.logging.LogFactory.getLog(XMLCipher.class.getName());

	/** The data we are working with */
	private CipherData _cipherData;

	/** MODES */
	private int _mode;

	/**
	 * Constructor for processing encrypted octets
	 *
	 * @param data The <code>CipherData</code> object to read the bytes from
	 * @throws {@link XMLEncryptionException}
	 */

	public XMLCipherInput(CipherData data) throws XMLEncryptionException {

		_cipherData = data;
		_mode = XMLCipher.DECRYPT_MODE;
		if (_cipherData == null) {
			throw new XMLEncryptionException("CipherData is null");
		}

	}

	/**
	 * Constructor for processing encrypted octets
	 *
	 * @param input The <code>EncryptedType</code> object to read 
	 * the bytes from.
	 * @throws {@link XMLEncryptionException}
	 */

	public XMLCipherInput(EncryptedType input) throws XMLEncryptionException {

		_cipherData = ((input == null) ? null : input.getCipherData());
		_mode = XMLCipher.DECRYPT_MODE;
		if (_cipherData == null) {
			throw new XMLEncryptionException("CipherData is null");
		}

	}

	/**
	 * Dereferences the input and returns it as a single byte array.
	 *
	 * @throws XMLEncryption Exception
	 */

	public byte[] getBytes() throws XMLEncryptionException {

		if (_mode == XMLCipher.DECRYPT_MODE) {
			return getDecryptBytes();
		}
		return null;
	}

	/**
	 * Internal method to get bytes in decryption mode
	 */

	private byte[] getDecryptBytes() throws XMLEncryptionException {

		String base64EncodedEncryptedOctets = null;

        if (_cipherData.getDataType() == CipherData.REFERENCE_TYPE) {
			// Fun time!
			logger.debug("Found a reference type CipherData");
			CipherReference cr = _cipherData.getCipherReference();

			// Need to wrap the uri in an Attribute node so that we can
			// Pass to the resource resolvers

			Attr uriAttr = cr.getURIAsAttr();
			XMLSignatureInput input = null;

			try {
				ResourceResolver resolver = 
					ResourceResolver.getInstance(uriAttr, null);
				input = resolver.resolve(uriAttr, null);
			} catch (ResourceResolverException ex) {
				throw new XMLEncryptionException("empty", ex);
			} catch (XMLSecurityException ex) {
				throw new XMLEncryptionException("empty", ex);
			}

			if (input != null) {
				logger.debug("Managed to resolve URI \"" + cr.getURI() + "\"");
			}
			else {
				logger.debug("Failed to resolve URI \"" + cr.getURI() + "\"");
			}
		
			// Lets see if there are any transforms
			Transforms transforms = cr.getTransforms();
			if (transforms != null) {
				logger.debug ("Have transforms in cipher reference");
				try {
 				    org.apache.xml.security.transforms.Transforms dsTransforms =
						transforms.getDSTransforms();
				    input =	dsTransforms.performTransforms(input);
				} catch (TransformationException ex) {
					throw new XMLEncryptionException("empty", ex);
				}
			}

			try {
				return input.getBytes();
			}
			catch (IOException ex) {
				throw new XMLEncryptionException("empty", ex);
			} catch (InvalidCanonicalizerException ex) {
				throw new XMLEncryptionException("empty", ex);
			} catch (CanonicalizationException ex) {
				throw new XMLEncryptionException("empty", ex);
			}
			
            // retrieve the cipher text
        } else if (_cipherData.getDataType() == CipherData.VALUE_TYPE) {
            CipherValue cv = _cipherData.getCipherValue();
            base64EncodedEncryptedOctets = new String(cv.getValue());
        } else {
			throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
		}

        logger.debug("Encrypted octets:\n" + base64EncodedEncryptedOctets);

        byte[] encryptedBytes = null;

        try {
			encryptedBytes = Base64.decode(base64EncodedEncryptedOctets);
        } catch (Base64DecodingException bde) {
            throw new XMLEncryptionException("empty", bde);
        }

		return (encryptedBytes);

	}

}


