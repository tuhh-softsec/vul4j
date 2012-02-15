package org.esigate.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.esigate.HttpErrorPage;
import org.esigate.servlet.HttpRequestImpl;
import org.esigate.servlet.HttpResponseImpl;

/**
 * Retrieves an HTML fragment from the provider application and inserts it into
 * the page. Extends AbstractReplaceableTag, so a ReplaceTag can be used inside
 * this tag.
 * 
 * @author Francois-Xavier Bonnet
 */
public class IncludeBlockTag extends BodyTagSupport implements ReplaceableTag,
		ParametrizableTag, ErrorManageableTag {
	private static final long serialVersionUID = 1L;
	private boolean addQuery = false;
	private String defaultErrorMessage;
	private boolean displayErrorPage = false;
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	private String name;
	private String page;
	private Map<String, String> parameters = new HashMap<String, String>();
	private boolean parseAbsoluteUrl = true;
	private String provider;
	private Map<String, String> replaceRules = new HashMap<String, String>();

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
			DriverUtils.renderBlock(provider, page, name, pageContext,
					replaceRules, parameters, addQuery);

		} catch (HttpErrorPage re) {
			if (displayErrorPage) {
				try {
					re.render(pageContext.getOut());
				} catch (IOException e) {
					throw new JspException(e);
				}
			} else if (errorMap.containsKey(re.getStatusCode())) {
				try {
					pageContext.getOut().append(
							errorMap.get(re.getStatusCode()));
				} catch (IOException e) {
					throw new JspException(e);
				}
			} else if (defaultErrorMessage != null) {
				try {
					pageContext.getOut().append(defaultErrorMessage);
				} catch (IOException e) {
					throw new JspException(e);
				}
			} else {
				try {
					pageContext.getOut().write(
							re.getStatusCode() + " " + re.getStatusMessage());
				} catch (IOException e) {
					throw new JspException(e);
				}
			}
		}
		name = null;
		page = null;
		provider = null;
		errorMap = new HashMap<Integer, String>();
		replaceRules = new HashMap<String, String>();
		parameters = new HashMap<String, String>();
		displayErrorPage = false;
		errorMap = new HashMap<Integer, String>();
		defaultErrorMessage = null;
		return Tag.EVAL_PAGE;
	}

	public String getDefaultMessage() {
		return defaultErrorMessage;
	}

	/**
	 * @see org.esigate.taglib.ErrorManageableTag#getErrorMap()
	 */
	public Map<Integer, String> getErrorMap() {
		return errorMap;
	}

	public String getName() {
		return name;
	}

	public String getPage() {
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.esigate.taglib.IParameterTag#getParameters()
	 */
	public Map<String, String> getParameters() {
		return parameters;
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

	public boolean isAddQuery() {
		return addQuery;
	}

	public boolean isDisplayErrorPage() {
		return displayErrorPage;
	}

	public boolean isParseAbsoluteUrl() {
		return parseAbsoluteUrl;
	}

	public void setAddQuery(boolean addQuery) {
		this.addQuery = addQuery;
	}

	public void setDefaultMessage(String errorMessage) {
		defaultErrorMessage = errorMessage;
	}

	public void setDisplayErrorPage(boolean displayErrorPage) {
		this.displayErrorPage = displayErrorPage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setParseAbsoluteUrl(boolean parseAbsoluteUrl) {
		this.parseAbsoluteUrl = parseAbsoluteUrl;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
