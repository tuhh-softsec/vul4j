package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Only used inside a tag that implements IParameterTag. This tag add a request
 * parameter to the parent tag, which will be applied on render.
 * 
 * <hr>
 * 
 * @author Cedric Brandes, 27 juin 08
 */
public class ParameterTag extends BodyTagSupport {
    private static final long serialVersionUID = 1L;
    private String parameter;

    public String getParameter() {
	return parameter;
    }

    /**
     * Sets the parameter to add to the request.
     * 
     * @param parameterName Name of the parameter
     */
    public void setParameter(String parameterName) {
	this.parameter = parameterName;
    }

    /**
     * Add a new request parameter to the parent tag. The request parameter is
     * the parameter parameter, the value of the parameter is the content of the
     * body
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
     */
    @Override
    public int doAfterBody() throws JspException {
	ParametrizableTag parent = (ParametrizableTag) getParent();
	String value = getBodyContent().getString();
	parent.getParameters().put(parameter, value);
	return SKIP_BODY;
    }
}
