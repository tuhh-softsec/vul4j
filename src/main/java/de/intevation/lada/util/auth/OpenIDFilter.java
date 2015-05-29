/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */

package de.intevation.lada.util.auth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openid4java.association.AssociationException;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.AbstractNonceVerifier;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

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

    private boolean enabled;

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
        List<?> discoveries = null;
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
                    logger.debug("Duplicate key: " + key + " ignored.");
                    continue;
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

    private boolean checkOpenIDQuery(ServletRequest req) {
        HttpServletRequest hReq = (HttpServletRequest) req;

        String oidParamString = hReq.getQueryString();

        if (oidParamString == null) {
            logger.debug("No query string.");
        }
        return checkOpenIDString(hReq, oidParamString);
    }

    private boolean checkOpenIDHeader(ServletRequest req) {

        HttpServletRequest hReq = (HttpServletRequest) req;
        /* Debug code to dump headers
        Enumeration<String> headerNames = hReq.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.debug("Header: " + headerName);
            Enumeration<String> headers = hReq.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                logger.debug("Value: " + headerValue);
            }
        }
        */
        /* First check if the header is provided at all */
        String oidParamString = hReq.getHeader(oidHeader);

        if (oidParamString == null) {
            logger.debug("Header " + oidHeader + " not provided.");
        }
        return checkOpenIDString(hReq, oidParamString);
    }

    private boolean checkOpenIDString(HttpServletRequest hReq,
                                      String oidParamString) {

        /* Parse the parameters to a map for openid4j */
        ParameterList oidParams = splitParams(oidParamString);
        if (oidParams == null) {
            return false;
        }

        /* Verify against the discovered server. */
        VerificationResult verification = null;
        String receivingURL = oidParams.getParameterValue("openid.return_to");

        try {
            verification = manager.verify(receivingURL, oidParams,
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

        AuthSuccess authSuccess =
                        (AuthSuccess) verification.getAuthResponse();
        String rolesValue = "";
        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
            FetchResponse fetchResp = null;
            try {
                fetchResp = (FetchResponse) authSuccess.getExtension(
                        AxMessage.OPENID_NS_AX);
            } catch (MessageException e) {
                logger.debug("Failed to fetch extended result: " +
                        e.getMessage());
                return false;
            }
            rolesValue = fetchResp.getAttributeValue("attr1");
        } else {
            logger.debug("No such extension.");
        }

        String[] identifier = verified.getIdentifier().split("/");
        String userName = identifier[identifier.length -1];
        hReq.setAttribute("lada.user.roles", rolesValue);
        hReq.setAttribute("lada.user.name", userName);
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
        enabled = !properties.getProperty("enabled",
                "true").toLowerCase().equals("false");

        manager = new ConsumerManager();
        /* We probably want to implement our own association store to keep
         * associations persistent. */
        manager.setAssociations(new InMemoryConsumerAssociationStore());
        manager.setNonceVerifier(new SessionNonceVerifier(sessionTimeout * 60));
        manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
        discoveryDone = discoverServer();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException, ServletException
    {
        if (!enabled) {
            /* If we are not enabled we pass everything through */
            logger.debug("OpenID filter disabled. Passing through.");
            chain.doFilter(req, resp);
            return;
        }

        HttpServletRequest hReq = (HttpServletRequest) req;
        HttpServletResponse hResp = (HttpServletResponse) resp;
        if (!discoveryDone) {
            discoveryDone = discoverServer();
        }
        if (discoveryDone) {
            if (checkOpenIDHeader(req))
            {
                /* Successfully authenticated. Through Header */
                chain.doFilter(req, resp);
                return;
            } else if (checkOpenIDQuery(req)) {
                /* Successfully authenticated. Through Query parameters.*/
                hResp.addHeader(oidHeader, hReq.getQueryString().replace(
                            "is_return=true",""));
                chain.doFilter(req, resp);
                return;
            }
        }
        /* Authentication failure */
        String authRequestURL = "Error communicating with openid server";
        int errorCode = 698;
        if (discoveryDone) {
            /* Parse the parameters to a map for openid4j */
            ParameterList params = splitParams(hReq.getQueryString());
            String returnToUrl;
            if (params == null) {
                logger.debug("Failed to get any parameters from url.");
                hResp.reset();
                hResp.setStatus(401);
                hResp.getOutputStream().print("{\"success\":false,\"message\":\"" + errorCode + "\",\"data\":" +
                        "\"No return url provided!\",\"errors\":{},\"warnings\":{}," +
                        "\"readonly\":false,\"totalCount\":0}");
                hResp.getOutputStream().flush();
                return;
            } else {
                returnToUrl = params.getParameterValue("return_to");
            }
            try {
                AuthRequest authReq = manager.authenticate(discovered,
                        returnToUrl);
                // Fetch the role attribute
                FetchRequest fetch = FetchRequest.createFetchRequest();

                fetch.addAttribute("attr1",
                        "http://axschema.org/person/role",
                        true, 0);
                // attach the extension to the authentication request
                authReq.addExtension(fetch);

                authRequestURL = authReq.getDestinationUrl(true);
                errorCode = 699;
            } catch (MessageException e) {
                logger.debug("Failed to create the Authentication request: " +
                        e.getMessage());
            } catch (ConsumerException e) {
                logger.debug("Error in consumer manager: " +
                        e.getMessage());
            }
        }
        hResp.reset();
        hResp.setStatus(401);
        hResp.getOutputStream().print("{\"success\":false,\"message\":\"" + errorCode + "\",\"data\":" +
                "\"" + authRequestURL + "\",\"errors\":{},\"warnings\":{}," +
                "\"readonly\":false,\"totalCount\":0}");
        hResp.getOutputStream().flush();
    }
    @Override
    public void destroy()
    {
    }
};
