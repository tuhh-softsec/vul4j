package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * TODO Type javadoc
 * 
 * @author Cedric BRANDES
 */
public class DefaultErrorTag extends BodyTagSupport {

    private static final long serialVersionUID = -4466967947717753242L;

    @Override
    public int doAfterBody() throws JspException {
	ErrorManageableTag parent = (ErrorManageableTag) getParent();
	String value = getBodyContent().getString();
	parent.setDefaultMessage(value);
	return SKIP_BODY;
    }
}
