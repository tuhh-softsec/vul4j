/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */

package de.intevation.lada.util.auth;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.net.URLDecoder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

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

/** ServletFilter used for OpenID authentification. */
@WebFilter("/*")
public class OpenIDFilter implements Filter
{
    private static Logger logger = Logger.getLogger(OpenIDFilter.class);

    private ConsumerManager manager;

    /* This should be moved into a map <server->discovered>
     * as we currently only supporting one server this is static. */
    boolean discoveryDone = false;
    private DiscoveryInformation discovered;
    private String authRequestURL;

    /** TODO: get this from config. */
    /** The name of the header field used to transport OpenID parameters.*/
    private static final String OID_HEADER_FIELD= "X-OPENID-PARAMS";

    /** The identity provider we accept here. */
    private static final String IDENTITY_PROVIDER =
        "http://localhost:8087/account";

    /** This is currently a faked dummy */
    private static final String RETURN_URL =
        "http://localhost:8086/consumer-servlet/consumer?is_return=true";

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
            authRequestURL = authReq.getDestinationUrl(true);
            logger.debug("Authenticate with: " + authRequestURL);
        } catch (MessageException e) {
            logger.debug("Failed to create the Authentication request: " +
                    e.getMessage());
        } catch (ConsumerException e) {
            logger.debug("Error in consumer manager: " +
                    e.getMessage());
        }
        return true;
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

    private boolean checkOpenIDHeader(ServletRequest req) {

        HttpServletRequest hReq = (HttpServletRequest) req;
        /* First check if the header is provided at all */
        String oidParamString = hReq.getHeader(OID_HEADER_FIELD);

        if (oidParamString == null) {
            logger.debug("Header " + OID_HEADER_FIELD + " not provided.");
            return false;
        }

        /* Parse the parameters to a map for openid4j */
        ParameterList oidParams = splitParams(oidParamString);
        if (oidParams == null) {
            return false;
        }

        /* Verify against the discovered server. */
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
    public void init(FilterConfig config)
    throws ServletException
    {
        manager = new ConsumerManager();
        /* TODO: Check for alternative configs. */
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new InMemoryNonceVerifier(50000));
        manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
        discoveryDone = discoverServer();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException, ServletException
    {
        if (!discoveryDone) {
            discoveryDone = discoverServer();
        }
        if (discoveryDone && checkOpenIDHeader(req)) {
            /** Successfully authenticated. */
            chain.doFilter(req, resp);
        }
        ((HttpServletResponse) resp).sendError(401, "{\"success\":false,\"message\":\"699\",\"data\":" +
                "\"" + authRequestURL + "\",\"errors\":{},\"warnings\":{}," +
                "\"readonly\":false,\"totalCount\":0}");
    }
    @Override
    public void destroy()
    {
    }
};
