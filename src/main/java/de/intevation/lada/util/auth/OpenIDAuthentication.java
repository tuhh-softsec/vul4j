/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.util.auth;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.net.URLDecoder;

import javax.inject.Inject;
import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.util.annotation.AuthenticationConfig;

import org.openid4java.association.AssociationSessionType;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.message.ParameterList;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;
import org.openid4java.message.AuthRequest;

import org.apache.log4j.Logger;

public class OpenIDAuthentication implements Authentication {

    /** The name of the header field used to transport OpenID parameters.*/
    private static final String OID_HEADER_FIELD= "X-OPENID-PARAMS";

    /** The identity provider we accept here. */
    private static final String IDENTITY_PROVIDER =
        "http://localhost:8087/account";

    /** This is currently a faked dummy */
    private static final String RETURN_URL =
        "http://localhost:8086/consumer-servlet/consumer?is_return=true";

    private static final Logger logger =
        Logger.getLogger(OpenIDAuthentication.class);

    private ConsumerManager manager;

    private Map<String,String> idParams;

    boolean discoveryDone = false;

    private DiscoveryInformation discovered;

    private boolean discoverServer() {
        /* Perform discovery on the configured IDENTITY_PROVIDER */
        List discoveries = null;
        try {
            discoveries = manager.discover(IDENTITY_PROVIDER);
        } catch (DiscoveryException e) {
            logger.debug("Discovery failed: " + e.getMessage());
            return false;
        }

        if (discoveries == null || discoveries.isEmpty()) {
            logger.error(
                    "Failed discovery step. OpenID provider unavailable?");
            return false;
        }

        /* Add association for the discovered information */
        discovered = manager.associate(discoveries);

        /* Validate the parameters. */
        logger.debug("After discovery.");
        try {
            AuthRequest authReq = manager.authenticate(discovered, RETURN_URL);
            logger.debug("Authenticate with: " + authReq.getDestinationUrl(true));
        } catch (MessageException e) {
            logger.debug("Failed to create the Authentication request: " +
                    e.getMessage());
        } catch (ConsumerException e) {
            logger.debug("Error in consumer manager: " +
                    e.getMessage());
        }
        logger.debug("After authenticate.");
        return true;
    }

    public OpenIDAuthentication() {
        manager = new ConsumerManager();
        /* TODO: Check for alternative configs. */
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new InMemoryNonceVerifier(50000));
        manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
        discoveryDone = discoverServer();
    }

    /** Split up the OpenID response query provided in the header.
     *
     * @param responseQuery The query provided in the header field.
     * @return The query as ParameterList or null on error.
     */
    private ParameterList splitParams(String responseQuery) {
        Map<String, String> queryMap =
            new LinkedHashMap<String, String>();
        final String[] pairs = responseQuery.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            if (idx <= 0) {
                logger.debug("Invalid query.");
                return null;
            }
            try {
                final String key = URLDecoder.decode(
                        pair.substring(0, idx), "UTF-8");

                if (queryMap.containsKey(key)) {
                    logger.debug("Invalid query. Duplicate key: " + key);
                    return null;
                }
                final String value = URLDecoder.decode(
                        pair.substring(idx + 1), "UTF-8");
                queryMap.put(key, value);
            } catch (java.io.UnsupportedEncodingException e) {
                logger.error("UTF-8 unkown?!");
                return null;
            }
        }
        if (queryMap.isEmpty()) {
            logger.debug("Empty query.");
            return null;
        }
        return new ParameterList(queryMap);
    }

    private boolean checkOpenIDHeader(HttpHeaders headers) {
        /* First check if there are is anything provided */
        List<String> oidParamString = headers.getRequestHeader(
                OID_HEADER_FIELD);
        if (oidParamString == null) {
            logger.debug("Header " + OID_HEADER_FIELD + " not provided.");
            return false;
        }
        if (oidParamString.size() != 1) {
            logger.debug("Found " + oidParamString.size() + " openid headers.");
            return false;
        }

        /* Parse the parameters. Do it first to avoid a useless discovery. */
        ParameterList oidParams = splitParams(oidParamString.get(0));
        if (oidParams == null) {
            return false;
        }

        VerificationResult verification = null;
        try {
            verification = manager.verify(RETURN_URL, oidParams, discovered);
        } catch (MessageException e) {
            logger.debug("Verification failed: " + e.getMessage());
            return false;
        } catch (DiscoveryException e) {
            logger.debug("Verification discovery exception: " + e.getMessage());
            return false;
        } catch (AssociationException e) {
            logger.debug("Verification assoc exception: " + e.getMessage());
            return false;
        }


        /* See what could be verified */
        Identifier verified = verification.getVerifiedId();
        if (verified == null) {
            logger.debug("Failed to verify Identity information: " +
                    verification.getStatusMsg());
            return false;
        }

        logger.debug("Verified user: " + verified);

        return true;
    }

    @Override
    public boolean isAuthenticated(HttpHeaders headers) {
        if (!discoveryDone) {
            discoveryDone = discoverServer();
        }
        if (!discoveryDone) {
            return false;
        }
        if (checkOpenIDHeader(headers)) {
            /** Successfully authenticated. */
            return true;
        } else {

            return false;
        }
    }
}
