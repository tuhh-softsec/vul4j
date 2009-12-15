package net.webassembletool.aggregator;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.esi.EsiRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class AggregatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(AggregatorServlet.class);
	private String provider;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}
		LOG.debug("Aggregating " + relUrl);
		try {
			DriverFactory.getInstance(provider).proxy(relUrl, request,
					response, new AggregateRenderer(request, response),
					new EsiRenderer(request, response));
		} catch (HttpErrorPage e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		provider = config.getInitParameter("provider");
	}
}
