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
import org.esigate.servlet.HttpServletMediator;

/**
 * Retrieves a template from the provider application and inserts it into the
 * page replacing the parameters. Extends AbstractReplaceableTag, so a
 * ReplaceTag can be used inside this tag.
 * 
 * @author Francois-Xavier Bonnet
 */
public class IncludeTemplateTag extends BodyTagSupport implements ReplaceableTag, ParametrizableTag, ErrorManageableTag {
	private static final long serialVersionUID = 1L;
	private String defaultErrorMessage;
	private boolean displayErrorPage = false;
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	private String name = null;
	private String page = null;
	private Map<String, String> parameters = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private String provider;
	private Map<String, String> replaceRules = new HashMap<String, String>();

	@Override
	public int doEndTag() throws JspException {
		try {
			DriverUtils.renderTemplate(provider, page, name, pageContext, params, replaceRules, parameters);
		} catch (HttpErrorPage e) {
			if (displayErrorPage) {
				try {
					new HttpServletMediator((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext.getServletContext()).sendResponse(e
							.getHttpResponse());
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			} else if (errorMap.containsKey(e.getHttpResponse().getStatusLine().getStatusCode())) {
				try {
					pageContext.getOut().append(errorMap.get(e.getHttpResponse().getStatusLine().getStatusCode()));
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			} else if (defaultErrorMessage != null) {
				try {
					pageContext.getOut().append(defaultErrorMessage);
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			} else {
				try {
					pageContext.getOut().write(e.getHttpResponse().getStatusLine().getStatusCode() + " " + e.getHttpResponse().getStatusLine().getReasonPhrase());
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			}
		}
		name = null;
		page = null;
		params = new HashMap<String, String>();
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

	public Map<String, String> getParams() {
		return params;
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

	public boolean isDisplayErrorPage() {
		return displayErrorPage;
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

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

}
