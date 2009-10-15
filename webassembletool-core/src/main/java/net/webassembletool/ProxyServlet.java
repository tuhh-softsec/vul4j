package net.webassembletool;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class ProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(ProxyServlet.class);
	private String provider;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}
		LOG.debug("Proxying " + relUrl);
		try {
			DriverFactory.getInstance(provider)
					.proxy(relUrl, request, response);
		} catch (HttpErrorPage e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		provider = config.getInitParameter("provider");
	}
}
