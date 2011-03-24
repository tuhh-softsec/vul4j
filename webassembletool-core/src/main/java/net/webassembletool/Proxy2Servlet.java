package net.webassembletool;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class Proxy2Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Proxy2Servlet.class);
	private String driver;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}
		LOG.debug("Proxying " + relUrl);
		try {
			DriverFactory.getInstance(driver).proxy2(relUrl, request, response);
		} catch (HttpErrorPage e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init() {
		driver = getServletConfig().getInitParameter("provider");
	}
}
