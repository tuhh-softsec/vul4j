package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * TODO Type javadoc
 * 
 * @author Cedric BRANDES
 */
public class ErrorDefinitionTag extends BodyTagSupport {

    private static final long serialVersionUID = -4466967947717753242L;

    private Integer errorCode;

    public Integer getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
	this.errorCode = errorCode;
    }

    @Override
    public int doAfterBody() throws JspException {
	ErrorManageableTag parent = (ErrorManageableTag) getParent();
	String value = getBodyContent().getString();
	parent.getErrorMap().put(errorCode, value);
	return SKIP_BODY;
    }
}
