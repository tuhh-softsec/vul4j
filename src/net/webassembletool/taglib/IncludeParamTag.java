package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Replaces a parameter in a template by the content of its body. This tag can
 * only be used inside an IncludeTemplate tag.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class IncludeParamTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String name;

    @Override
    public int doEndTag() throws JspException {
	if (getBodyContent() != null) {
	    String bodyString = getBodyContent().getString();
	    IncludeTemplateTag templateTag = (IncludeTemplateTag) getParent();
	    templateTag.getParams().put(name, bodyString);
	}
	return EVAL_PAGE;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
