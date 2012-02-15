package org.esigate.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.esigate.servlet.HttpRequestImpl;
import org.esigate.servlet.HttpResponseImpl;
import org.esigate.vars.VariablesResolver;

/**
 * Generates an HTML "base" tag pointing to a page inside the provider
 * application.<br />
 * This tag is an alternative to using the ProxyServlet.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class IncludeBaseTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String page;
	private String provider;

	@Override
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {

			String baseURL = DriverUtils.getBaseUrl(provider, HttpRequestImpl
					.wrap((HttpServletRequest) this.pageContext.getRequest()),
					HttpResponseImpl
							.wrap((HttpServletResponse) this.pageContext
									.getResponse()));

			String strpage = VariablesResolver.replaceAllVariables(page);
			out.write("<base href=\"" + baseURL + strpage + "\" />");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getPage() {
		return page;
	}

	public String getProvider() {
		return provider;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
