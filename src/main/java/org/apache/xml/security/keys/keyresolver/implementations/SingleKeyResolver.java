package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * Resolves a single Key based on the KeyName.
 */
public class SingleKeyResolver extends KeyResolverSpi
{
    private static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(SingleKeyResolver.class);

    private String keyName;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;

    /**
     * Constructor.
     * @param keyName
     * @param publicKey
     */
    public SingleKeyResolver(String keyName, PublicKey publicKey) {
        this.keyName = keyName;
        this.publicKey = publicKey;
    }

    /**
     * Constructor.
     * @param keyName
     * @param privateKey
     */
    public SingleKeyResolver(String keyName, PrivateKey privateKey) {
        this.keyName = keyName;
        this.privateKey = privateKey;
    }

    /**
     * Constructor.
     * @param keyName
     * @param secretKey
     */
    public SingleKeyResolver(String keyName, SecretKey secretKey) {
        this.keyName = keyName;
        this.secretKey = secretKey;
    }

    /**
     * This method returns whether the KeyResolverSpi is able to perform the requested action.
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return whether the KeyResolverSpi is able to perform the requested action.
     */
    public boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME);
    }

    /**
     * Method engineLookupAndResolvePublicKey
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return null if no {@link PublicKey} could be obtained
     * @throws KeyResolverException
     */
    public PublicKey engineLookupAndResolvePublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        if (log.isDebugEnabled()) {
            log.debug("Can I resolve " + element.getTagName() + "?");
        }

        if (publicKey != null 
            && XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME)) {
            String name = element.getFirstChild().getNodeValue();
            if (keyName.equals(name)) {
                return publicKey;
            }
        }

        log.debug("I can't");
        return null;
    }

    /**
     * Method engineResolveX509Certificate
     * @inheritDoc
     * @param element
     * @param baseURI
     * @param storage
     * @throws KeyResolverException
     */
    public X509Certificate engineLookupResolveX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return null;
    }

    /**
     * Method engineResolveSecretKey
     *
     * @param element
     * @param baseURI
     * @param storage
     * @return resolved SecretKey key or null if no {@link SecretKey} could be obtained
     *
     * @throws KeyResolverException
     */
    public SecretKey engineResolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        if (log.isDebugEnabled()) {
            log.debug("Can I resolve " + element.getTagName() + "?");
        }

        if (secretKey != null
            && XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME)) {
            String name = element.getFirstChild().getNodeValue();
            if (keyName.equals(name)) {
                return secretKey;
            }
        }

        log.debug("I can't");
        return null;
    }

    /**
     * Method engineResolvePrivateKey
     * @inheritDoc
     * @param element
     * @param baseURI
     * @param storage
     * @return resolved PrivateKey key or null if no {@link PrivateKey} could be obtained
     * @throws KeyResolverException
     */
    public PrivateKey engineLookupAndResolvePrivateKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        if (log.isDebugEnabled()) {
            log.debug("Can I resolve " + element.getTagName() + "?");
        }

        if (privateKey != null
            && XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_KEYNAME)) {
            String name = element.getFirstChild().getNodeValue();
            if (keyName.equals(name)) {
                return privateKey;
            }
        }

        log.debug("I can't");
        return null;
    }
}
