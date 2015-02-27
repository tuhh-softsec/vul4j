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
import java.util.Date;
import java.util.Properties;

import java.io.InputStream;
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
import org.openid4java.consumer.AbstractNonceVerifier;
import org.openid4java.message.ParameterList;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;
import org.openid4java.message.AuthRequest;

/** ServletFilter used for OpenID authentification. */
@WebFilter("/*")
public class OpenIDFilter implements Filter {

    private static final String CONFIG_FILE = "/openid.properties";

    /** The name of the header field used to transport OpenID parameters.*/
    private static final String OID_HEADER_DEFAULT = "X-OPENID-PARAMS";
    private String oidHeader;

    /** The identity provider we accept here. */
    private static final String IDENTITY_PROVIDER_DEFAULT =
        "https://localhost/openid/";
    private String providerUrl;

    private static final int SESSION_TIMEOUT_DEFAULT_MINUTES = 60;
    private int sessionTimeout;

    private static Logger logger = Logger.getLogger(OpenIDFilter.class);

    /** Nonce verifier to allow a session based on openid information.
     *
     * Usually one would create a session for the user but this would not
     * be an advantage here as we want to transport the session in a header
     * anyway.
     *
     * A nonce will be valid as long as as the maxAge is not reached.
     * This is implemented by the basis verifier.
     * We only implement seed no mark that we accept nonce's multiple
     * times.
     */
    private class SessionNonceVerifier extends AbstractNonceVerifier {
        public SessionNonceVerifier(int maxAge) {
            super(maxAge);
        }

        @Override
        protected int seen(Date now, String opUrl, String nonce) {
            return OK;
        }
    };

    private ConsumerManager manager;

    /* This should be moved into a map <server->discovered>
     * as we currently only supporting one server this is static. */
    boolean discoveryDone = false;
    private DiscoveryInformation discovered;

    private boolean discoverServer() {
        /* Perform discovery on the configured providerUrl */
        List discoveries = null;
        try {
            discoveries = manager.discover(providerUrl);
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

        return true;
    }

    /** Split up the OpenID response query provided in the header.
     *
     * @param responseQuery The query provided in the header field.
     * @return The query as ParameterList or null on error.
     */
    private ParameterList splitParams(String responseQuery) {
        if (responseQuery == null) {
            return null;
        }
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
        String oidParamString = hReq.getHeader(oidHeader);

        if (oidParamString == null) {
            logger.debug("Header " + oidHeader + " not provided. Trying params.");
            oidParamString = hReq.getQueryString();
        }

        /* Parse the parameters to a map for openid4j */
        ParameterList oidParams = splitParams(oidParamString);
        if (oidParams == null) {
            return false;
        }

        /* Verify against the discovered server. */
        VerificationResult verification = null;
        /* extract the receiving URL from the HTTP request */
        String receivingURL = hReq.getRequestURL().toString();

        if (!receivingURL.contains("?is_return=true&")) {
            receivingURL += "?is_return=true&";
        }
        /* XXX this is broken and does not work as that information only
         * authenticates this Return url and not any other URL. We have
         * to change this. */
        receivingURL.replace("localhost", "127.0.0.1");

        try {
            verification = manager.verify(receivingURL.toString(), oidParams,
                    discovered);
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
        /* Read config and initialize configuration variables */
        Properties properties = new Properties();
        InputStream stream = null;
        try {
            stream = getClass().getResourceAsStream(CONFIG_FILE);
            properties.load(stream);
            stream.close();
        } catch (java.io.FileNotFoundException e) {
            logger.error ("Failed to find config file: " + CONFIG_FILE);
        } catch (java.io.IOException e) {
            logger.error ("Failed to read config file: " + CONFIG_FILE);
        }
        try {
            sessionTimeout = Integer.parseInt(
                    properties.getProperty("session_timeout_minutes"));
        } catch (NumberFormatException e) {
            sessionTimeout = SESSION_TIMEOUT_DEFAULT_MINUTES;
        }
        oidHeader = properties.getProperty("oidHeader", OID_HEADER_DEFAULT);
        providerUrl = properties.getProperty("identity_provider",
                IDENTITY_PROVIDER_DEFAULT);

        manager = new ConsumerManager();
        /* We probably want to implement our own association store to keep
         * associations persistent. */
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new SessionNonceVerifier(sessionTimeout));
        manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
        discoveryDone = discoverServer();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException, ServletException
    {
        HttpServletRequest hReq = (HttpServletRequest) req;
        HttpServletResponse hResp = (HttpServletResponse) resp;
        if (!discoveryDone) {
            discoveryDone = discoverServer();
        }
        if (discoveryDone && checkOpenIDHeader(req)) {
            /** Successfully authenticated. */
            hResp.addHeader(oidHeader, hReq.getQueryString().replace(
                        "is_return=true",""));
            chain.doFilter(req, resp);
            return;
        }
        String authRequestURL = "Error communicating with openid server";
        if (discoveryDone) {
            /* Get the authentication url for this server. */
            try {
                String returnToUrl = hReq.getRequestURL().toString()
                    + "?is_return=true";
                AuthRequest authReq = manager.authenticate(discovered,
                        returnToUrl);
                authRequestURL = authReq.getDestinationUrl(true);
            } catch (MessageException e) {
                logger.debug("Failed to create the Authentication request: " +
                        e.getMessage());
            } catch (ConsumerException e) {
                logger.debug("Error in consumer manager: " +
                        e.getMessage());
            }
        }
        hResp.sendError(401, "{\"success\":false,\"message\":\"699\",\"data\":" +
                "\"" + authRequestURL + "\",\"errors\":{},\"warnings\":{}," +
                "\"readonly\":false,\"totalCount\":0}");
    }
    @Override
    public void destroy()
    {
    }
};
