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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.lang.Integer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.utils.URI;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;
import org.apache.xml.security.utils.Base64;


/**
 * <code>XMLCipher</code> encrypts and decrypts the contents of
 * <code>Document</code>s, <code>Element</code>s and <code>Element</code>
 * contents. It was designed to resemble <code>javax.crypto.Cipher</code> in
 * order to facilitate understanding of its functioning.
 *
 * @author Axl Mattheus (Sun Microsystems)
 * @author Christian Geuer-Pollmann
 */
public class XMLCipher {

    private static org.apache.commons.logging.Log logger = 
        org.apache.commons.logging.LogFactory.getLog(XMLCipher.class.getName());

    public static final String TRIPLEDES =                   
        EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES;
    public static final String AES_128 =                     
        EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    public static final String AES_256 =                     
        EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256;
    public static final String AES_192 =                     
        EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192;
    public static final String RSA_v1dot5 =                  
        EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
    public static final String RSA_OAEP =                    
        EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
    public static final String DIFFIE_HELLMAN =              
        EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH;
    public static final String TRIPLEDES_KeyWrap =           
        EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES;
    public static final String AES_128_KeyWrap =             
        EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
    public static final String AES_256_KeyWrap =             
        EncryptionConstants.ALGO_ID_KEYWRAP_AES256;
    public static final String AES_192_KeyWrap =             
        EncryptionConstants.ALGO_ID_KEYWRAP_AES192;
    public static final String SHA1 =                        
        Constants.ALGO_ID_DIGEST_SHA1;
    public static final String SHA256 =                      
        MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256;
    public static final String SHA512 =                      
        MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512;
    public static final String RIPEMD_160 =                  
        MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160;
    public static final String XML_DSIG =                    
        Constants.SignatureSpecNS;
    public static final String N14C_XML =                    
        Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
    public static final String N14C_XML_WITH_COMMENTS =      
        Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS;
    public static final String EXCL_XML_N14C =               
        Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
    public static final String EXCL_XML_N14C_WITH_COMMENTS = 
        Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS;
    public static final String BASE64_ENCODING =             
        org.apache.xml.security.transforms.Transforms.TRANSFORM_BASE64_DECODE;

    public static final int ENCRYPT_MODE = Cipher.ENCRYPT_MODE;
    public static final int DECRYPT_MODE = Cipher.DECRYPT_MODE;
    public static final int UNWRAP_MODE  = Cipher.UNWRAP_MODE;
    public static final int WRAP_MODE    = Cipher.WRAP_MODE;

    private static XMLCipher instance = null;
    private static final String ENC_ALGORITHMS = TRIPLEDES + "\n" +
        AES_128 + "\n" + AES_256 + "\n" + AES_192 + "\n" + RSA_v1dot5 + "\n" +
        RSA_OAEP + "\n" + TRIPLEDES_KeyWrap + "\n" + AES_128_KeyWrap + "\n" +
        AES_256_KeyWrap + "\n" + AES_192_KeyWrap+ "\n";
    private static final String ALGORITHMS = TRIPLEDES + "\n" +
        AES_128 + "\n" + AES_256 + "\n" + AES_192 + "\n" + RSA_v1dot5 + "\n" +
        RSA_OAEP + "\n" + DIFFIE_HELLMAN + "\n" + TRIPLEDES_KeyWrap + "\n" +
        AES_128_KeyWrap + "\n" +  AES_256_KeyWrap + "\n" +
        AES_192_KeyWrap+ "\n" + SHA1 + "\n" + SHA256 + "\n" + SHA512 + "\n" +
        RIPEMD_160 + "\n" + XML_DSIG + "\n" + N14C_XML + "\n" +
        N14C_XML_WITH_COMMENTS + "\n" + EXCL_XML_N14C + "\n" +
        EXCL_XML_N14C_WITH_COMMENTS;

    private Cipher contextCipher;
    private int cipherMode = Integer.MIN_VALUE;
    private String algorithm = null;  // URI for requested algorithm
	private String requestedJCEProvider = null;
    private Document contextDocument;
    private Factory factory;
    private Serializer serializer;
    private Map enc2JCE;
	private Map enc2IV;
	private Key localKey;

    /**
     * Creates a new <code>XMLCipher</code>.
     *
     * @since 1.0.
     */
    private XMLCipher() {
        logger.debug("Constructing XMLCipher...");

        factory = new Factory();
        serializer = new Serializer();
        // block encryption
        enc2JCE = new HashMap();
        enc2JCE.put(TRIPLEDES, "DESede/CBC/NoPadding");
        enc2JCE.put(AES_128, "AES/CBC/PKCS5Padding");
        enc2JCE.put(AES_256, "AES/CBC/PKCS5Padding");
        enc2JCE.put(AES_192, "AES/CBC/PKCS5Padding");

        // key encryption
        enc2JCE.put(RSA_v1dot5, "RSA/ECB/PKCS1Padding");
        enc2JCE.put(RSA_OAEP, "RSA/ECB/OAEPPadding");
        enc2JCE.put(TRIPLEDES_KeyWrap, "DESede");
        enc2JCE.put(AES_128_KeyWrap, "AES");
        enc2JCE.put(AES_256_KeyWrap, "AES");
        enc2JCE.put(AES_192_KeyWrap, "AES");
    }

    /**
     * Checks to ensure that the supplied algorithm is valid.
     *
     * @param algorithm the algorithm to check.
     * @returm true if the algorithm is valid, otherwise false.
     * @since 1.0.
     */
    private static boolean isValidEncryptionAlgorithm(String algorithm) {
        boolean result = (
            algorithm.equals(TRIPLEDES) ||
            algorithm.equals(AES_128) ||
            algorithm.equals(AES_256) ||
            algorithm.equals(AES_192) ||
            algorithm.equals(RSA_v1dot5) ||
            algorithm.equals(RSA_OAEP) ||
            algorithm.equals(TRIPLEDES_KeyWrap) ||
            algorithm.equals(AES_128_KeyWrap) ||
            algorithm.equals(AES_256_KeyWrap) ||
            algorithm.equals(AES_192_KeyWrap)
        );

        return (result);
    }

    /**
     * Returns an <code>XMLCipher</code> that implements the specified
     * transformation and operates on the specified context document.
     * <p>
     * If the default provider package supplies an implementation of the
     * requested transformation, an instance of Cipher containing that
     * implementation is returned. If the transformation is not available in
     * the default provider package, other provider packages are searched.
     * <p>
     * <b>NOTE<sub>1</sub>:</b> The transformation name does not follow the same
     * pattern as that oulined in the Java Cryptography Extension Reference
     * Guide but rather that specified by the XML Encryption Syntax and
     * Processing document. The rational behind this is to make it easier for a
     * novice at writing Java Encryption software to use the library.
     * <p>
     * <b>NOTE<sub>2</sub>:</b> <code>getInstance()</code> does not follow the
     * same pattern regarding exceptional conditions as that used in
     * <code>javax.crypto.Cipher</code>. Instead, it only throws an
     * <code>XMLEncryptionException</code> which wraps an underlying exception.
     * The stack trace from the exception should be self explanitory.
     *
     * @param transformation the name of the transformation, e.g.,
     *   <code>XMLCipher.TRIPLEDES</code> which is shorthand for
     *   &quot;http://www.w3.org/2001/04/xmlenc#tripledes-cbc&quot;
     * @throws <code>XMLEncryptionException</code>.
     * @see javax.crypto.Cipher#getInstance
     */
    public static XMLCipher getInstance(String transformation) throws
            XMLEncryptionException {
        // sanity checks
        logger.debug("Getting XMLCipher...");
        if (null == transformation)
            logger.error("Transformation unexpectedly null...");
        if(!isValidEncryptionAlgorithm(transformation))
            logger.error("Alogorithm unvalid, expected one of " + ENC_ALGORITHMS);

        if (null == instance) {
            instance = new XMLCipher();
        }

        instance.algorithm = transformation;

        try {
            String jceAlgorithm = (String) instance.enc2JCE.get(transformation);
            instance.contextCipher = Cipher.getInstance(jceAlgorithm);

            logger.debug("cihper.algoritm = " +
                instance.contextCipher.getAlgorithm());
        } catch (NoSuchAlgorithmException nsae) {
            throw new XMLEncryptionException("empty", nsae);
        } catch (NoSuchPaddingException nspe) {
            throw new XMLEncryptionException("empty", nspe);
        }

        return (instance);
    }

    /**
     * Returns an <code>XMLCipher</code> that implements the specified
     * transformation and operates on the specified context document.
     *
     * @param transformation the name of the transformation, e.g.,
     *   <code>XMLCipher.TRIPLEDES</code> which is shorthand for
     *   &quot;http://www.w3.org/2001/04/xmlenc#tripledes-cbc&quot;
     * @param provider the JCE provider that supplies the transformation
     * @throws <code>XMLEncryptionException</code>.
     */
    public static XMLCipher getInstance(String transformation, String provider)
            throws XMLEncryptionException {
        // sanity checks
        logger.debug("Getting XMLCipher...");
        if (null == transformation)
            logger.error("Transformation unexpectedly null...");
        if(null == provider)
            logger.error("Provider unexpectedly null..");
        if("" == provider)
            logger.error("Provider's value unexpectedly not specified...");
        if(!isValidEncryptionAlgorithm(transformation))
            logger.error("Alogorithm unvalid, expected one of " + ENC_ALGORITHMS);

        if (null == instance) {
            instance = new XMLCipher();
        }

        instance.algorithm = transformation;
		instance.requestedJCEProvider = provider;

        try {
            String jceAlgorithm = (String) instance.enc2JCE.get(transformation);
            instance.contextCipher = Cipher.getInstance(jceAlgorithm, provider);

            logger.debug("cipher.algorithm = " +
                instance.contextCipher.getAlgorithm());
            logger.debug("provider.name = " + provider);
        } catch (NoSuchAlgorithmException nsae) {
            throw new XMLEncryptionException("empty", nsae);
        } catch (NoSuchProviderException nspre) {
            throw new XMLEncryptionException("empty", nspre);
        } catch (NoSuchPaddingException nspe) {
            throw new XMLEncryptionException("empty", nspe);
        }

        return (instance);
    }

    /**
     * Initializes this cipher with a key.
     * <p>
     * The cipher is initialized for one of the following four operations:
     * encryption, decryption, key wrapping or key unwrapping, depending on the
     * value of opmode.
     *
     * @param opmode the operation mode of this cipher (this is one of the
     *   following: ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE or UNWRAP_MODE)
     * @param key
     * @see javax.crypto.Cipher#init
     */
    public void init(int opmode, Key key) throws XMLEncryptionException {
        // sanity checks
        logger.debug("Initializing XMLCipher...");
        if (opmode != ENCRYPT_MODE && opmode != DECRYPT_MODE)
            logger.error("Mode unexpectedly invalid...");
        logger.debug("opmode = " +
            ((opmode == ENCRYPT_MODE) ? "ENCRYPT_MODE" : "DECRYPT_MODE"));

        cipherMode = opmode;
		localKey = key;

    }

    /**
     * Encrypts an <code>Element</code> and replaces it with its encrypted
     * counterpart in the context <code>Document</code>, that is, the
     * <code>Document</code> specified when one calls
     * {@link #getInstance(Document, String) getInstance}.
     *
     * @param element the <code>Element</code> to encrypt.
     * @return the context <code>Document</code> with the encrypted
     *   <code>Element</code> having replaced the source <code>Element</code>.
     */
    private Document encryptElement(Element element) throws
            XMLEncryptionException {
        logger.debug("Encrypting element...");
        if(null == element) 
            logger.error("Element unexpectedly null...");
        if(cipherMode != ENCRYPT_MODE)
            logger.error("XMLCipher unexpectedly not in ENCRYPT_MODE...");

        String serializedOctets = serializer.serialize(element);
        logger.debug("Serialized octets:\n" + serializedOctets);

        byte[] encryptedBytes = null;
		// Now create the working cipher

		String jceAlgorithm =
			JCEMapper.translateURItoJCEID(algorithm).getAlgorithmID();
		String provider;

		if (requestedJCEProvider == null)
			provider =
				JCEMapper.translateURItoJCEID(algorithm).getProviderId();
		else
			provider = requestedJCEProvider;

		logger.debug("provider = " + provider + "alg = " + jceAlgorithm);

		Cipher c;
		try {
			c = Cipher.getInstance(jceAlgorithm, provider);
		} catch (NoSuchAlgorithmException nsae) {
			throw new XMLEncryptionException("empty", nsae);
		} catch (NoSuchProviderException nspre) {
			throw new XMLEncryptionException("empty", nspre);
		} catch (NoSuchPaddingException nspae) {
			throw new XMLEncryptionException("empty", nspae);
		}

		// Now perform the encryption

		try {
			// Should internally generate an IV
			// todo - allow user to set an IV
			c.init(cipherMode, localKey);
		} catch (InvalidKeyException ike) {
			throw new XMLEncryptionException("empty", ike);
		}

        try {
            encryptedBytes =
                c.doFinal(serializedOctets.getBytes("UTF-8"));

            logger.debug("Expected cipher.outputSize = " +
                Integer.toString(c.getOutputSize(
                    serializedOctets.getBytes().length)));
            logger.debug("Actual cipher.outputSize = " +
                Integer.toString(encryptedBytes.length));
        } catch (IllegalStateException ise) {
            throw new XMLEncryptionException("empty", ise);
        } catch (IllegalBlockSizeException ibse) {
            throw new XMLEncryptionException("empty", ibse);
        } catch (BadPaddingException bpe) {
            throw new XMLEncryptionException("empty", bpe);
        } catch (UnsupportedEncodingException uee) {
		   	throw new XMLEncryptionException("empty", uee);
		}

		// Now build up to a properly XML Encryption encoded octet stream
		// IvParameterSpec iv;

		byte[] iv = c.getIV();
		byte[] finalEncryptedBytes = 
			new byte[iv.length + encryptedBytes.length];
		System.arraycopy(iv, 0, finalEncryptedBytes, 0,
						 iv.length);
		System.arraycopy(encryptedBytes, 0, finalEncryptedBytes, 
						 iv.length,
						 encryptedBytes.length);

        String base64EncodedEncryptedOctets = new BASE64Encoder().encode(
            finalEncryptedBytes);

        logger.debug("Encrypted octets:\n" + base64EncodedEncryptedOctets);
        logger.debug("Encrypted octets length = " +
            base64EncodedEncryptedOctets.length());

        EncryptedData data = createEncryptedData(CipherData.VALUE_TYPE,
            base64EncodedEncryptedOctets);
        try {
            data.setType(new URI(EncryptionConstants.TYPE_ELEMENT).toString());
            EncryptionMethod method = factory.newEncryptionMethod(
                new URI(algorithm).toString());
            data.setEncryptionMethod(method);
        } catch (URI.MalformedURIException mfue) {
            throw new XMLEncryptionException("empty", mfue);
        }

        Element encryptedElement = factory.toElement(data);

        Node sourceParent = element.getParentNode();
        sourceParent.replaceChild(encryptedElement, element);

        return (contextDocument);
    }

    /**
     * Encrypts a <code>NodeList</code> (the contents of an
     * <code>Element</code>) and replaces its parent <code>Element</code>'s
     * content with this the resulting <code>EncryptedType</code> within the
     * context <code>Document</code>, that is, the <code>Document</code>
     * specified when one calls
     * {@link #getInstance(Document, String) getInstance}.
     *
     * @param content the <code>NodeList</code> to encrypt.
     * @return the context <code>Document</code> with the encrypted
     *   <code>NodeList</code> having replaced the content of the source
     *   <code>Element</code>.
     */
    private Document encryptElementContent(Element element) throws
            XMLEncryptionException {
        logger.debug("Encrypting element content...");
        if(null == element) 
            logger.error("Element unexpectedly null...");
        if(cipherMode != ENCRYPT_MODE)
            logger.error("XMLCipher unexpectedly not in ENCRYPT_MODE...");

        NodeList children = element.getChildNodes();
        String serializedOctets = null;
        if ((null != children)) {
            serializedOctets = serializer.serialize(children);
        } else {
            Object exArgs[] = {"Element has no content."};
            throw new XMLEncryptionException("empty", exArgs);
        }
        logger.debug("Serialized octets:\n" + serializedOctets);

        byte[] encryptedBytes = null;
        try {
            encryptedBytes =
                contextCipher.doFinal(serializedOctets.getBytes("UTF-8"));

            logger.debug("Expected cipher.outputSize = " +
                Integer.toString(contextCipher.getOutputSize(
                    serializedOctets.getBytes().length)));
            logger.debug("Actual cipher.outputSize = " +
                Integer.toString(encryptedBytes.length));
        } catch (IllegalStateException ise) {
            throw new XMLEncryptionException("empty", ise);
        } catch (IllegalBlockSizeException ibse) {
            throw new XMLEncryptionException("empty", ibse);
        } catch (BadPaddingException bpe) {
            throw new XMLEncryptionException("empty", bpe);
        } catch (UnsupportedEncodingException uee) {
		   	throw new XMLEncryptionException("empty", uee);
        }

        String base64EncodedEncryptedOctets = new BASE64Encoder().encode(
            encryptedBytes);

        logger.debug("Encrypted octets:\n" + base64EncodedEncryptedOctets);
        logger.debug("Encrypted octets length = " +
            base64EncodedEncryptedOctets.length());

        EncryptedData data = createEncryptedData(CipherData.VALUE_TYPE,
            base64EncodedEncryptedOctets);
        try {
            data.setType(new URI(EncryptionConstants.TYPE_CONTENT).toString());
            EncryptionMethod method = factory.newEncryptionMethod(
                new URI(algorithm).toString());
            data.setEncryptionMethod(method);
        } catch (URI.MalformedURIException mfue) {
            throw new XMLEncryptionException("empty", mfue);
        }

        Element encryptedElement = factory.toElement(data);
        removeContent(element);
        element.appendChild(encryptedElement);

        return (contextDocument);
    }

    /**
     * Process a DOM <code>Document</code> node. The processing depends on the
     * initialization parameters of {@link #init(int, Key) init()}.
     *
     * @param context the context <code>Document</code>.
     * @param source the <code>Document</code> to be encrypted or decrypted.
     * @return the processed <code>Document</code>.
     * @throws XMLEnccryptionException to indicate any exceptional conditions.
     */
    public Document doFinal(Document context, Document source) throws
            XMLEncryptionException {
        logger.debug("Processing source document...");
        if(null == context)
            logger.error("Context document unexpectedly null...");
        if(null == source)
            logger.error("Source document unexpectedly null...");

        instance.contextDocument = context;

        Document result = null;

        switch (cipherMode) {
        case DECRYPT_MODE:
            result = decryptElement(source.getDocumentElement());
            break;
        case ENCRYPT_MODE:
            result = encryptElement(source.getDocumentElement());
            break;
        case UNWRAP_MODE:
            break;
        case WRAP_MODE:
            break;
        default:
            throw new XMLEncryptionException(
                "empty", new IllegalStateException());
        }

        return (result);
    }

    /**
     * Process a DOM <code>Element</code> node. The processing depends on the
     * initialization parameters of {@link #init(int, Key) init()}.
     *
     * @param context the context <code>Document</code>.
     * @param element the <code>Element</code> to be encrypted.
     * @return the processed <code>Document</code>.
     * @throws XMLEnccryptionException to indicate any exceptional conditions.
     */
    public Document doFinal(Document context, Element element) throws
            XMLEncryptionException {
        logger.debug("Processing source element...");
        if(null == context)
            logger.error("Context document unexpectedly null...");
        if(null == element)
            logger.error("Source element unexpectedly null...");

        instance.contextDocument = context;

        Document result = null;

        switch (cipherMode) {
        case DECRYPT_MODE:
            result = decryptElement(element);
            break;
        case ENCRYPT_MODE:
            result = encryptElement(element);
            break;
        case UNWRAP_MODE:
            break;
        case WRAP_MODE:
            break;
        default:
            throw new XMLEncryptionException(
                "empty", new IllegalStateException());
        }

        return (result);
    }

    /**
     * Process the contents of a DOM <code>Element</code> node. The processing
     * depends on the initialization parameters of
     * {@link #init(int, Key) init()}.
     *
     * @param context the context <code>Document</code>.
     * @param element the <code>Element</code> which contents is to be
     *   encrypted.
     * @return the processed <code>Document</code>.
     * @throws XMLEnccryptionException to indicate any exceptional conditions.
     */
    public Document doFinal(Document context, Element element, boolean content)
            throws XMLEncryptionException {
        logger.debug("Processing source element...");
        if(null == context)
            logger.error("Context document unexpectedly null...");
        if(null == element)
            logger.error("Source element unexpectedly null...");

        instance.contextDocument = context;

        Document result = null;

        switch (cipherMode) {
        case DECRYPT_MODE:
            if (content) {
                result = decryptElementContent(element);
            } else {
                result = decryptElement(element);
            }
            break;
        case ENCRYPT_MODE:
            if (content) {
                result = encryptElementContent(element);
            } else {
                result = encryptElement(element);
            }
            break;
        case UNWRAP_MODE:
            break;
        case WRAP_MODE:
            break;
        default:
            throw new XMLEncryptionException(
                "empty", new IllegalStateException());
        }

        return (null);
    }

    /**
     * Process a DOM <code>NodeList</code>. The processing depends on the
     * initialization parameters of {@link #init(int, Key) init()}.
     *
     * @param context the context <code>Document</code>.
     * @param elements the <code>NodeList</code> which contents is to be
     *   processed.
     * @return the processed <code>Document</code>.
     * @throws XMLEnccryptionException to indicate any exceptional conditions.
     */
    private Document doFinal(Document context, NodeList elements) throws
            XMLEncryptionException {
        return (null);
    }

    /**
     * Process an XPath expression. The processing depends on the
     * initialization parameters of {@link #init(int, Key) init()}.
     *
     * @param xpathExpression the expression to process.
     * @return the processed <code>Document</code>.
     * @throws XMLEncryptionException to indicat any exceptional conditions.
     */
    private Document doFinal(String xpathExpression) throws
            XMLEncryptionException {
        return (null);
    }

    /**
     *
     */
    private Document doFinal(Document context, Element element,
            EncryptedData data) throws XMLEncryptionException {
        return (null);
    }

    /**
     *
     */
    private Document doFinal(Document context, Element element,
            EncryptedKey key) throws XMLEncryptionException {
        return (null);
    }

    /**
     * Returns an <code>EncryptedData</code> interface. Use this operation if
     * you want to have full control over the contents of the
     * <code>EncryptedData</code> structure.
     *
     * @param context the context <code>Document</code>.
     * @param element the <code>Element</code> that will be encrypted.
     * @throws XMLEncryptionException.
     */
    public EncryptedData encryptData(Document context, Element element) throws
            XMLEncryptionException {
        logger.debug("Encrypting element...");
        if(null == context)
            logger.error("Context document unexpectedly null...");
        if(null == element)
            logger.error("Element unexpectedly null...");
        if(cipherMode != ENCRYPT_MODE)
            logger.error("XMLCipher unexpectedly not in ENCRYPT_MODE...");

        instance.contextDocument = context;

        String serializedOctets = serializer.serialize(element);
        logger.debug("Serialized octets:\n" + serializedOctets);

        byte[] encryptedBytes = null;
        try {
            encryptedBytes =
                contextCipher.doFinal(serializedOctets.getBytes("UTF-8"));

            logger.debug("Expected cipher.outputSize = " +
                Integer.toString(contextCipher.getOutputSize(
                    serializedOctets.getBytes().length)));
            logger.debug("Actual cipher.outputSize = " +
                Integer.toString(encryptedBytes.length));
        } catch (IllegalStateException ise) {
            throw new XMLEncryptionException("empty", ise);
		} catch (UnsupportedEncodingException uee) {
			throw new XMLEncryptionException("empty", uee);
        } catch (IllegalBlockSizeException ibse) {
            throw new XMLEncryptionException("empty", ibse);
        } catch (BadPaddingException bpe) {
            throw new XMLEncryptionException("empty", bpe);
        }

        String base64EncodedEncryptedOctets = new BASE64Encoder().encode(
            encryptedBytes);

        logger.debug("Encrypted octets:\n" + base64EncodedEncryptedOctets);
        logger.debug("Encrypted octets length = " +
            base64EncodedEncryptedOctets.length());

        EncryptedData data = createEncryptedData(CipherData.VALUE_TYPE,
            base64EncodedEncryptedOctets);
        try {
            data.setType(new URI(EncryptionConstants.TYPE_ELEMENT).toString());
            EncryptionMethod method = factory.newEncryptionMethod(
                new URI(algorithm).toString());
            data.setEncryptionMethod(method);
        } catch (URI.MalformedURIException mfue) {
            throw new XMLEncryptionException("empty", mfue);
        }

        return (data);
    }

    /**
     * Returns an <code>EncryptedData</code> interface. Use this operation if
     * you want to load an <code>EncryptedData</code> structure from a DOM 
	 * structure and manipulate the contents 
     *
     * @param context the context <code>Document</code>.
     * @param element the <code>Element</code> that will be loaded
     * @throws XMLEncryptionException.
     */
    public EncryptedData loadEncryptedData(Document context, Element element) 
		throws XMLEncryptionException {
        logger.debug("Loading encrypted element...");
        if(null == context)
            logger.error("Context document unexpectedly null...");
        if(null == element)
            logger.error("Element unexpectedly null...");
        if(cipherMode != DECRYPT_MODE)
            logger.error("XMLCipher unexpectedly not in DECRYPT_MODE...");

        instance.contextDocument = context;
        EncryptedData encryptedData = factory.newEncryptedData(element);

		return (encryptedData);
    }




    /**
     * Decrypts an <code>EncryptedKey</code> object.
     */
    public EncryptedKey encryptKey(Document context, Element element) throws
            XMLEncryptionException {
        return (null);
    }

    /**
     * Removes the contents of a <code>Node</code>.
     *
     * @param node the <code>Node</code> to clear.
     */
    private void removeContent(Node node) {
        NodeList list = node.getChildNodes();
        if (list.getLength() > 0) {
            Node n = list.item(0);
            if (null != n) {
                n.getParentNode().removeChild(n);
            }
            removeContent(node);
        }
    }

    /**
     * Decrypts <code>EncryptedData</code> in a single-part operation.
     *
     * @param data the <code>EncryptedData</code> to decrypt.
     * @return the <code>Node</code> as a result of the decrypt operation.
     */
    private Document decryptElement(Element element) throws
            XMLEncryptionException {

        logger.debug("Decrypting element...");

        if(cipherMode != DECRYPT_MODE)
            logger.error("XMLCipher unexpectedly not in DECRYPT_MODE...");

        EncryptedData encryptedData = factory.newEncryptedData(element);

        CipherData cipherData = encryptedData.getCipherData();
        String base64EncodedEncryptedOctets = null;

        if (cipherData.getDataType() == CipherData.REFERENCE_TYPE) {
            // retrieve the cipher text
        } else if (cipherData.getDataType() == CipherData.VALUE_TYPE) {
            CipherValue cipherValue = cipherData.getCipherValue();
            base64EncodedEncryptedOctets = new String(cipherValue.getValue());
        } else {
            // complain...
        }
        logger.debug("Encrypted octets:\n" + base64EncodedEncryptedOctets);

        byte[] encryptedBytes = null;

        try {
			encryptedBytes = Base64.decode(base64EncodedEncryptedOctets);
        } catch (Base64DecodingException bde) {
            throw new XMLEncryptionException("empty", bde);
        }

		// Now create the working cipher

		String jceAlgorithm = 
			JCEMapper.translateURItoJCEID(encryptedData.getEncryptionMethod()
										  .getAlgorithm()).getAlgorithmID();
		String provider;

		if (requestedJCEProvider == null)
			provider =
				JCEMapper.translateURItoJCEID(encryptedData
											  .getEncryptionMethod()
											  .getAlgorithm())
				.getProviderId();
		else
			provider = requestedJCEProvider;

		Cipher c;
		try {
			c = Cipher.getInstance(jceAlgorithm, provider);
		} catch (NoSuchAlgorithmException nsae) {
			throw new XMLEncryptionException("empty", nsae);
		} catch (NoSuchProviderException nspre) {
			throw new XMLEncryptionException("empty", nspre);
		} catch (NoSuchPaddingException nspae) {
			throw new XMLEncryptionException("empty", nspae);
		}


		// Calculate the IV length and copy out

		// For now, we only work with Block ciphers, so this will work.
		// This should probably be put into the JCE mapper.

		int ivLen = c.getBlockSize();
		byte[] ivBytes = new byte[ivLen];

		// You may be able to pass the entire piece in to IvParameterSpec
		// and it will only take the first x bytes, but no way to be certain
		// that this will work for every JCE provider, so lets copy the
		// necessary bytes into a dedicated array.

		System.arraycopy(encryptedBytes, 0, ivBytes, 0, ivLen);
		IvParameterSpec iv = new IvParameterSpec(ivBytes);		
		
		try {
			c.init(cipherMode, localKey, iv);
		} catch (InvalidKeyException ike) {
			throw new XMLEncryptionException("empty", ike);
		} catch (InvalidAlgorithmParameterException iape) {
			throw new XMLEncryptionException("empty", iape);
		}

        String octets = null;
		byte[] plainBytes;

        try {
            octets = new String(c.doFinal(encryptedBytes, ivLen, 
										  encryptedBytes.length - ivLen),
								"UTF-8");
        } catch (IllegalBlockSizeException ibse) {
            throw new XMLEncryptionException("empty", ibse);
		} catch (UnsupportedEncodingException uee) {
			throw new XMLEncryptionException("empty", uee);
        } catch (BadPaddingException bpe) {
            throw new XMLEncryptionException("empty", bpe);
        }
		
		// Now remove any padding
		//octets = new String(padder.dePad(plainBytes));
	    

        logger.debug("Decrypted octets:\n" + octets);

        Node sourceParent =  element.getParentNode();

        DocumentFragment decryptedFragment = 
			serializer.deserialize(octets, sourceParent);


		// The de-serialiser returns a fragment whose children we need to
		// take on.

		if (sourceParent instanceof Document) {
			
			// If this is a content decryption, this may have problems

			contextDocument.removeChild(contextDocument.getDocumentElement());
			contextDocument.appendChild(decryptedFragment);
		}
		else {

			sourceParent.replaceChild(decryptedFragment, element);

		}

        return (contextDocument);
    }
    

	/**
	 * 
	 * @param element
	 */
    private Document decryptElementContent(Element element) throws 
    		XMLEncryptionException {
    	Element e = (Element) element.getElementsByTagNameNS(
    		EncryptionConstants.EncryptionSpecNS, 
    		EncryptionConstants._TAG_ENCRYPTEDDATA).item(0);
    	
    	if (null == e) {
    		throw new XMLEncryptionException("No EncryptedData child element.");
    	}
    	
    	return (decryptElement(e));
    }


    /**
     * Creates an <code>EncryptedData</code> <code>Element</code>.
     *
     * @param text the Base 64 encoded, encrypted text to wrap in the
     *   <code>EncryptedData</code>.
     * @return the <code>EncryptedData</code> <code>Element</code>.
     *
     * <!--
     * <EncryptedData Id[OPT] Type[OPT] MimeType[OPT] Encoding[OPT]>
     *     <EncryptionMethod/>[OPT]
     *     <ds:KeyInfo>[OPT]
     *         <EncryptedKey/>[OPT]
     *         <AgreementMethod/>[OPT]
     *         <ds:KeyName/>[OPT]
     *         <ds:RetrievalMethod/>[OPT]
     *         <ds:[MUL]/>[OPT]
     *     </ds:KeyInfo>
     *     <CipherData>[MAN]
     *         <CipherValue/> XOR <CipherReference/>
     *     </CipherData>
     *     <EncryptionProperties/>[OPT]
     * </EncryptedData>
     * -->
     */
    private EncryptedData createEncryptedData(int type, String value) throws
            XMLEncryptionException {
        EncryptedData result = null;
        CipherData data = null;

        switch (type) {
            case CipherData.REFERENCE_TYPE:
                String referenceUri = null;
                try {
                    referenceUri = new URI(value).toString();
                } catch (URI.MalformedURIException mfue) {
                    throw new XMLEncryptionException("empty", mfue);
                }
                CipherReference cipherReference = factory.newCipherReference(
                    referenceUri);
                data = factory.newCipherData(type);
                data.setCipherReference(cipherReference);
                result = factory.newEncryptedData(data);
            case CipherData.VALUE_TYPE:
                CipherValue cipherValue = factory.newCipherValue(value);
                data = factory.newCipherData(type);
                data.setCipherValue(cipherValue);
                result = factory.newEncryptedData(data);
        }

        return (result);
    }

    /**
     * Converts <code>String</code>s into <code>Node</code>s and visa versa.
     * <p>
     * <b>NOTE:</b> For internal use only.
     *
     * @author  Axl Mattheus
     */
    private class Serializer {
        private OutputFormat format;
        private XMLSerializer serializer;

        /**
         * Initialize the <code>XMLSerializer</code> with the specified context
         * <code>Document</code>.
         *
         * @param document the context <code>Document</code>.
         */
        Serializer() {
            format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setOmitDocumentType(true);
            format.setOmitXMLDeclaration(true);
            format.setPreserveSpace(true);
        }

        /**
         * Returns a <code>String</code> representation of the specified
         * <code>Document</code>.
         *
         * @param doc the <code>Document</code> to serialize.
         * @return the <code>String</code> representation of the serilaized
         *   <code>Document</code>.
         * @throws
         */
        String serialize(Document document) throws XMLEncryptionException {
            StringWriter output = new StringWriter();
            serializer = new XMLSerializer(output, format);

            try {
                serializer.serialize(document);
            } catch (IOException ioe) {
                throw new XMLEncryptionException("empty", ioe);
            }

            return (output.toString());
        }

        /**
         * Returns a <code>String</code> representation of the specified
         * <code>Element</code>.
         *
         * @param doc the <code>Element</code> to serialize.
         * @return the <code>String</code> representation of the serilaized
         *   <code>Element</code>.
         * @throws XMLEncryptionException
         */
        String serialize(Element element) throws XMLEncryptionException {
            // StringWriter output = new StringWriter();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
            serializer = new XMLSerializer(output, format);

            try {
                serializer.serialize(element);
            } catch (IOException ioe) {
                throw new XMLEncryptionException("empty", ioe);
            }


			String ret = null;
			try {
				ret = output.toString("UTF-8");
			} catch (UnsupportedEncodingException uee) {
				throw new XMLEncryptionException("empty", uee);
			}
            return ret;
        }

        /**
         * Returns a <code>String</code> representation of the specified
         * <code>NodeList</code>.
         *
         * @param doc the <code>NodeList</code> to serialize.
         * @return the <code>String</code> representation of the serilaized
         *   <code>NodeList</code>.
         * @throws
         */
        String serialize(NodeList content) throws XMLEncryptionException {
            StringWriter output = new StringWriter();
            serializer = new XMLSerializer(output, format);

            try {
                for (int i =0; i < content.getLength(); i++) {
                    Node n = content.item(i);
                    if ((null != n) && (n.getNodeType() == Node.ELEMENT_NODE)) {
                        serializer.serialize((Element) n);
                    }
                }
            } catch (IOException ioe) {
                throw new XMLEncryptionException("empty", ioe);
            }

            return (output.toString());
        }

        /**
         *
         */
        DocumentFragment deserialize(String source, Node ctx) throws XMLEncryptionException {
			DocumentFragment result;
            final String tagname = "fragment";

			// Create the context to parse the document against
			StringBuffer sb;
			
			sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><"+tagname);
			
			// Run through each node up to the document node and find any
			// xmlns: nodes

			Node wk = ctx;
			
			while (wk != null) {

				NamedNodeMap atts = wk.getAttributes();
				int length;
				if (atts != null)
					length = atts.getLength();
				else
					length = 0;

				for (int i = 0 ; i < length ; ++i) {
					Node att = atts.item(i);
					if (att.getNodeName().startsWith("xmlns:") ||
						att.getNodeName() == "xmlns") {
					
						// Check to see if this node has already been found
						Node p = ctx;
						boolean found = false;
						while (p != wk) {
							NamedNodeMap tstAtts = p.getAttributes();
							if (tstAtts != null && 
								tstAtts.getNamedItem(att.getNodeName()) != null) {
								found = true;
								break;
							}
							p = p.getParentNode();
						}
						if (found == false) {
							
							// This is an attribute node
							sb.append(" " + att.getNodeName() + "=\"" + 
									  att.getNodeValue() + "\"");
						}
					}
				}
				wk = wk.getParentNode();
			}
			sb.append(">" + source + "</" + tagname + ">");
			String fragment = sb.toString();

            try {
                DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.parse(
				    new InputSource(new StringReader(fragment)));

				Element fragElt = (Element) contextDocument.importNode(
						 d.getDocumentElement(), true);
				result = contextDocument.createDocumentFragment();
				Node child = fragElt.getFirstChild();
				while (child != null) {
					fragElt.removeChild(child);
					result.appendChild(child);
					child = fragElt.getFirstChild();
				}
				String outp = serialize(d);

            } catch (SAXException se) {
                throw new XMLEncryptionException("empty", se);
            } catch (ParserConfigurationException pce) {
                throw new XMLEncryptionException("empty", pce);
            } catch (IOException ioe) {
                throw new XMLEncryptionException("empty", ioe);
            }

            return (result);
        }
    }

    /**
     *
     * @author Axl Mattheus
     */
    private class Factory {
        /**
         *
         */
        AgreementMethod newAgreementMethod(String algorithm) throws
                XMLEncryptionException {
            return (new AgreementMethodImpl(algorithm));
        }

        /**
         *
         */
        CipherData newCipherData(int type) {
            return (new CipherDataImpl(type));
        }

        /**
         *
         */
        CipherReference newCipherReference(String uri) throws
                XMLEncryptionException {
            return (new CipherReferenceImpl(uri));
        }

        /**
         *
         */
        CipherValue newCipherValue(String value) {
            return (new CipherValueImpl(value));
        }

        /**
         *
         */
        CipherValue newCipherValue(byte[] value) {
            return (new CipherValueImpl(value));
        }

        /**
         *
         */
        EncryptedData newEncryptedData(CipherData data) {
            return (new EncryptedDataImpl(data));
        }

        /**
         *
         */
        EncryptedKey newEncryptedKey(CipherData data) {
            return (new EncryptedKeyImpl(data));
        }

        /**
         *
         */
        EncryptionMethod newEncryptionMethod(String algorithm) throws
                XMLEncryptionException {
            return (new EncryptionMethodImpl(algorithm));
        }

        /**
         *
         */
        EncryptionProperties newEncryptionProperties() {
            return (new EncryptionPropertiesImpl());
        }

        /**
         *
         */
        EncryptionProperty newEncryptionProperty() {
            return (new EncryptionPropertyImpl());
        }

        /**
         *
         */
        ReferenceList newReferenceList(int type) {
            return (new ReferenceList(type));
        }

        /**
         *
         */
        Transforms newTransforms() {
            return (new TransformsImpl());
        }

        /**
         *
         */
        // <element name="AgreementMethod" type="xenc:AgreementMethodType"/>
        // <complexType name="AgreementMethodType" mixed="true">
        //     <sequence>
        //         <element name="KA-Nonce" minOccurs="0" type="base64Binary"/>
        //         <!-- <element ref="ds:DigestMethod" minOccurs="0"/> -->
        //         <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
        //         <element name="OriginatorKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
        //         <element name="RecipientKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
        //     </sequence>
        //     <attribute name="Algorithm" type="anyURI" use="required"/>
        // </complexType>
        AgreementMethod newAgreementMethod(Element element) throws
                XMLEncryptionException {
            if (null == element) {
                //complain
            }

            String algorithm = element.getAttributeNS(null, 
            	EncryptionConstants._ATT_ALGORITHM);
            AgreementMethod result = newAgreementMethod(algorithm);

            Element kaNonceElement = (Element) element.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_KA_NONCE).item(0);
            if (null != kaNonceElement) {
                result.setKANonce(kaNonceElement.getNodeValue().getBytes());
            }
            // TODO: ///////////////////////////////////////////////////////////
            // Figure out how to make this pesky line work..
            // <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>

            // TODO: ///////////////////////////////////////////////////////////
            // Implement properly, implement a KeyInfo marshaler.
            Element originatorKeyInfoElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_ORIGINATORKEYINFO).item(0);
            if (null != originatorKeyInfoElement) {
                result.setOriginatorKeyInfo(null);
            }

            // TODO: ///////////////////////////////////////////////////////////
            // Implement properly, implement a KeyInfo marshaler.
            Element recipientKeyInfoElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_RECIPIENTKEYINFO).item(0);
            if (null != recipientKeyInfoElement) {
                result.setRecipientKeyInfo(null);
            }

            return (result);
        }

        /**
         *
         */
        // <element name='CipherData' type='xenc:CipherDataType'/>
        // <complexType name='CipherDataType'>
        //     <choice>
        //         <element name='CipherValue' type='base64Binary'/>
        //         <element ref='xenc:CipherReference'/>
        //     </choice>
        // </complexType>
        CipherData newCipherData(Element element) throws
                XMLEncryptionException {
            if (null == element) {
                // complain
            }

            int type = 0;
            Element e = null;
            if (element.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS, 
                EncryptionConstants._TAG_CIPHERVALUE).getLength() > 0) {
                type = CipherData.VALUE_TYPE;
                e = (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_CIPHERVALUE).item(0);
            } else if (element.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_CIPHERREFERENCE).getLength() > 0) {
                type = CipherData.REFERENCE_TYPE;
                e = (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_CIPHERREFERENCE).item(0);
            }

            CipherData result = newCipherData(type);
            if (type == CipherData.VALUE_TYPE) {
                result.setCipherValue(newCipherValue(e));
            } else if (type == CipherData.REFERENCE_TYPE) {
                //
            }

            return (result);
        }

        /**
         *
         */
        // <element name='CipherReference' type='xenc:CipherReferenceType'/>
        // <complexType name='CipherReferenceType'>
        //     <sequence>
        //         <element name='Transforms' type='xenc:TransformsType' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='URI' type='anyURI' use='required'/>
        // </complexType>
        CipherReference newCipherReference(Element element) throws
                XMLEncryptionException {
            // NOTE: ///////////////////////////////////////////////////////////
            //
            // This operation will be implemented during November 2002. Until
            //then, complain.
            // TODO: Implement.
            String uo = "This operation is not implemented in this release.";
            throw new XMLEncryptionException("empty",
                new UnsupportedOperationException(uo));
        }

        /**
         *
         */
        CipherValue newCipherValue(Element element) throws
                XMLEncryptionException {
            String value = element.getFirstChild().getNodeValue();

            CipherValue result = newCipherValue(value);

            return (result);
        }

        /**
         *
         */
        // <complexType name='EncryptedType' abstract='true'>
        //     <sequence>
        //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
        //             minOccurs='0'/>
        //         <element ref='ds:KeyInfo' minOccurs='0'/>
        //         <element ref='xenc:CipherData'/>
        //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <attribute name='Type' type='anyURI' use='optional'/>
        //     <attribute name='MimeType' type='string' use='optional'/>
        //     <attribute name='Encoding' type='anyURI' use='optional'/>
        // </complexType>
        // <element name='EncryptedData' type='xenc:EncryptedDataType'/>
        // <complexType name='EncryptedDataType'>
        //     <complexContent>
        //         <extension base='xenc:EncryptedType'/>
        //     </complexContent>
        // </complexType>
        EncryptedData newEncryptedData(Element element) throws
			XMLEncryptionException {
            EncryptedData result = null;

            Element dataElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_CIPHERDATA).item(0);
            CipherData data = newCipherData(dataElement);

            result = newEncryptedData(data);

            try {
                result.setId(element.getAttributeNS(
                    null, EncryptionConstants._ATT_ID));
                result.setType(new URI(
                    element.getAttributeNS(
                        null, EncryptionConstants._ATT_TYPE)).toString());
                result.setMimeType(element.getAttributeNS(
                    null, EncryptionConstants._ATT_MIMETYPE));
                result.setEncoding(new URI(
                    element.getAttributeNS(
                        null, Constants._ATT_ENCODING)).toString());
            } catch (URI.MalformedURIException mfue) {
                // do nothing
            }

            Element encryptionMethodElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_ENCRYPTIONMETHOD).item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(newEncryptionMethod(
                    encryptionMethodElement));
            }

            // BFL 16/7/03 - simple implementation
			// TODO: Work out how to handle relative URI

            Element keyInfoElement =
                (Element) element.getElementsByTagNameNS(
                    Constants.SignatureSpecNS, Constants._TAG_KEYINFO).item(0);
            if (null != keyInfoElement) {
				try {
					result.setKeyInfo(new KeyInfo(keyInfoElement, null));
				} catch (XMLSecurityException xse) {
					throw new XMLEncryptionException("Error loading Key Info", 
													 xse);
				}
            }

            // TODO: Implement
            Element encryptionPropertiesElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_ENCRYPTIONPROPERTIES).item(0);
            if (null != encryptionPropertiesElement) {
                result.setEncryptionProperties(
                    newEncryptionProperties(encryptionPropertiesElement));
            }

            return (result);
        }

        /**
         *
         */
        // <complexType name='EncryptedType' abstract='true'>
        //     <sequence>
        //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
        //             minOccurs='0'/>
        //         <element ref='ds:KeyInfo' minOccurs='0'/>
        //         <element ref='xenc:CipherData'/>
        //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <attribute name='Type' type='anyURI' use='optional'/>
        //     <attribute name='MimeType' type='string' use='optional'/>
        //     <attribute name='Encoding' type='anyURI' use='optional'/>
        // </complexType>
        // <element name='EncryptedKey' type='xenc:EncryptedKeyType'/>
        // <complexType name='EncryptedKeyType'>
        //     <complexContent>
        //         <extension base='xenc:EncryptedType'>
        //             <sequence>
        //                 <element ref='xenc:ReferenceList' minOccurs='0'/>
        //                 <element name='CarriedKeyName' type='string' minOccurs='0'/>
        //             </sequence>
        //             <attribute name='Recipient' type='string' use='optional'/>
        //         </extension>
        //     </complexContent>
        // </complexType>
        EncryptedKey newEncryptedKey(Element element) throws
                XMLEncryptionException {
            EncryptedKey result = null;

            Element dataElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_CIPHERDATA).item(0);
            CipherData data = newCipherData(dataElement);
            result = newEncryptedKey(data);

            try {
                result.setId(element.getAttributeNS(
                    null, EncryptionConstants._ATT_ID));
                result.setType(new URI(
                    element.getAttributeNS(
                        null, EncryptionConstants._ATT_TYPE)).toString());
                result.setMimeType(element.getAttributeNS(
                    null, EncryptionConstants._ATT_MIMETYPE));
                result.setEncoding(new URI(
                    element.getAttributeNS(
                        null, Constants._ATT_ENCODING)).toString());
                result.setRecipient(element.getAttributeNS(
                    null, EncryptionConstants._ATT_RECIPIENT));
            } catch (URI.MalformedURIException mfue) {
                // do nothing
            }

            Element encryptionMethodElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTIONMETHOD).item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(newEncryptionMethod(
                    encryptionMethodElement));
            }

            // TODO: Implement
            Element keyInfoElement =
                (Element) element.getElementsByTagNameNS(
                    Constants.SignatureSpecNS, Constants._TAG_KEYINFO).item(0);
            if (null != keyInfoElement) {
                result.setKeyInfo(null);
            }

            // TODO: Implement
            Element encryptionPropertiesElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_ENCRYPTIONPROPERTIES).item(0);
            if (null != encryptionPropertiesElement) {
                result.setEncryptionProperties(
                    newEncryptionProperties(encryptionPropertiesElement));
            }

            Element referenceListElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_REFERENCELIST).item(0);
            if (null != referenceListElement) {
                result.setReferenceList(newReferenceList(referenceListElement));
            }

            Element carriedNameElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS,
                    EncryptionConstants._TAG_CARRIEDKEYNAME).item(0);
            if (null != carriedNameElement) {
                result.setCarriedName(carriedNameElement.getNodeValue());
            }

            return (result);
        }

        /**
         *
         */
        // <complexType name='EncryptionMethodType' mixed='true'>
        //     <sequence>
        //         <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
        //         <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
        //         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
        //     </sequence>
        //     <attribute name='Algorithm' type='anyURI' use='required'/>
        // </complexType>
        EncryptionMethod newEncryptionMethod(Element element) throws
                XMLEncryptionException {
            String algorithm = element.getAttributeNS(
                null, EncryptionConstants._ATT_ALGORITHM);
            EncryptionMethod result = newEncryptionMethod(algorithm);

            Element keySizeElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_KEYSIZE).item(0);
            if (null != keySizeElement) {
                result.setKeySize(
                    Integer.valueOf(keySizeElement.getNodeValue()).intValue());
            }

            Element oaepParamsElement =
                (Element) element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_OAEPPARAMS).item(0);
            if (null != oaepParamsElement) {
                result.setOAEPparams(
                    oaepParamsElement.getNodeValue().getBytes());
            }

            // TODO: Make this mess work
            // <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>

            return (result);
        }

        /**
         *
         */
        // <element name='EncryptionProperties' type='xenc:EncryptionPropertiesType'/>
        // <complexType name='EncryptionPropertiesType'>
        //     <sequence>
        //         <element ref='xenc:EncryptionProperty' maxOccurs='unbounded'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        // </complexType>
        EncryptionProperties newEncryptionProperties(Element element) throws
                XMLEncryptionException {
            EncryptionProperties result = newEncryptionProperties();

            result.setId(element.getAttributeNS(
                null, EncryptionConstants._ATT_ID));

            NodeList encryptionPropertyList =
                element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
            for(int i = 0; i < encryptionPropertyList.getLength(); i++) {
                Node n = encryptionPropertyList.item(i);
                if (null != n) {
                    result.addEncryptionProperty(
                        newEncryptionProperty((Element) n));
                }
            }

            return (result);
        }

        /**
         *
         */
        // <element name='EncryptionProperty' type='xenc:EncryptionPropertyType'/>
        // <complexType name='EncryptionPropertyType' mixed='true'>
        //     <choice maxOccurs='unbounded'>
        //         <any namespace='##other' processContents='lax'/>
        //     </choice>
        //     <attribute name='Target' type='anyURI' use='optional'/>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <anyAttribute namespace="http://www.w3.org/XML/1998/namespace"/>
        // </complexType>
        EncryptionProperty newEncryptionProperty(Element element) throws
                XMLEncryptionException {
            EncryptionProperty result = newEncryptionProperty();

            try {
                result.setTarget(new URI(
                    element.getAttributeNS(
                        null, EncryptionConstants._ATT_TARGET)).toString());
            } catch (URI.MalformedURIException mfue) {
                // do nothing
            }
            result.setId(element.getAttributeNS(
                null, EncryptionConstants._ATT_ID));
            // TODO: Make this lot work...
            // <anyAttribute namespace="http://www.w3.org/XML/1998/namespace"/>

            // TODO: Make this work...
            // <any namespace='##other' processContents='lax'/>

            return (result);
        }

        /**
         *
         */
        // <element name='ReferenceList'>
        //     <complexType>
        //         <choice minOccurs='1' maxOccurs='unbounded'>
        //             <element name='DataReference' type='xenc:ReferenceType'/>
        //             <element name='KeyReference' type='xenc:ReferenceType'/>
        //         </choice>
        //     </complexType>
        // </element>
        ReferenceList newReferenceList(Element element) throws
                XMLEncryptionException {
            int type = 0;
            if (null != element.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS, 
                EncryptionConstants._TAG_DATAREFERENCE).item(0)) {
                type = ReferenceList.DATA_REFERENCE;
            } else if (null != element.getElementsByTagNameNS(
                EncryptionConstants.EncryptionSpecNS,
                EncryptionConstants._TAG_KEYREFERENCE).item(0)) {
                type = ReferenceList.KEY_REFERENCE;
            } else {
                // complain
            }

            ReferenceList result = newReferenceList(type);
            NodeList list = null;
            switch (type) {
                case ReferenceList.DATA_REFERENCE:
                list = element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_DATAREFERENCE);
                for (int i = 0; i < list.getLength() ; i++) {
                    String uri = null;
                    try {
                        uri = new URI(
                            ((Element) list.item(0)).getNodeValue()).toString();
                    } catch (URI.MalformedURIException mfue) {
                    }
                    result.add(ReferenceList.newDataReference(uri));
                }
                case ReferenceList.KEY_REFERENCE:
                list = element.getElementsByTagNameNS(
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_KEYREFERENCE);
                for (int i = 0; i < list.getLength() ; i++) {
                    String uri = null;
                    try {
                        uri = new URI(
                            ((Element) list.item(0)).getNodeValue()).toString();
                    } catch (URI.MalformedURIException mfue) {
                    }
                    result.add(ReferenceList.newKeyReference(uri));
                }
            }

            return (result);
        }

        /**
         *
         */
        Transforms newTransforms(Element element) {
            return (null);
        }

        /**
         *
         */
        Element toElement(AgreementMethod agreementMethod) {
            return ((AgreementMethodImpl) agreementMethod).toElement();
        }

        /**
         *
         */
        Element toElement(CipherData cipherData) {
            return ((CipherDataImpl) cipherData).toElement();
        }

        /**
         *
         */
        Element toElement(CipherReference cipherReference) {
            return ((CipherReferenceImpl) cipherReference).toElement();
        }

        /**
         *
         */
        Element toElement(CipherValue cipherValue) {
            return ((CipherValueImpl) cipherValue).toElement();
        }

        /**
         *
         */
        Element toElement(EncryptedData encryptedData) {
            return ((EncryptedDataImpl) encryptedData).toElement();
        }

        /**
         *
         */
        Element toElement(EncryptedKey encryptedKey) {
            return ((EncryptedKeyImpl) encryptedKey).toElement();
        }

        /**
         *
         */
        Element toElement(EncryptionMethod encryptionMethod) {
            return ((EncryptionMethodImpl) encryptionMethod).toElement();
        }

        /**
         *
         */
        Element toElement(EncryptionProperties encryptionProperties) {
            return ((EncryptionPropertiesImpl) encryptionProperties).toElement();
        }

        /**
         *
         */
        Element toElement(EncryptionProperty encryptionProperty) {
            return ((EncryptionPropertyImpl) encryptionProperty).toElement();
        }

        Element toElement(ReferenceList referenceList) {
            // NOTE: ///////////////////////////////////////////////////////////
            // TODO: Complete
            return (null);
        }

        /**
         *
         */
        Element toElement(Transforms transforms) {
            return ((TransformsImpl) transforms).toElement();
        }

        // <element name="AgreementMethod" type="xenc:AgreementMethodType"/>
        // <complexType name="AgreementMethodType" mixed="true">
        //     <sequence>
        //         <element name="KA-Nonce" minOccurs="0" type="base64Binary"/>
        //         <!-- <element ref="ds:DigestMethod" minOccurs="0"/> -->
        //         <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
        //         <element name="OriginatorKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
        //         <element name="RecipientKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
        //     </sequence>
        //     <attribute name="Algorithm" type="anyURI" use="required"/>
        // </complexType>
        private class AgreementMethodImpl implements AgreementMethod {
            private byte[] kaNonce = null;
            private List agreementMethodInformation = null;
            private KeyInfo originatorKeyInfo = null;
            private KeyInfo recipientKeyInfo = null;
            private String algorithmURI = null;

            public AgreementMethodImpl(String algorithm) {
                agreementMethodInformation = new LinkedList();
                URI tmpAlgorithm = null;
                try {
                    tmpAlgorithm = new URI(algorithm);
                } catch (URI.MalformedURIException fmue) {
                    //complain?
                }
                algorithmURI = tmpAlgorithm.toString();
            }

            public byte[] getKANonce() {
                return (kaNonce);
            }

            public void setKANonce(byte[] kanonce) {
                kaNonce = kanonce;
            }

            public Iterator getAgreementMethodInformation() {
                return (agreementMethodInformation.iterator());
            }

            public void addAgreementMethodInformation(Element info) {
                agreementMethodInformation.add(info);
            }

            public void revoveAgreementMethodInformation(Element info) {
                agreementMethodInformation.remove(info);
            }

            public KeyInfo getOriginatorKeyInfo() {
                return (originatorKeyInfo);
            }

            public void setOriginatorKeyInfo(KeyInfo keyInfo) {
                originatorKeyInfo = keyInfo;
            }

            public KeyInfo getRecipientKeyInfo() {
                return (recipientKeyInfo);
            }

            public void setRecipientKeyInfo(KeyInfo keyInfo) {
                recipientKeyInfo = keyInfo;
            }

            public String getAlgorithm() {
                return (algorithmURI);
            }

            public void setAlgorithm(String algorithm) {
                URI tmpAlgorithm = null;
                try {
                    tmpAlgorithm = new URI(algorithm);
                } catch (URI.MalformedURIException mfue) {
                    //complain
                }
                algorithm = tmpAlgorithm.toString();
            }

            // <element name="AgreementMethod" type="xenc:AgreementMethodType"/>
            // <complexType name="AgreementMethodType" mixed="true">
            //     <sequence>
            //         <element name="KA-Nonce" minOccurs="0" type="base64Binary"/>
            //         <!-- <element ref="ds:DigestMethod" minOccurs="0"/> -->
            //         <any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
            //         <element name="OriginatorKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
            //         <element name="RecipientKeyInfo" minOccurs="0" type="ds:KeyInfoType"/>
            //     </sequence>
            //     <attribute name="Algorithm" type="anyURI" use="required"/>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, 
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_AGREEMENTMETHOD);
                result.setAttributeNS(
                    null, EncryptionConstants._ATT_ALGORITHM, algorithmURI);
                if (null != kaNonce) {
                    result.appendChild(
                        ElementProxy.createElementForFamily(
                            contextDocument, 
                            EncryptionConstants.EncryptionSpecNS, 
                            EncryptionConstants._TAG_KA_NONCE)).appendChild(
                            contextDocument.createTextNode(new String(kaNonce)));
                }
                if (!agreementMethodInformation.isEmpty()) {
                    Iterator itr = agreementMethodInformation.iterator();
                    while (itr.hasNext()) {
                        result.appendChild((Element) itr.next());
                    }
                }
                if (null != originatorKeyInfo) {
                    // TODO: complete
                }
                if (null != recipientKeyInfo) {
                    // TODO: complete
                }

                return (result);
            }
        }

        // <element name='CipherData' type='xenc:CipherDataType'/>
        // <complexType name='CipherDataType'>
        //     <choice>
        //         <element name='CipherValue' type='base64Binary'/>
        //         <element ref='xenc:CipherReference'/>
        //     </choice>
        // </complexType>
        private class CipherDataImpl implements CipherData {
            private static final String valueMessage =
                "Data type is reference type.";
            private static final String referenceMessage =
                "Data type is value type.";
            private CipherValue cipherValue = null;
            private CipherReference cipherReference = null;
            private int cipherType = Integer.MIN_VALUE;

            public CipherDataImpl(int type) {
                cipherType = type;
            }

            public CipherValue getCipherValue() {
                return (cipherValue);
            }

            public void setCipherValue(CipherValue value) throws
                    XMLEncryptionException {
                if (cipherType == REFERENCE_TYPE) {
                    throw new XMLEncryptionException("empty",
                        new UnsupportedOperationException(valueMessage));
                }

                cipherValue = value;
            }

            public CipherReference getCipherReference() {
                return (cipherReference);
            }

            public void setCipherReference(CipherReference reference) throws
                    XMLEncryptionException {
                if (cipherType == VALUE_TYPE) {
                    throw new XMLEncryptionException("empty",
                        new UnsupportedOperationException(referenceMessage));
                }

                cipherReference = reference;
            }

            public int getDataType() {
                return (cipherType);
            }

            // <element name='CipherData' type='xenc:CipherDataType'/>
            // <complexType name='CipherDataType'>
            //     <choice>
            //         <element name='CipherValue' type='base64Binary'/>
            //         <element ref='xenc:CipherReference'/>
            //     </choice>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, 
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_CIPHERDATA);
                if (cipherType == VALUE_TYPE) {
                    result.appendChild(
                        ((CipherValueImpl) cipherValue).toElement());
                } else if (cipherType == REFERENCE_TYPE) {
                    result.appendChild(
                        ((CipherReferenceImpl) cipherReference).toElement());
                } else {
                    // complain
                }

                return (result);
            }
        }

        // <element name='CipherReference' type='xenc:CipherReferenceType'/>
        // <complexType name='CipherReferenceType'>
        //     <sequence>
        //         <element name='Transforms' type='xenc:TransformsType' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='URI' type='anyURI' use='required'/>
        // </complexType>
        private class CipherReferenceImpl implements CipherReference {
            private String referenceURI = null;
            private Transforms referenceTransforms = null;

            public CipherReferenceImpl(String uri) {
                URI tmpReferenceURI = null;
                try {
                    tmpReferenceURI = new URI(uri);
                } catch (URI.MalformedURIException mfue) {
                    // complain
                }
                referenceURI = tmpReferenceURI.toString();
            }

            public String getURI() {
                return (referenceURI);
            }

            public Transforms getTransforms() {
                return (referenceTransforms);
            }

            public void setTransforms(Transforms transforms) {
                referenceTransforms = transforms;
            }

            // <element name='CipherReference' type='xenc:CipherReferenceType'/>
            // <complexType name='CipherReferenceType'>
            //     <sequence>
            //         <element name='Transforms' type='xenc:TransformsType' minOccurs='0'/>
            //     </sequence>
            //     <attribute name='URI' type='anyURI' use='required'/>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, 
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_CIPHERREFERENCE);
                result.setAttributeNS(
                    null, EncryptionConstants._ATT_URI, referenceURI);
                if (null != referenceTransforms) {
                    result.appendChild(
                        ((TransformsImpl) referenceTransforms).toElement());
                }

                return (result);
            }
        }

        private class CipherValueImpl implements CipherValue {
            private byte[] cipherValue = null;

            public CipherValueImpl(byte[] value) {
                cipherValue = value;
            }

            public CipherValueImpl(String value) {
                cipherValue = value.getBytes();
            }

            public byte[] getValue() {
                return (cipherValue);
            }

            public void setValue(byte[] value) {
                cipherValue = value;
            }

            public void setValue(String value) {
                cipherValue = value.getBytes();
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_CIPHERVALUE);
                result.appendChild(contextDocument.createTextNode(
                    new String(cipherValue)));

                return (result);
            }
        }

        // <complexType name='EncryptedType' abstract='true'>
        //     <sequence>
        //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
        //             minOccurs='0'/>
        //         <element ref='ds:KeyInfo' minOccurs='0'/>
        //         <element ref='xenc:CipherData'/>
        //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <attribute name='Type' type='anyURI' use='optional'/>
        //     <attribute name='MimeType' type='string' use='optional'/>
        //     <attribute name='Encoding' type='anyURI' use='optional'/>
        // </complexType>
        // <element name='EncryptedData' type='xenc:EncryptedDataType'/>
        // <complexType name='EncryptedDataType'>
        //     <complexContent>
        //         <extension base='xenc:EncryptedType'/>
        //     </complexContent>
        // </complexType>
        private class EncryptedDataImpl extends EncryptedTypeImpl implements
                EncryptedData {
            public EncryptedDataImpl(CipherData data) {
                super(data);
            }

            // <complexType name='EncryptedType' abstract='true'>
            //     <sequence>
            //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
            //             minOccurs='0'/>
            //         <element ref='ds:KeyInfo' minOccurs='0'/>
            //         <element ref='xenc:CipherData'/>
            //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
            //     </sequence>
            //     <attribute name='Id' type='ID' use='optional'/>
            //     <attribute name='Type' type='anyURI' use='optional'/>
            //     <attribute name='MimeType' type='string' use='optional'/>
            //     <attribute name='Encoding' type='anyURI' use='optional'/>
            // </complexType>
            // <element name='EncryptedData' type='xenc:EncryptedDataType'/>
            // <complexType name='EncryptedDataType'>
            //     <complexContent>
            //         <extension base='xenc:EncryptedType'/>
            //     </complexContent>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTEDDATA);

                if (null != super.getId()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_ID, super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_TYPE,
                        super.getType().toString());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_MIMETYPE, 
                        super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_ENCODING, 
                        super.getEncoding().toString());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl)
                        super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    // TODO: complete
                }
                result.appendChild(
                    ((CipherDataImpl) super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl)
                        super.getEncryptionProperties()).toElement());
                }

                return (result);
            }
        }

        // <complexType name='EncryptedType' abstract='true'>
        //     <sequence>
        //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
        //             minOccurs='0'/>
        //         <element ref='ds:KeyInfo' minOccurs='0'/>
        //         <element ref='xenc:CipherData'/>
        //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <attribute name='Type' type='anyURI' use='optional'/>
        //     <attribute name='MimeType' type='string' use='optional'/>
        //     <attribute name='Encoding' type='anyURI' use='optional'/>
        // </complexType>
        // <element name='EncryptedKey' type='xenc:EncryptedKeyType'/>
        // <complexType name='EncryptedKeyType'>
        //     <complexContent>
        //         <extension base='xenc:EncryptedType'>
        //             <sequence>
        //                 <element ref='xenc:ReferenceList' minOccurs='0'/>
        //                 <element name='CarriedKeyName' type='string' minOccurs='0'/>
        //             </sequence>
        //             <attribute name='Recipient' type='string' use='optional'/>
        //         </extension>
        //     </complexContent>
        // </complexType>
        private class EncryptedKeyImpl extends EncryptedTypeImpl implements
                EncryptedKey {
            private String keyRecipient = null;
            private ReferenceList referenceList = null;
            private String carriedName = null;

            public EncryptedKeyImpl(CipherData data) {
                super(data);
            }

            public String getRecipient() {
                return (keyRecipient);
            }

            public void setRecipient(String recipient) {
                keyRecipient = recipient;
            }

            public ReferenceList getReferenceList() {
                return (referenceList);
            }

            public void setReferenceList(ReferenceList list) {
                referenceList = list;
            }

            public String getCarriedName() {
                return (carriedName);
            }

            public void setCarriedName(String name) {
                carriedName = name;
            }

            // <complexType name='EncryptedType' abstract='true'>
            //     <sequence>
            //         <element name='EncryptionMethod' type='xenc:EncryptionMethodType'
            //             minOccurs='0'/>
            //         <element ref='ds:KeyInfo' minOccurs='0'/>
            //         <element ref='xenc:CipherData'/>
            //         <element ref='xenc:EncryptionProperties' minOccurs='0'/>
            //     </sequence>
            //     <attribute name='Id' type='ID' use='optional'/>
            //     <attribute name='Type' type='anyURI' use='optional'/>
            //     <attribute name='MimeType' type='string' use='optional'/>
            //     <attribute name='Encoding' type='anyURI' use='optional'/>
            // </complexType>
            // <element name='EncryptedKey' type='xenc:EncryptedKeyType'/>
            // <complexType name='EncryptedKeyType'>
            //     <complexContent>
            //         <extension base='xenc:EncryptedType'>
            //             <sequence>
            //                 <element ref='xenc:ReferenceList' minOccurs='0'/>
            //                 <element name='CarriedKeyName' type='string' minOccurs='0'/>
            //             </sequence>
            //             <attribute name='Recipient' type='string' use='optional'/>
            //         </extension>
            //     </complexContent>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTEDDATA);

                if (null != super.getId()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_ID, super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(
                        null, EncryptionConstants._ATT_TYPE, 
                        super.getType().toString());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(null, 
                        EncryptionConstants._ATT_MIMETYPE, super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(null, Constants._ATT_ENCODING,
                        super.getEncoding().toString());
                }
                if (null != getRecipient()) {
                    result.setAttributeNS(null, 
                        EncryptionConstants._ATT_RECIPIENT, getRecipient());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl)
                        super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    // TODO: complete
                }
                result.appendChild(
                    ((CipherDataImpl) super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl)
                        super.getEncryptionProperties()).toElement());
                }
                if (!referenceList.isEmpty()) {
                    // TODO: complete
                }
                if (null != carriedName) {
                    result.appendChild(
                        ElementProxy.createElementForFamily(contextDocument, 
                            EncryptionConstants.EncryptionSpecNS, 
                            EncryptionConstants._TAG_CARRIEDKEYNAME).appendChild(
                            contextDocument.createTextNode(carriedName)));
                }

                return (result);
            }
        }

        private abstract class EncryptedTypeImpl {
            private String id =  null;
            private String type = null;
            private String mimeType = null;
            private String encoding = null;
            private EncryptionMethod encryptionMethod = null;
            private KeyInfo keyInfo = null;
            private CipherData cipherData = null;
            private EncryptionProperties encryptionProperties = null;

            protected EncryptedTypeImpl(CipherData data) {
                cipherData = data;
            }

            public String getId() {
                return (id);
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return (type);
            }

            public void setType(String type) {
                URI tmpType = null;
                try {
                    tmpType = new URI(type);
                } catch (URI.MalformedURIException mfue) {
                    // complain
                }
                this.type = tmpType.toString();
            }

            public String getMimeType() {
                return (mimeType);
            }

            public void setMimeType(String type) {
                mimeType = type;
            }

            public String getEncoding() {
                return (encoding);
            }

            public void setEncoding(String encoding) {
                URI tmpEncoding = null;
                try {
                    tmpEncoding = new URI(encoding);
                } catch (URI.MalformedURIException mfue) {
                    // complain
                }
                this.encoding = tmpEncoding.toString();
            }

            public EncryptionMethod getEncryptionMethod() {
                return (encryptionMethod);
            }

            public void setEncryptionMethod(EncryptionMethod method) {
                encryptionMethod = method;
            }

            public KeyInfo getKeyInfo() {
                return (keyInfo);
            }

            public void setKeyInfo(KeyInfo info) {
                keyInfo = info;
            }

            public CipherData getCipherData() {
                return (cipherData);
            }

            public EncryptionProperties getEncryptionProperties() {
                return (encryptionProperties);
            }

            public void setEncryptionProperties(
                    EncryptionProperties properties) {
                encryptionProperties = properties;
            }
        }

        // <complexType name='EncryptionMethodType' mixed='true'>
        //     <sequence>
        //         <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
        //         <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
        //         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
        //     </sequence>
        //     <attribute name='Algorithm' type='anyURI' use='required'/>
        // </complexType>
        private class EncryptionMethodImpl implements EncryptionMethod {
            private String algorithm = null;
            private int keySize = Integer.MIN_VALUE;
            private byte[] oaepParams = null;
            private List encryptionMethodInformation = null;

            public EncryptionMethodImpl(String algorithm) {
                URI tmpAlgorithm = null;
                try {
                    tmpAlgorithm = new URI(algorithm);
                } catch (URI.MalformedURIException mfue) {
                    // complain
                }
                this.algorithm = tmpAlgorithm.toString();
                encryptionMethodInformation = new LinkedList();
            }

            public String getAlgorithm() {
                return (algorithm);
            }

            public int getKeySize() {
                return (keySize);
            }

            public void setKeySize(int size) {
                keySize = size;
            }

            public byte[] getOAEPparams() {
                return (oaepParams);
            }

            public void setOAEPparams(byte[] params) {
                oaepParams = params;
            }

            public Iterator getEncryptionMethodInformation() {
                return (encryptionMethodInformation.iterator());
            }

            public void addEncryptionMethodInformation(Element info) {
                encryptionMethodInformation.add(info);
            }

            public void removeEncryptionMethodInformation(Element info) {
                encryptionMethodInformation.remove(info);
            }

            // <complexType name='EncryptionMethodType' mixed='true'>
            //     <sequence>
            //         <element name='KeySize' minOccurs='0' type='xenc:KeySizeType'/>
            //         <element name='OAEPparams' minOccurs='0' type='base64Binary'/>
            //         <any namespace='##other' minOccurs='0' maxOccurs='unbounded'/>
            //     </sequence>
            //     <attribute name='Algorithm' type='anyURI' use='required'/>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTIONMETHOD);
                result.setAttributeNS(null, EncryptionConstants._ATT_ALGORITHM, 
                    algorithm.toString());
                if (keySize > 0) {
                    result.appendChild(
                        ElementProxy.createElementForFamily(contextDocument, 
                            EncryptionConstants.EncryptionSpecNS, 
                            EncryptionConstants._TAG_KEYSIZE).appendChild(
                            contextDocument.createTextNode(
                                String.valueOf(keySize))));
                }
                if (null != oaepParams) {
                    result.appendChild(
                        ElementProxy.createElementForFamily(contextDocument, 
                            EncryptionConstants.EncryptionSpecNS, 
                            EncryptionConstants._TAG_OAEPPARAMS).appendChild(
                            contextDocument.createTextNode(
                                new String(oaepParams))));
                }
                if (!encryptionMethodInformation.isEmpty()) {
                    Iterator itr = encryptionMethodInformation.iterator();
                    result.appendChild((Element) itr.next());
                }

                return (result);
            }
        }

        // <element name='EncryptionProperties' type='xenc:EncryptionPropertiesType'/>
        // <complexType name='EncryptionPropertiesType'>
        //     <sequence>
        //         <element ref='xenc:EncryptionProperty' maxOccurs='unbounded'/>
        //     </sequence>
        //     <attribute name='Id' type='ID' use='optional'/>
        // </complexType>
        private class EncryptionPropertiesImpl implements EncryptionProperties {
            private String id = null;
            private List encryptionProperties = null;

            public EncryptionPropertiesImpl() {
                encryptionProperties = new LinkedList();
            }

            public String getId() {
                return (id);
            }

            public void setId(String id) {
                this.id = id;
            }

            public Iterator getEncryptionProperties() {
                return (encryptionProperties.iterator());
            }

            public void addEncryptionProperty(EncryptionProperty property) {
                encryptionProperties.add(property);
            }

            public void removeEncryptionProperty(EncryptionProperty property) {
                encryptionProperties.remove(property);
            }

            // <element name='EncryptionProperties' type='xenc:EncryptionPropertiesType'/>
            // <complexType name='EncryptionPropertiesType'>
            //     <sequence>
            //         <element ref='xenc:EncryptionProperty' maxOccurs='unbounded'/>
            //     </sequence>
            //     <attribute name='Id' type='ID' use='optional'/>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
                if (null != id) {
                    result.setAttributeNS(null, EncryptionConstants._ATT_ID, id);
                }
                Iterator itr = getEncryptionProperties();
                while (itr.hasNext()) {
                    result.appendChild(((EncryptionPropertyImpl)
                        itr.next()).toElement());
                }

                return (result);
            }
        }

        // <element name='EncryptionProperty' type='xenc:EncryptionPropertyType'/>
        // <complexType name='EncryptionPropertyType' mixed='true'>
        //     <choice maxOccurs='unbounded'>
        //         <any namespace='##other' processContents='lax'/>
        //     </choice>
        //     <attribute name='Target' type='anyURI' use='optional'/>
        //     <attribute name='Id' type='ID' use='optional'/>
        //     <anyAttribute namespace="http://www.w3.org/XML/1998/namespace"/>
        // </complexType>
        private class EncryptionPropertyImpl implements EncryptionProperty {
            private String target = null;
            private String id = null;
            private String attributeName = null;
            private String attributeValue = null;
            private List encryptionInformation = null;

            public EncryptionPropertyImpl() {
                encryptionInformation = new LinkedList();
            }

            public String getTarget() {
                return (target);
            }

            public void setTarget(String target) {
                URI tmpTarget = null;
                try {
                    tmpTarget = new URI(target);
                } catch (URI.MalformedURIException mfue) {
                    // complain
                }
                this.target = tmpTarget.toString();
            }

            public String getId() {
                return (id);
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAttribute(String attribute) {
                return (attributeValue);
            }

            public void setAttribute(String attribute, String value) {
                attributeName = attribute;
                attributeValue = value;
            }

            public Iterator getEncryptionInformation() {
                return (encryptionInformation.iterator());
            }

            public void addEncryptionInformation(Element info) {
                encryptionInformation.add(info);
            }

            public void removeEncryptionInformation(Element info) {
                encryptionInformation.remove(info);
            }

            // <element name='EncryptionProperty' type='xenc:EncryptionPropertyType'/>
            // <complexType name='EncryptionPropertyType' mixed='true'>
            //     <choice maxOccurs='unbounded'>
            //         <any namespace='##other' processContents='lax'/>
            //     </choice>
            //     <attribute name='Target' type='anyURI' use='optional'/>
            //     <attribute name='Id' type='ID' use='optional'/>
            //     <anyAttribute namespace="http://www.w3.org/XML/1998/namespace"/>
            // </complexType>
            Element toElement() {
                Element result = ElementProxy.createElementForFamily(
                    contextDocument, EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
                if (null != target) {
                    result.setAttributeNS(null, EncryptionConstants._ATT_TARGET, 
                        target.toString());
                }
                if (null != id) {
                    result.setAttributeNS(null, EncryptionConstants._ATT_ID, 
                        id);
                }
                // TODO: figure out the anyAttribyte stuff...
                // TODO: figure out the any stuff...

                return (result);
            }
        }

        // <complexType name='TransformsType'>
        //     <sequence>
        //         <element ref='ds:Transform' maxOccurs='unbounded'/>
        //     </sequence>
        // </complexType>
        private class TransformsImpl implements Transforms {
            private List transforms = null;

            public TransformsImpl() {
                transforms = new LinkedList();
            }

            public Iterator getTransforms() {
                return (transforms.iterator());
            }

            public void addTransform(Transform transform) {
                transforms.add(transform);
            }

            public void removeTransform(Transform transform) {
                transforms.remove(transform);
            }

            // <complexType name='TransformsType'>
            //     <sequence>
            //         <element ref='ds:Transform' maxOccurs='unbounded'/>
            //     </sequence>
            // </complexType>
            Element toElement() {
                Element result = null;

                result = ElementProxy.createElementForFamily(contextDocument, 
                    EncryptionConstants.EncryptionSpecNS, 
                    EncryptionConstants._TAG_TRANSFORMS);
                // TODO: figure out how to do this ...

                return (result);
            }
        }
    }
}
