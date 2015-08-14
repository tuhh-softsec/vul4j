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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
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

/** ServletFilter used for Shibboleth authentification. */
@WebFilter("/*")
public class ShibbolethFilter implements Filter {

    private static final String CONFIG_FILE = "/shibboleth.properties";

    private String applicationId = "default";


    @Inject
    private Logger logger = Logger.getLogger(ShibbolethFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
        //applicationId = properties.getProperty("applicationId");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Unsupported request!");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Unsupported request!");
        }

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        //Enumeration<String> headers = httpRequest.getHeaderNames();
        //while(headers.hasMoreElements()) {
        //    String name = headers.nextElement();
        //    logger.debug("key: " + name + " value: " + httpRequest.getHeader(name));
        //}

        String user = httpRequest.getHeader("X-SHIB-user");
        String roles = httpRequest.getHeader("X-SHIB-roles");
        //String appId = httpRequest.getHeader("X_SHIB-applicationId");

        //if (!applicationId.equals(appId)) {
        //        httpResponse.reset();
        //        httpResponse.setStatus(401);
        //        httpResponse.getOutputStream().print("{\"success\":false,\"message\":\"698\",\"data\":" +
        //                "\"Not authenticated via the Lada application!\",\"errors\":{},\"warnings\":{}," +
        //                "\"readonly\":false,\"totalCount\":0}");
        //        httpResponse.getOutputStream().flush();
        //        return;
        //}

        if (user == null || "".equals(user)) {
                httpResponse.reset();
                httpResponse.setStatus(401);
                httpResponse.getOutputStream().print("{\"success\":false,\"message\":\"698\",\"data\":" +
                        "\"No valid user found!\",\"errors\":{},\"warnings\":{}," +
                        "\"readonly\":false,\"totalCount\":0}");
                httpResponse.getOutputStream().flush();
                return;
        }

        List<String> rolesValue = extractRoles(roles);
        if (roles == null || "".equals(roles) ||
            rolesValue == null || rolesValue.isEmpty()) {
                httpResponse.reset();
                httpResponse.setStatus(401);
                httpResponse.getOutputStream().print("{\"success\":false,\"message\":\"698\",\"data\":" +
                        "\"No valid role found!\",\"errors\":{},\"warnings\":{}," +
                        "\"readonly\":false,\"totalCount\":0}");
                httpResponse.getOutputStream().flush();
                return;
        }

        String roleAttribute = "";
        for (String r : rolesValue) {
            roleAttribute += r + ",";
        }
        roleAttribute = roleAttribute.substring(0, roleAttribute.length() - 2);
        httpRequest.setAttribute("lada.user.roles", rolesValue);
        httpRequest.setAttribute("lada.user.name", user);

        chain.doFilter(request, response);
        return;
    }

    @Override
    public void destroy() {

    }

    private List<String> extractRoles(String roles) {
        LdapName ldap;
        try {
            ldap = new LdapName("");
            String[] groupStrings = roles.split(";");
            for (int i = 0; i < groupStrings.length; i++) {
                String[] items = groupStrings[i].trim().split(",");
                for (int j = 0; j < items.length; j++) {
                    ldap.add(items[j]);
                }
            }
            List<Rdn> rdns = ldap.getRdns();
            List<String> groups = new ArrayList<String>();
            for (Rdn rdn: rdns) {
               String value = (String)rdn.getValue();
               if (rdn.getType().equals("cn") &&
                   !"groups".equals(rdn.getValue().toString())) {
                   groups.add(value);
               }
            }
            return groups;
        } catch (InvalidNameException e) {
            logger.debug("ShibbolethFilter failed!", e);
            return null;
        }
    }

}
