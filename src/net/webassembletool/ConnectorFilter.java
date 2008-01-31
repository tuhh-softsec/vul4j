package net.webassembletool;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This handles the user name and locale passed as parameters by the driver and
 * overrides getRemoteUser and getLocale methods from the request in order for
 * the application to retrieve them.
 * 
 * @author François-Xavier Bonnet
 */
public class ConnectorFilter implements Filter {
    public void destroy() {
	// Nothing to do
    }

    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain filterChain) throws IOException, ServletException {
	FilteredRequest filteredRequest = new FilteredRequest(
		(HttpServletRequest) request);
	filterChain.doFilter(filteredRequest, response);
    }

    public void init(FilterConfig arg0) throws ServletException {
	// Nothing to do
    }
}
