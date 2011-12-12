package org.esigate.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.aggregator.AggregateRenderer;
import org.esigate.esi.EsiRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class AggregatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AggregatorServlet.class);
	private String provider;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}
		LOG.debug("Aggregating " + relUrl);
		try {
			DriverFactory.getInstance(provider).proxy(relUrl, HttpRequestImpl.wrap(request), HttpResponseImpl.wrap(response), new AggregateRenderer(), new EsiRenderer());
		} catch (HttpErrorPage e) {
			response.setStatus(e.getStatusCode());
			response.getWriter().write(e.getErrorPageContent());
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		provider = config.getInitParameter("provider");
	}
}
