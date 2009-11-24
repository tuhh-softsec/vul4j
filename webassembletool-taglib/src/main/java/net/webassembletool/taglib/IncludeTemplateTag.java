package net.webassembletool.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import net.webassembletool.HttpErrorPage;

/**
 * Retrieves a template from the provider application and inserts it into the
 * page replacing the parameters. Extends AbstractReplaceableTag, so a
 * ReplaceTag can be used inside this tag.
 * 
 * @author Francois-Xavier Bonnet
 */
public class IncludeTemplateTag extends BodyTagSupport implements
		ReplaceableTag, ParametrizableTag, ErrorManageableTag {
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String page = null;
	private String provider;
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, String> replaceRules = new HashMap<String, String>();
	private Map<String, String> parameters = new HashMap<String, String>();
	private String defaultErrorMessage;
	private boolean displayErrorPage = false;

	@Override
	public int doEndTag() throws JspException {
		try {
			DriverUtils.renderTemplate(provider, page, name, pageContext,
					params, replaceRules, parameters);
		} catch (HttpErrorPage e) {
			if (displayErrorPage)
				try {
					pageContext.getOut().append(e.getErrorPageContent());
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			else if (errorMap.containsKey(e.getStatusCode()))
				try {
					pageContext.getOut()
							.append(errorMap.get(e.getStatusCode()));
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			else if (defaultErrorMessage != null)
				try {
					pageContext.getOut().append(defaultErrorMessage);
				} catch (IOException e1) {
					throw new JspException(e1);
				}
			else
				try {
					pageContext.getOut().write(
							e.getStatusCode() + " " + e.getStatusMessage());
				} catch (IOException e1) {
					throw new JspException(e1);
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

	public boolean isDisplayErrorPage() {
		return displayErrorPage;
	}

	public void setDisplayErrorPage(boolean displayErrorPage) {
		this.displayErrorPage = displayErrorPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.webassembletool.taglib.IReplaceableTag#getReplaceRules()
	 */
	public Map<String, String> getReplaceRules() {
		return replaceRules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.webassembletool.taglib.IParameterTag#getParameters()
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @see net.webassembletool.taglib.ErrorManageableTag#getErrorMap()
	 */
	public Map<Integer, String> getErrorMap() {
		return errorMap;
	}

	public String getDefaultMessage() {
		return defaultErrorMessage;
	}

	public void setDefaultMessage(String errorMessage) {
		defaultErrorMessage = errorMessage;
	}
}
