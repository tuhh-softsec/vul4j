package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 'errorDefinition' JSP tag.
 * <p>
 * Displays the body's content of retrieved resource instead of the error
 * message in case when error happens while retrieving resource and error code
 * matches one specified by tag.
 * <p>
 * <b>Note</b>: This tag can only be used inside a includeBlock or
 * includeTemplate tag
 * <p>
 * Parameters:
 * <ul>
 * <li><b>errorCode</b> - defines integer error code for matching
 * </ul>
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
