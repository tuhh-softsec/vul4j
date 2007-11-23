package net.webassembletool.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.webassembletool.Context;
import net.webassembletool.Driver;


/**
 * Retrieves a template from the provider application and inserts it into the
 * page replacing the parameters.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class IncludeTemplateTag extends BodyTagSupport {
	private String name = null;
	private String page = null;
	private Map<String, String> params = new HashMap<String, String>();
	Properties prop = null;
	public int doEndTag() throws JspException {
		Context.retrieveFromSession((HttpServletRequest) pageContext.getRequest());
		try {
			Driver.getInstance().renderTemplate(page, name, pageContext.getOut(), params);
		} catch (IOException e) {
			throw new JspException(e);
		}
		name = null;
		page = null;
		params = new HashMap<String, String>();
		return EVAL_PAGE;
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
}
