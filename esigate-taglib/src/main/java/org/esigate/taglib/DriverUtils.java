/**
 * 
 */
package org.esigate.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.servlet.HttpRequestImpl;
import org.esigate.servlet.HttpResponseImpl;

/**
 * Utility class used by all tags to access to the Driver
 * 
 * @author Francois-Xavier Bonnet
 */
public class DriverUtils {
	public final static String getBaseUrl(String provider) {
		return DriverFactory.getInstance(provider).getConfiguration()
				.getBaseURL();
	}

	public final static void renderBlock(String provider, String page,
			String name, PageContext pageContext,
			Map<String, String> replaceRules, Map<String, String> parameters,
			boolean copyOriginalRequestParameters) throws JspException,
			HttpErrorPage {
		try {
			Driver driver = DriverFactory.getInstance(provider);
			driver.renderBlock(page, name, pageContext.getOut(),
					HttpRequestImpl.wrap((HttpServletRequest) pageContext
							.getRequest()), HttpResponseImpl
							.wrap((HttpServletResponse) pageContext
									.getResponse()), replaceRules, parameters,
					copyOriginalRequestParameters);
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	public final static void renderEsi(String provider, String source,
			PageContext pageContext) throws JspException, HttpErrorPage {
		try {
			Driver driver = DriverFactory.getInstance(provider);
			driver.renderEsi(source, pageContext.getOut(), HttpRequestImpl
					.wrap((HttpServletRequest) pageContext.getRequest()),
					HttpResponseImpl.wrap((HttpServletResponse) pageContext
							.getResponse()));
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	public final static void renderTemplate(String provider, String page,
			String name, PageContext pageContext, Map<String, String> params,
			Map<String, String> replaceRules, Map<String, String> parameters)
			throws JspException, HttpErrorPage {
		try {
			Driver driver = DriverFactory.getInstance(provider);
			boolean propagateJsessionId = ((HttpServletResponse) pageContext
					.getResponse()).encodeURL("/").contains("jsessionid");
			driver.renderTemplate(page, name, pageContext.getOut(),
					HttpRequestImpl.wrap((HttpServletRequest) pageContext
							.getRequest()), HttpResponseImpl
							.wrap((HttpServletResponse) pageContext
									.getResponse()), params, replaceRules,
					parameters, propagateJsessionId);
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	public final static void renderXml(String provider, String source,
			String template, PageContext pageContext) throws JspException,
			HttpErrorPage {
		try {
			Driver driver = DriverFactory.getInstance(provider);
			driver.renderXml(source, template, pageContext.getOut(),
					HttpRequestImpl.wrap((HttpServletRequest) pageContext
							.getRequest()), HttpResponseImpl
							.wrap((HttpServletResponse) pageContext
									.getResponse()));
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	public final static void renderXpath(String provider, String source,
			String xpath, PageContext pageContext) throws JspException,
			HttpErrorPage {
		try {
			Driver driver = DriverFactory.getInstance(provider);
			driver.renderXpath(source, xpath, pageContext.getOut(),
					HttpRequestImpl.wrap((HttpServletRequest) pageContext
							.getRequest()), HttpResponseImpl
							.wrap((HttpServletResponse) pageContext
									.getResponse()));
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	private DriverUtils() {

	}
}
