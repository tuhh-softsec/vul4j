package org.esigate.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.esigate.HttpErrorPage;
import org.esigate.servlet.HttpRequestImpl;
import org.esigate.servlet.HttpResponseImpl;

/**
 * Retrieves an XML fragment from the provider application and inserts it into
 * the page. May optionally evaluate XPath expressions and apply XSLT templates
 * on retrieved fragment before insert.
 * 
 * @author Stanislav Bernatskyi
 * @author Sylvain Sicard
 * 
 */
public class IncludeXmlTag extends BodyTagSupport implements ReplaceableTag {
	private static final long serialVersionUID = 1L;
	private boolean parseAbsoluteUrl = true;
	private String provider;
	private Map<String, String> replaceRules = new HashMap<String, String>();
	private String source;
	private String template;
	private String xpath;

	@Override
	public int doEndTag() throws JspException {
		if (parseAbsoluteUrl) {
			String baseUrl = DriverUtils.getBaseUrl(provider, HttpRequestImpl
					.wrap((HttpServletRequest) pageContext.getRequest()),
					HttpResponseImpl.wrap((HttpServletResponse) pageContext
							.getResponse()));
			if (replaceRules == null) {
				replaceRules = new HashMap<String, String>();
			}
			int baseUrlEnd = baseUrl.indexOf('/', baseUrl.indexOf("//") + 2);
			if (baseUrlEnd > 0) {
				baseUrl = baseUrl.substring(0, baseUrlEnd);
			}
			replaceRules.put("href=(\"|')/(.*)(\"|')", "href=$1" + baseUrl
					+ "/$2$3");
			replaceRules.put("src=(\"|')/(.*)(\"|')", "src=$1" + baseUrl
					+ "/$2$3");
		}
		try {
			if (xpath != null && template == null) {
				DriverUtils.renderXpath(provider, source, xpath, pageContext,
						replaceRules);
			} else if (template != null && xpath == null) {
				DriverUtils.renderXml(provider, source, template, pageContext,
						replaceRules);
			} else {
				throw new JspException(
						"One and only one of the attributes \"xpath\" or \"template\" must be defined");
			}
			return EVAL_BODY_INCLUDE;
		} catch (HttpErrorPage e) {
			throw new JspException(e);
		}
	}

	public String getProvider() {
		return provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esigate.taglib.IReplaceableTag#getReplaceRules()
	 */
	public Map<String, String> getReplaceRules() {
		return replaceRules;
	}

	public String getSource() {
		return source;
	}

	public String getTemplate() {
		return template;
	}

	public String getXpath() {
		return xpath;
	}

	public boolean isParseAbsoluteUrl() {
		return parseAbsoluteUrl;
	}

	public void setParseAbsoluteUrl(boolean parseAbsoluteUrl) {
		this.parseAbsoluteUrl = parseAbsoluteUrl;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
}
