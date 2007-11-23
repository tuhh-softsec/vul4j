package net.webassembletool.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.webassembletool.Context;
import net.webassembletool.Driver;


/**
 * Retrieves an HTML fragment from the provider application and inserts it into
 * the page.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class IncludeBlockTag extends BodyTagSupport {
	private String name;
	private String page;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int doEndTag() throws JspException {
		Context.retrieveFromSession((HttpServletRequest) pageContext.getRequest());
		try {
			Driver.getInstance().renderBlock(page, name, pageContext.getOut());
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
}
