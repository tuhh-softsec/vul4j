package net.webassembletool;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to proxy requests from a remote application.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class ProxyServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getServletPath();
		if (request.getPathInfo() != null)
			relUrl += request.getPathInfo();
		Driver.getInstance().renderResource(relUrl, request, response);
	}
}
