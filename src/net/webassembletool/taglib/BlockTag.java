package net.webassembletool.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This tag generates HTML comments to define the position of a block.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class BlockTag extends BodyTagSupport {
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
	    pageContext.getOut().print("<!--$endblock$" + name + "$-->");
	} catch (IOException e) {
	    throw new JspException(e);
	}
	return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
	try {
	    pageContext.getOut().print("<!--$beginblock$" + name + "$-->");
	} catch (IOException e) {
	    throw new JspException(e);
	}
	return EVAL_BODY_INCLUDE;
    }
}
