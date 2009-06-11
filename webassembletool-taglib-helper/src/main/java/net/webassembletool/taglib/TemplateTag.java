package net.webassembletool.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This tag generates HTML comments to define a template.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class TemplateTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public int doEndTag() throws JspException {
	try {
	    pageContext.getOut().print("<!--$endtemplate$" + name + "$-->");
	} catch (IOException e) {
	    throw new JspException(e);
	}
	return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
	try {
	    pageContext.getOut().print("<!--$begintemplate$" + name + "$-->");
	} catch (IOException e) {
	    throw new JspException(e);
	}
	return EVAL_BODY_INCLUDE;
    }
}
