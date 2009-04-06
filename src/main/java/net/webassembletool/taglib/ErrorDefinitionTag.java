package net.webassembletool.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import org.apache.commons.logging.LogFactory;

/**
 * 'errorDefinition' JSP tag.
 * <p>
 * Displays the body's content of retrieved resource instead of the error message in case when error happens while retrieving resource and error code matches one specified by tag.
 * <p>
 * <b>Note</b>: This tag can only be used inside a includeBlock or includeTemplate tag
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
        ErrorManageableTag parent = getFirstAvailableParent();
        if (parent == null) {
            LogFactory.getLog(ErrorDefinitionTag.class).warn("No parent found, skipping tag");
            return Tag.SKIP_BODY;
        }
        String value = getBodyContent().getString();
        parent.getErrorMap().put(errorCode, value);
        return Tag.SKIP_BODY;
    }

    private ErrorManageableTag getFirstAvailableParent() {
        ErrorManageableTag parent = null;
        if (getParent() instanceof ErrorManageableTag)
            parent = (ErrorManageableTag) getParent();
        else if (getParent() instanceof IfTag && getParent().getParent() instanceof ErrorManageableTag)
            parent = (ErrorManageableTag) getParent().getParent();
        return parent;
    }
}
